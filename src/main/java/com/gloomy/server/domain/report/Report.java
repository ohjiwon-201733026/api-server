package com.gloomy.server.domain.report;

import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name="report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed_id;

    private String reportCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user_id;

    public Report(Feed feed_id, User user_id, String reportCategory) {
        this.feed_id = feed_id;
        this.reportCategory = reportCategory;
        this.user_id = user_id;
    }

    public static Report of(Feed feed_id,User user_id, String reportCategory){
        return new Report(feed_id,user_id,reportCategory);
    }

    public Long getId() {
        return id;
    }

    public Feed getFeed_id() {
        return feed_id;
    }

    public String getReportCategory() {
        return reportCategory;
    }

    public User getUser_id() {
        return user_id;
    }
}