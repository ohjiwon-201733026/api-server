package com.gloomy.server.application.feed;

import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.feed.Category;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {
    Page<Feed> findByUserId(Pageable pageable, User userId);

    Page<Feed> findAll(Pageable pageable);

    Page<Feed> findByStatusOrderByCreatedAtDesc(Pageable pageable, Status status);

    Page<Feed> findByStatusAndCategoryOrderByCreatedAtDesc(Pageable pageable, Status status, Category category);

    @Query(value = "select * from feed f" +
            "   left outer join (select feed_id as id, count(*) as like_count from feed_like group by id order by count(id)) as fl" +
            "   on f.id = fl.id" +
            "   where f.status = :#{#status?.getCode()}" +
            "   order by fl.like_count desc, f.id desc", nativeQuery = true)
    Page<Feed> findByStatusOrderByLikeCount(Pageable pageable, Status status);

    @Query(value = "select * from feed f" +
            "   left outer join (select feed_id as id, count(*) as like_count from feed_like group by id order by count(id)) as fl" +
            "   on f.id = fl.id" +
            "   where f.status = :#{#status?.getCode()}" +
            "   and f.category = :#{#category?.getCode()}" +
            "   order by fl.like_count desc, f.id desc", nativeQuery = true)
    Page<Feed> findByStatusAndCategoryOrderByLikeCount(Pageable pageable, Status status, Category category);

    @Query(value = "select f from Feed f where f not in (select r.feedId from Report r where r.userId = :userId) and f.status = :status order by f.id desc")
    Page<Feed> findByStatusWithReportOrderByCreatedDesc(Pageable pageable, @Param("userId") User userId, @Param("status") Status status);

    @Query(value = "select f from Feed f where f not in (select r.feedId from Report r where r.userId = :userId) and f.status = :status and f.category = :category order by f.id desc")
    Page<Feed> findByStatusAndCategoryWithReportOrderByCreatedDesc(Pageable pageable, @Param("userId") User userId, @Param("status") Status status, @Param("category") Category category);

    @Query(value = "select f from feed f" +
            "   left outer join (select feed_id as id, count(*) as like_count from feed_like group by id order by count(id)) as fl" +
            "   on f.id = fl.id" +
            "   where f.id not in (select r.feed_id from report r where r.userId = :userId)" +
            "   and f.status = :#{#status?.getCode()}" +
            "   order by fl.like_count desc, f.id desc", nativeQuery = true)
    Page<Feed> findByStatusWithReportOrderByLikeCount(Pageable pageable, @Param("userId") Long user, @Param("status") Status status);

    @Query(value = "select f from feed f" +
            "   left outer join (select feed_id as id, count(*) as like_count from feed_like group by id order by count(id)) as fl" +
            "   on f.id = fl.id" +
            "   where f.id not in (select r.feed_id from report r where r.userId = :userId)" +
            "   and f.status = :#{#status?.getCode()}" +
            "   and f.category = :#{#category?.getCode()}" +
            "   order by fl.like_count desc, f.id desc", nativeQuery = true)
    Page<Feed> findByStatusAndCategoryWithReportOrderByLikeCount(Pageable pageable, @Param("userId") Long user, @Param("status") Status status, @Param("category") Category category);
}
