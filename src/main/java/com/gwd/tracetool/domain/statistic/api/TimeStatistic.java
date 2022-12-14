package com.gwd.tracetool.domain.statistic.api;

import lombok.Getter;

/**
 * -lt : <
 * -gt : >
 * -le : <=
 * -ge : >=
 * -eq : ==
 **/

@Getter
public class TimeStatistic {    // response 응답 시간에 따라 통계
    private Integer gt100Ms;
    private Integer gt500Ms;
    private Integer gt1000Ms;
    private Integer gt5s;
    private Integer gt10s;
    private Integer gt30s;
    private Integer gt60s;
    private Integer gt120s;
    private Integer gt300s;
    private Integer gt600s;
    private Integer gt1200s;
    private Integer le1200s;

    public TimeStatistic() {
        gt100Ms = 0;
        gt500Ms = 0;
        gt1000Ms = 0;
        gt5s = 0;
        gt10s = 0;
        gt30s = 0;
        gt60s = 0;
        gt120s = 0;
        gt300s = 0;
        gt600s = 0;
        gt1200s = 0;
        le1200s = 0;
    }

    public void increaseTimeCount(String msTime) {
        increaseTimeCount(Integer.parseInt(msTime.replace("ms", "")));
    }

    public void increaseTimeCount(int msTime) {
        if (msTime < 100) {
            gt100Ms++;
        } else if (msTime < 500) {
            gt500Ms++;
        } else if (msTime < 1000) {
            gt1000Ms++;
        } else if (msTime < 1000 * 5) {
            gt5s++;
        } else if (msTime < 1000 * 10) {
            gt10s++;
        } else if (msTime < 1000 * 30) {
            gt30s++;
        } else if (msTime < 1000 * 60) {
            gt60s++;
        } else if (msTime < 1000 * 120) {
            gt120s++;
        } else if (msTime < 1000 * 300) {
            gt300s++;
        } else if (msTime < 1000 * 600) {
            gt600s++;
        } else if (msTime < 1000 * 1200) {
            gt1200s++;
        } else {
            le1200s++;
        }
    }

}
