package com.lg.hibernate.userguide.entity;

import javax.persistence.Embeddable;

/**
 * Created by liuguo on 2016/9/9.
 */
@Embeddable
public class Name {

    private String first;
    private String middle;
    private String last;

    public Name() {
    }

    public Name(String first, String middle, String last) {
        this.first = first;
        this.middle = middle;
        this.last = last;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
