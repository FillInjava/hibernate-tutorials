package com.lg.hibernate.userguide.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by liuguo on 2016/9/9.
 */
@Entity
@Table(name = "Contact")
public class Contact {

    private Integer id;

    private Name name;

    private String notes;

    private String website;

    private boolean starred;

    public Contact() {
    }

    public Contact(Name name, String notes, String website, boolean starred) {
        this.name = name;
        this.notes = notes;
        this.website = website;
        this.starred = starred;
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment",strategy = "increment")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Embedded
    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name=" + name +
                ", notes='" + notes + '\'' +
                ", website='" + website + '\'' +
                ", starred=" + starred +
                '}';
    }
}
