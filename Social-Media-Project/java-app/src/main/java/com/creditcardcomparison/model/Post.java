package com.creditcardcomparison.model;

import java.util.ArrayList;
import java.util.List;

public class Post {

    /** ------ Instance Variables ------ **/
    private int id;
    private String title = "";
    private String author = "";
    private String content = "";
    private int numOfLikes = 0;
    private List<Comment> comments = new ArrayList<>();
    private boolean likedByUser = false;
    private int numberOfComments = 0;


    /** ------ Constructor ------ **/
    public Post(int id) {
        this.id = id;
    }


    /** ------ Getters ------ **/
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public int getNumOfLikes() {
        return numOfLikes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    /** ------ Setters ------ **/
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setNumOfLikes(int numOfLikes) {
        this.numOfLikes = numOfLikes;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public void setCommentList(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }
}
