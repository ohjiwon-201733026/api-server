package com.gloomy.server.domain.user;

import com.gloomy.server.domain.common.entity.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

import static javax.persistence.GenerationType.IDENTITY;

@Setter
@Getter
@ToString
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    @Embedded
    private Profile profile;

    @Embedded
    private Password password;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Status joinStatus;

//    @OneToMany(mappedBy = "userId")
//    private List<Feed> feeds=new ArrayList<>();
//
//    @OneToMany(mappedBy = "userId")
//    private List<Comment> comments=new ArrayList<>();

//    @Embedded
//    private Token token;

    protected User() {
    }

    private User(String email, Profile profile, Password password) {
        this.email = email;
        this.profile = profile;
        this.role = Role.USER;
        this.password = password;
    }

    private User(String email, Profile profile, Password password, Sex sex, LocalDate dateOfBirth, Status joinStatus) {
        this.email = email;
        this.profile = profile;
        this.role = Role.USER;
        this.password = password;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.joinStatus = joinStatus;
    }

    public static User of(String email, String name, Password password,
                          Sex sex, int year, int month, int day, Status status) {
        return new User(email, Profile.from(name), password, sex, LocalDate.of(year, month, day), status);
    }

    public static User of(String email, String name, Password password) {
        return new User(email, Profile.from(name), password);
    }

    public static User of(String email, String name) {
        return new User(email, Profile.from(name), null);
    }

    boolean matchesPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return password.matchesPassword(rawPassword, passwordEncoder);
    }

    /**
     * change*
     */
    public void changeId(Long id) {
        this.id = id;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeSex(Sex sex) {
        this.sex = sex;
    }

    //    public void changeImage(String image){
//        System.out.println(">>>>>>>>>>"+this.profile);
//        System.out.println(">>>>>>>>>>"+this.profile.getImage());
//        this.profile.getImage().changeImage(image);}
    public void changeDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * 비즈니스 로직
     */
//    public void removeFeed(Long feedId){
//        List<Feed> feeds=this.getFeeds();
//
//        for(Iterator<Feed> itr=feeds.iterator();itr.hasNext();){
//            Feed feed=itr.next();
//            if(feed.getId()==feedId) {
//                itr.remove();
//            }
//        }
//
//
//    }

    /**
     * getter
     */
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getName() {
        return getProfile().getUserName();
    }

    public Password getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
