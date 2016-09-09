package com.lg.hibernate.tutorials;

import java.util.Date;

/**
 * Created by liuguo on 2016/9/9.
 */
public class Event {

    private Long id;

    private String title;
    private Date date;

    public Event() {
        // this form used by Hibernate
    }

    public Event(String title, Date date) {
        // for application use, to create new events
        this.title = title;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
