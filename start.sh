#!/bin/bash

# 0. cd home
cd ~

# 1. install git
GIT=$(which git)
if [ -z "$GIT" ];
then
sudo yum -y install git
fi

# 2. install jdk and configure java
JAVAC=$(which javac)
if [ -z "$JAVAC" ];
then
sudo amazon-linux-extras install java-openjdk11 -y
fi
JAVAC=$(which javac)
READLINK=$(readlink -f $JAVAC)
JAVA_CHECK=$(echo ${READLINK%/*/*})
if [[ -z "$JAVA_HOME" ]];  
then
echo "export JAVA_HOME=${JAVA_CHECK}" | sudo tee /etc/profile
source /etc/profile
fi

# 3. install redis
REDISENABLED=$(systemctl list-units | grep redis-server)
if [ -z "$REDISENABLED" ];
then
sudo yum update
sudo yum -y install gcc make

wget http://download.redis.io/releases/redis-6.0.4.tar.gz
tar xzf redis-6.0.4.tar.gz
cd redis-6.0.4
make

sudo mkdir /etc/redis
sudo mkdir /var/lib/redis
sudo cp src/redis-server src/redis-cli /usr/local/bin/
sudo cp redis.conf /etc/redis

sudo find /etc/redis -name 'redis.conf' -exec sed -i 's/^daemonize no/daemonize yes/g' {} \;
sudo find /etc/redis -name 'redis.conf' -exec sed -i 's/^bind 127.0.0.1/bind 0.0.0.0/g' {} \;
sudo find /etc/redis -name 'redis.conf' -exec sed -i 's/^dir .\//dir \/var\/lib\/redis/g' {} \;
sudo find /etc/redis -name 'redis.conf' -exec sed -i 's/^logfile \"\"/logfile \"\/var\/log\/redis_6379.log\"/g' {} \;

cd /tmp
wget https://raw.github.com/saxenap/install-redis-amazon-linux-centos/master/redis-server

sudo mv redis-server /etc/init.d
sudo chmod 755 /etc/init.d/redis-server

sudo chkconfig --add redis-server
sudo chkconfig --level 345 redis-server on

rm -rf ~/redis-6.0.4*
fi

REDISACTIVE=$(systemctl is-active redis-server.service)
if [ "$REDISACTIVE" != "activating" ];
then
sudo service redis-server start
redis-cli ping
fi

# 4. 80 -> 8080 portfowarding
IPTABLES=$(systemctl is-active iptables)
if [ "$IPTABLES" != "active" ];
then
sudo systemctl stop firewalld
sudo systemctl disable firewalld
sudo systemctl mask --now firewalld

sudo yum install -y iptables-services
sudo systemctl enable iptables
sudo systemctl start iptables

sudo iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080
fi
# 5. allocate swap memory
sudo dd if=/dev/zero of=/swapfile bs=128M count=16
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo swapon -s
sudo sh -c "echo /swapfile swap swap defaults 0 0 >> /etc/fstab"

# 6. git
cd ~
if [ ! -d /home/ec2-user/api-server ];
then
git clone -b develop --single-branch https://github.com/gomingout-project/api-server.git
cd api-server
git config user.name $1
git config user.email $2
fi

# 7. set Korean
LANGKO="ko_KR.UTF-8"
if [ "$LANG" != "$LANGKO" ];
then
sudo localedef -c -i ko_KR -f UTF-8 $LANGKO
sudo localectl set-locale LANG=$LANGKO
sudo shutdown -r now
fi
