package com.gloomy.server.domain.report;

import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@Getter
public class Report {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name="report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feedId;

    @Enumerated
    private ReportCategory reportCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Builder
    public Report(Feed feed_id, User user_id, ReportCategory reportCategory) {
        this.feedId = feed_id;
        this.reportCategory = reportCategory;
        this.userId = user_id;
    }

    public static Report of(Feed feed_id,User user_id, ReportCategory reportCategory){
        return new Report(feed_id,user_id,reportCategory);
    }

    public String getReportCategory(){ return this.reportCategory.toString();}

}