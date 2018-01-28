package com.seasonfif.star.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by lxy on 2018/1/27.
 */

public class Repository implements Serializable{
    public String name;
    public String language;
    public String description;
    public int stargazers_count;
    public int forks_count;
    public int watchers_count;
    public String created_at;
    public String updated_at;
    public User owner;

    public static class User {
        public int id;
        public String login;
        public String name;
        public String company;
        public String created_at;
        public String updated_at;
        public String avatar_url;
        public String gravatar_id;
        public String blog;
        public String bio;
        public String email;
        public String location;
        public String type;
        public boolean site_admin;
        public int public_repos;
        public int public_gists;
        public int followers;
        public int following;
    }
}
