package com.creditcardcomparison.dao;

import com.creditcardcomparison.model.Post;

import java.util.List;

public interface PostDao {

    void makePost(int postingMemberId, String postTitle, String postContent, String postTopic);

    List<Post> getPostsForLoggedOutUser();
    List<Post> getPostsForLoggedInUser(int memberId);

    Post getPostForLoggedOutUser(int postId);

    Post getPostForLoggedInUser(int postId, int memberId);

    Post getRootPostOfCommentThread(int commentId);

    List<Post> getPostsWithSearch(String searchTerm, String searchTopic);

    void addLike(int postId, int memberId);

    void removeLike(int postId, int memberId);

    int getNumOfLikesForPost(int postId);

    boolean isPostLikedByUser(int postId, int memberId);

}
