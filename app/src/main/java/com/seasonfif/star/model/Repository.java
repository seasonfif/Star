package com.seasonfif.star.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * Created by lxy on 2018/1/27.
 */
@Entity
public class Repository implements Serializable{

    @Transient
    private static final long serialVersionUID = -7554628269759806030L;
    @Id
    public long id;
    public String name;
    public String language;
    public String description;
    public int stargazers_count;
    public int forks_count;
    public int watchers_count;
    public String created_at;
    public String updated_at;
    @Transient
    public User owner;

    @Generated(hash = 1428655450)
    public Repository(long id, String name, String language, String description,
            int stargazers_count, int forks_count, int watchers_count,
            String created_at, String updated_at) {
        this.id = id;
        this.name = name;
        this.language = language;
        this.description = description;
        this.stargazers_count = stargazers_count;
        this.forks_count = forks_count;
        this.watchers_count = watchers_count;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    @Generated(hash = 984204935)
    public Repository() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStargazers_count() {
        return this.stargazers_count;
    }

    public void setStargazers_count(int stargazers_count) {
        this.stargazers_count = stargazers_count;
    }

    public int getForks_count() {
        return this.forks_count;
    }

    public void setForks_count(int forks_count) {
        this.forks_count = forks_count;
    }

    public int getWatchers_count() {
        return this.watchers_count;
    }

    public void setWatchers_count(int watchers_count) {
        this.watchers_count = watchers_count;
    }

    public String getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public static class User {
        public int id;
        public String login;
        public String name;
        public String avatar_url;
    }
}