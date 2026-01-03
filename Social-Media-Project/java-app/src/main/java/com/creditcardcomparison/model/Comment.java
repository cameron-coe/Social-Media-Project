package com.creditcardcomparison.model;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    /** ------ Instance Variables ------ **/
    private int id;
    private String author = "";
    private String content = "";
    private String createdOn = "";
    private int numOfLikes = 0;
    private List<Comment> comments = new ArrayList<>();
    private boolean likedByUser = false;


    /** ------ Constructor ------ **/
    public Comment(int id) {
        this.id = id;
    }




    /** ------ Getters ------ **/
    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public int getNumOfLikes() {
        return numOfLikes;
    }

    public List<Comment> getReplies() {
        return comments;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    /** ------ Setters ------ **/
    public void setContent(String content) {
        this.content = content;
    }

    public void setNumOfLikes(int numOfLikes) {
        this.numOfLikes = numOfLikes;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }
}
