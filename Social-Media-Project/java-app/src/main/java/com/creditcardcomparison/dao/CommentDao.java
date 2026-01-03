package com.creditcardcomparison.dao;

import com.creditcardcomparison.model.Comment;

import java.util.List;

public interface CommentDao {

    void addCommentToPost(int parentPostId, int commentingMemberId, String commentContent);

    void addCommentToComment(int rootPostId, int parentCommentId, int commentingMemberId, String commentContent);

    List<Comment> getCommentsOfPostForLoggedOutUser(int postId, int numberOfEmbeddedComments);

    List<Comment> getCommentsOfPostForLoggedInUser(int postId, int memberId, int numberOfEmbeddedComments);

    List<Comment> getCommentsOfCommentForLoggedOutUser(int commentId, int depthRemaining);

    List<Comment> getCommentsOfCommentForLoggedInUser(int commentId, int memberId, int depthRemaining);

    List<Comment> getCommentThread(int commentId);

    void addLike(int commentId, int memberId);

    void removeLike(int commentId, int memberId);

    int getNumOfLikesForComment(int commentId);

    boolean isCommentLikedByUser(int commentId, int memberId);

}
