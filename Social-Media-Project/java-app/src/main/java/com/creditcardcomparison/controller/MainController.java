package com.creditcardcomparison.controller;

import com.creditcardcomparison.dao.*;
import com.creditcardcomparison.http.*;
import com.creditcardcomparison.model.Comment;
import com.creditcardcomparison.model.Member;
import com.creditcardcomparison.model.Post;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;


public class MainController {

    private final HttpMethod GET = HttpMethod.GET;
    private final HttpMethod POST = HttpMethod.POST;
    private HttpResponse response;
    private PgAdminConnection dbConnection  = new PgAdminConnection();
    private TokenManager tokenManager = new TokenManager();


    /** ------ Constructor ------ **/
    public MainController () {

    }


    public String getResponseBody(HttpResponse response) {
        this.response = response;
        response.setHttpStatusCode(HttpStatusCode.SUCCESS_RESPONSE_200_OK);

        if (requestLineMatches(POST, "/sign-up")) {
            return signUp(response);
        }

        if (requestLineMatches(POST, "/login")) {
            return login(response);
        }

        if (requestLineMatches(POST, "/new-post")) {
            return newPost();
        }

        if (requestLineMatches(GET, "/get-posts")) {
            return getPosts();
        }

        if (requestLineMatches(POST, "/get-posts-2")) {
            return getPosts2();
        }

        if (requestLineStartsWith(GET, "/get-post")) {
            try {
                String stringPostId = this.response.getRequest().getArrayOfTarget()[2];
                try {
                    int postId = Integer.parseInt(stringPostId);
                    return getPost(postId);
                } catch (NumberFormatException e) {
                    response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            } catch (IndexOutOfBoundsException e) {
                response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }
        }

        if (requestLineMatches(POST, "/get-post-2")) {
            return getPost2();
        }

        if (requestLineStartsWith(GET, "/get-root-post-of-comment-thread")) {
            try {
                String stringPostId = this.response.getRequest().getArrayOfTarget()[2];
                try {
                    int commentId = Integer.parseInt(stringPostId);
                    return getRootPostOfCommentThread(commentId);
                } catch (NumberFormatException e) {
                    response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            } catch (IndexOutOfBoundsException e) {
                response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }
        }

        if (requestLineMatches(POST, "/get-posts-with-search")) {
            return getPostsWithSearch();
        }

        if (requestLineStartsWith(GET, "/get-comments-for-post")) {
            try {
                String stringParentPostId = this.response.getRequest().getArrayOfTarget()[2];
                try {
                    int intParentPostId = Integer.parseInt(stringParentPostId);
                    return getCommentsForPost(intParentPostId);
                } catch (NumberFormatException e) {
                    response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            } catch (IndexOutOfBoundsException e) {
                response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }
        }

        if (requestLineMatches(POST, "/get-comments-for-post-2")) {
            return getCommentsForPost2();
        }

        if (requestLineStartsWith(GET, "/get-comments-for-comment")) {
            try {
                String stringParentCommentId = this.response.getRequest().getArrayOfTarget()[2];
                try {
                    int intParentCommentId = Integer.parseInt(stringParentCommentId);
                    return getCommentsForComment(intParentCommentId);
                } catch (NumberFormatException e) {
                    response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            } catch (IndexOutOfBoundsException e) {
                response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }
        }

        if (requestLineMatches(POST, "/get-comments-for-comment-2")) {
            return getCommentsForComment2();
        }

        if (requestLineStartsWith(GET, "/get-comment-thread")) {
            try {
                String stringParentCommentId = this.response.getRequest().getArrayOfTarget()[2];
                try {
                    int intParentCommentId = Integer.parseInt(stringParentCommentId);
                    return getCommentThread(intParentCommentId);
                } catch (NumberFormatException e) {
                    response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            } catch (IndexOutOfBoundsException e) {
                response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }
        }

        if (requestLineMatches(POST, "/new-comment-to-post")) {
            return addCommentToPost();
        }

        if (requestLineMatches(POST, "/new-comment-to-comment")) {
            return addCommentToComment();
        }

        if (requestLineMatches(GET, "/tags")) {
            return getTags();
        }

        if (requestLineMatches(POST, "/get-like-data-for-post")) {
            return getLikeDataForPost();
        }

        if (requestLineMatches(POST, "/like-post")) {
            return likePost();
        }

        if (requestLineMatches(POST, "/unlike-post")) {
            return unlikePost();
        }


        if (requestLineMatches(POST, "/get-like-data-for-comment")) {
            return getLikeDataForComment();
        }

        if (requestLineMatches(POST, "/like-comment")) {
            return likeComment();
        }

        if (requestLineMatches(POST, "/unlike-comment")) {
            return unlikeComment();
        }

        // Default Web Page
        return notFound();
    }


    /** --- Helper Methods --- **/
    private boolean requestLineMatches(HttpMethod httpMethod, String path) {
        if (response != null) {
            if (this.response.getRequest().getMethod() == httpMethod) {
                if (this.response.getRequest().getTarget().equals(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean requestLineStartsWith(HttpMethod httpMethod, String path) {
        if (response != null) {
            if (this.response.getRequest().getMethod() == httpMethod) {
                if (this.response.getRequest().getTarget().startsWith(path)) {
                    return true;
                }
            }
        }
        return false;
    }


    /** ------ Sign Up Response ------ **/
    private String signUp(HttpResponse servletResponse) {
        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            String username = jsonNode.get("username").asText();
            String password = jsonNode.get("password").asText();

            createNewMember(username, password);

        } catch (JsonMappingException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            return makeResponseJson("\"We're sorry, we ran into an issue signing you up.\"");
        } catch (JsonProcessingException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            return makeResponseJson("\"We're sorry, we ran into an issue signing you up.\"");
        } catch (DuplicateKeyException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            if (e.getMessage().contains("member_username_key")) {
                return makeResponseJson(false, "This username already exists. Please try a new one.");
            }
            else {
                return makeResponseJson("\"Your username or password is already taken. Try a new combo\"");
            }
        }

        return login(servletResponse);
    }

    private void createNewMember(String username, String password) throws DuplicateKeyException, CannotGetJdbcConnectionException, DataIntegrityViolationException {
        MemberDao memberDao = new JdbcMemberDao(dbConnection.getDataSource());
        Member newMember = memberDao.createNewMember(username, password);
        response.setHttpStatusCode(HttpStatusCode.SUCCESS_RESPONSE_201_CREATED);
    }

    /** ------ Login Response ------ **/
    private String login(HttpResponse servletResponse) {
        Member member = null;

        String username = "";

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            username = jsonNode.get("username").asText();
            String password = jsonNode.get("password").asText();
            member = getExistingMember(username, password);

        } catch (JsonMappingException e) {
            servletResponse.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (JsonProcessingException e) {
            servletResponse.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        if (member != null) {
            String token = tokenManager.generateToken(member);

            // Set the token in the response (assuming a method to set response body)
            servletResponse.setResponseBody(
                "{\"token\": \"" + token + "\"," +
                " \"memberId\": \"" + member.getId() + "\"," +
                " \"username\": \"" + username + "\" }"
            );
            servletResponse.setHttpStatusCode(HttpStatusCode.SUCCESS_RESPONSE_200_OK);

            // Optionally, you can also set the token as a cookie
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60); // 60 minutes
            servletResponse.addCookie(cookie);

            return servletResponse.getResponseBody();
        } else {
            servletResponse.setResponseBody("\"Could not login\"");
            return servletResponse.getResponseBody();
        }
    }



    private Member getExistingMember(String username, String password) {
        MemberDao memberDao = new JdbcMemberDao(dbConnection.getDataSource());
        Member member = null;

        try {
            member = memberDao.getMemberByUsernameAndPassword(username, password);
            response.setHttpStatusCode(HttpStatusCode.SUCCESS_RESPONSE_200_OK);
        } catch (CannotGetJdbcConnectionException | DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            e.printStackTrace();
        }

        return member;
    }




    /** ------ New Post ------ **/
    private String newPost() {
        String token = getTokenFromRequest();
        if (!tokenManager.isTokenValid(token)) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED);
            return " { \"response\" : \"Unauthorized\" } ";
        }


        PostDao postDao = new JdbcPostDao(dbConnection.getDataSource());

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int memberId = jsonNode.get("memberId").asInt();
            String postContent = jsonNode.get("content").asText();
            String postTag = jsonNode.get("tag").asText();
            String postTitle = jsonNode.get("title").asText();

            postDao.makePost(memberId, postTitle, postContent, postTag);

        } catch (JsonMappingException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (JsonProcessingException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        return " { \"response\" : \"Post was made.\" } ";
    }


    /** ------ Get posts ------ **/
    private String getPosts() {
        PostDao postDao = new JdbcPostDao(dbConnection.getDataSource());
        List<Post> posts = new ArrayList<>();

        try {
            posts = postDao.getPostsForLoggedOutUser();
        } catch (DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String responseData = postsListToJson(posts);

        return makeResponseJson(responseData);
    }

    private String getPosts2() {
        PostDao postDao = new JdbcPostDao(dbConnection.getDataSource());
        List<Post> posts = new ArrayList<>();

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int memberId = jsonNode.get("memberId").asInt();

            boolean memberIdExists = memberId > 0;

            if (memberIdExists) {
                posts = postDao.getPostsForLoggedInUser(memberId);
            }
            else {
                posts = postDao.getPostsForLoggedOutUser();
            }


        } catch (DataIntegrityViolationException | JsonProcessingException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String responseData = postsListToJson(posts);
        return makeResponseJson(responseData);
    }




    /** ------ Get post by ID ------ **/
    private String getPost(int postId) {
        PostDao postDao = new JdbcPostDao(dbConnection.getDataSource());
        Post post = null;
        try {
            post = postDao.getPostForLoggedOutUser(postId);
        } catch (DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String responseData = postToJson(post);
        return makeResponseJson(responseData);
    }

    private String getPost2() {
        PostDao postDao = new JdbcPostDao(dbConnection.getDataSource());
        Post post = null;

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int postId = jsonNode.get("postId").asInt();
            int memberId = jsonNode.get("memberId").asInt();

            boolean memberIdExists = memberId > 0;

            if (memberIdExists) {
                post = postDao.getPostForLoggedInUser(postId, memberId);
            }
            else {
                post = postDao.getPostForLoggedOutUser(postId);
            }

        } catch (DataIntegrityViolationException | JsonProcessingException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String responseData = postToJson(post);
        return makeResponseJson(responseData);
    }





    /** ------ Get the root post of a comment thread ------ **/
    private String getRootPostOfCommentThread(int commentId) {
        PostDao postDao = new JdbcPostDao(dbConnection.getDataSource());
        Post post = null;
        try {
            post = postDao.getRootPostOfCommentThread(commentId);
        } catch (DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String responseData = postToJson(post);
        return makeResponseJson(responseData);
    }


    /** ------ Get posts with a search term------ **/
    private String getPostsWithSearch() {
        PostDao postDao = new JdbcPostDao(dbConnection.getDataSource());
        List<Post> posts = new ArrayList<>();

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            String searchTerm = jsonNode.get("searchTerm").asText();
            String searchTag = jsonNode.get("searchTag").asText();

            posts = postDao.getPostsWithSearch(searchTerm, searchTag);
        } catch (JsonProcessingException | DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String responseData = postsListToJson(posts);

        return makeResponseJson(responseData);
    }


    /** ------ Get Tags ------ **/
    private String getTags() {
        TagDao tagDao = new JdbcTagDao(dbConnection.getDataSource());
        String tagArrayString = "[";

        try {
            List<String> tags = tagDao.getTags();

            for(int i = 0; i < tags.size(); i++) {
                tagArrayString += "\"";
                tagArrayString += tags.get(i);
                tagArrayString += "\"";

                // Add comma if this isn't the last tag
                if (i < tags.size() - 1) {
                    tagArrayString += ", ";
                }
            }
            tagArrayString += "]";
        } catch (DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        return makeResponseJson(tagArrayString);
    }

    /** ------ Not Found Response ------ **/
    private String notFound() {
        response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_404_NOT_FOUND);
        return "";
    }


    /** ------ Get Comment Responses ------ **/
    private String getCommentsForPost(int parentPostId) {
        CommentDao commentDao = new JdbcCommentDao(dbConnection.getDataSource());
        List<Comment> comments = new ArrayList<>();

        int numberOfEmbeddedComments = 2;

        try {
            comments = commentDao.getCommentsOfPostForLoggedOutUser(parentPostId, numberOfEmbeddedComments);
        } catch (DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String responseData = commentListToJson(comments);

        return makeResponseJson(responseData);
    }

    private String getCommentsForPost2() {
        CommentDao commentDao = new JdbcCommentDao(dbConnection.getDataSource());
        List<Comment> comments = new ArrayList<>();

        int numberOfEmbeddedComments = 2;

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int postId = jsonNode.get("postId").asInt();
            int memberId = jsonNode.get("memberId").asInt();

            boolean memberIdExists = memberId > 0;

            if (memberIdExists) {
                comments = commentDao.getCommentsOfPostForLoggedInUser(postId, memberId, numberOfEmbeddedComments);
            }
            else {
                comments = commentDao.getCommentsOfPostForLoggedOutUser(postId, numberOfEmbeddedComments);
            }

        } catch (JsonProcessingException | DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String responseData = commentListToJson(comments);
        return makeResponseJson(responseData);
    }


    private String getCommentsForComment(int parentCommentId){
        CommentDao commentDao = new JdbcCommentDao(dbConnection.getDataSource());
        List<Comment> comments = new ArrayList<>();

        int numberOfEmbeddedComments = 0;

        try {
            comments = commentDao.getCommentsOfCommentForLoggedOutUser(parentCommentId, numberOfEmbeddedComments);
        } catch (DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String responseData = commentListToJson(comments);

        return makeResponseJson(responseData);
    }

    private String getCommentsForComment2() {
        CommentDao commentDao = new JdbcCommentDao(dbConnection.getDataSource());
        List<Comment> comments = new ArrayList<>();

        int numberOfEmbeddedComments = 0;

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int commentId = jsonNode.get("commentId").asInt();
            int memberId = jsonNode.get("memberId").asInt();

            boolean memberIdExists = memberId > 0;

            if (memberIdExists) {
                comments = commentDao.getCommentsOfCommentForLoggedInUser(commentId, memberId, numberOfEmbeddedComments);
            }
            else {
                comments = commentDao.getCommentsOfCommentForLoggedOutUser(commentId, numberOfEmbeddedComments);
            }

        } catch (JsonProcessingException | DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String responseData = commentListToJson(comments);
        return makeResponseJson(responseData);
    }

    private String getCommentThread(int commentId){
        CommentDao commentDao = new JdbcCommentDao(dbConnection.getDataSource());
        List<Comment> commentThread = new ArrayList<>();

        try {
            commentThread = commentDao.getCommentThread(commentId);
        } catch (DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String responseData = commentListToJson(commentThread);

        return makeResponseJson(responseData);
    }


    /** ------ Add Comment To A Post ------ **/
    private String addCommentToPost() {
        String token = getTokenFromRequest();
        if (!tokenManager.isTokenValid(token)) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED);
            return " { \"response\" : \"Unauthorized\" } ";
        }

        CommentDao commentDao = new JdbcCommentDao((dbConnection.getDataSource()));

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int parentPostId = jsonNode.get("parentPostId").asInt();
            int memberId = jsonNode.get("memberId").asInt();
            String content = jsonNode.get("content").asText();

            commentDao.addCommentToPost(parentPostId, memberId, content);

        } catch (JsonMappingException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (JsonProcessingException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        return " { \"response\" : \"Comment was made.\" } ";
    }

    /** ------ Add Comment To A Comment ------ **/
    private String addCommentToComment() {
        String token = getTokenFromRequest();
        if (!tokenManager.isTokenValid(token)) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED);
            return " { \"response\" : \"Unauthorized\" } ";
        }

        CommentDao commentDao = new JdbcCommentDao((dbConnection.getDataSource()));

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int parentCommentId = jsonNode.get("parentCommentId").asInt();
            int rootPostId = jsonNode.get("rootPostId").asInt();
            int memberId = jsonNode.get("memberId").asInt();
            String content = jsonNode.get("content").asText();

            commentDao.addCommentToComment(rootPostId, parentCommentId, memberId, content);

        } catch (JsonMappingException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (JsonProcessingException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        return " { \"response\" : \"Comment was made.\" } ";

    }

    /** ------ Get Like Data For Post ------ **/
    private String getLikeDataForPost() {
        String token = getTokenFromRequest();
        if (!tokenManager.isTokenValid(token)) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED);
            return " { \"response\" : \"Unauthorized\" } ";
        }

        PostDao postDao = new JdbcPostDao(dbConnection.getDataSource());

        int numOfLikes = 0;
        boolean isPostLikedByUser = false;

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int postId = jsonNode.get("id").asInt();
            int userId = jsonNode.get("userId").asInt();

            numOfLikes = postDao.getNumOfLikesForPost(postId);
            isPostLikedByUser = postDao.isPostLikedByUser(postId, userId);
        } catch (JsonProcessingException | DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String response = " { \"response\" : {\"numOfLikes\" :" + numOfLikes + ", \"isLikedByUser\" :" + isPostLikedByUser + " } } ";

        return response;
    }

    private String likePost() {
        String token = getTokenFromRequest();
        if (!tokenManager.isTokenValid(token)) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED);
            return " { \"response\" : \"Unauthorized\" } ";
        }

        PostDao postDao = new JdbcPostDao(dbConnection.getDataSource());

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int postId = jsonNode.get("id").asInt();
            int userId = jsonNode.get("userId").asInt();

            postDao.addLike(postId, userId);
            return " { \"response\" : \"Success\" } ";
        } catch (JsonProcessingException | DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            return " { \"response\" : \"Error liking post.\" } ";
        }
    }

    private String unlikePost() {
        String token = getTokenFromRequest();
        if (!tokenManager.isTokenValid(token)) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED);
            return " { \"response\" : \"Unauthorized\" } ";
        }

        PostDao postDao = new JdbcPostDao(dbConnection.getDataSource());

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int postId = jsonNode.get("id").asInt();
            int userId = jsonNode.get("userId").asInt();

            postDao.removeLike(postId, userId);
            return " { \"response\" : \"Success\" } ";
        } catch (JsonProcessingException | DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            return " { \"response\" : \"Error unliking post.\" } ";
        }
    }








    /** ------ Get Like Data For Comment ------ **/
    private String getLikeDataForComment() {
        String token = getTokenFromRequest();
        if (!tokenManager.isTokenValid(token)) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED);
            return " { \"response\" : \"Unauthorized\" } ";
        }

        CommentDao commentDao = new JdbcCommentDao(dbConnection.getDataSource());

        int numOfLikes = 0;
        boolean isCommentLikedByUser = false;

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int commentId = jsonNode.get("id").asInt();
            int userId = jsonNode.get("userId").asInt();

            numOfLikes = commentDao.getNumOfLikesForComment(commentId);
            isCommentLikedByUser = commentDao.isCommentLikedByUser(commentId, userId);
        } catch (JsonProcessingException | DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        return " { \"response\" : {\"numOfLikes\" :" + numOfLikes + ", \"isLikedByUser\" :" + isCommentLikedByUser + " } } ";
    }

    private String likeComment() {
        String token = getTokenFromRequest();
        if (!tokenManager.isTokenValid(token)) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED);
            return " { \"response\" : \"Unauthorized\" } ";
        }

        CommentDao commentDao = new JdbcCommentDao(dbConnection.getDataSource());

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int commentId = jsonNode.get("id").asInt();
            int userId = jsonNode.get("userId").asInt();

            commentDao.addLike(commentId, userId);
            return " { \"response\" : \"Success\" } ";
        } catch (JsonProcessingException | DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            return " { \"response\" : \"Error liking comment.\" } ";
        }
    }

    private String unlikeComment() {
        String token = getTokenFromRequest();
        if (!tokenManager.isTokenValid(token)) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED);
            return " { \"response\" : \"Unauthorized\" } ";
        }

        CommentDao commentDao = new JdbcCommentDao(dbConnection.getDataSource());

        try {
            JsonNode jsonNode = getJsonFromRequestBody();
            int commentId = jsonNode.get("id").asInt();
            int userId = jsonNode.get("userId").asInt();

            commentDao.removeLike(commentId, userId);
            return " { \"response\" : \"Success\" } ";
        } catch (JsonProcessingException | DataIntegrityViolationException e) {
            response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            return " { \"response\" : \"Error unliking comment.\" } ";
        }
    }


    /*******************************************************************************************************************
     * Helper Methods
     */
    private String getTokenFromRequest() {
        String authHeader = response.getRequest().getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }




    private String makeResponseJson(String responseData) {
        String output = "{";
        output += " \"response\" : ";

        output += responseData;

        output += " }";

        return output;
    }

    private String makeResponseJson(boolean success, String responseData) {
        String output = "{";
        output += " \"response\" : " + "\"" + responseData + "\"" + ", ";

        output += " \"success\" : " + success;

        output += " }";

        return output;
    }

    private JsonNode getJsonFromRequestBody() throws JsonMappingException, JsonProcessingException{
        String requestBody = response.getRequest().getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody);

        return  jsonNode;
    }

    private String postsListToJson(List<Post> posts) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(posts);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]"; // fallback
        }
    }

    private String postToJson(Post post) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(post);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}"; // fallback
        }
    }

    private String commentListToJson(List<Comment> comments) {
        String output = "[ ";

        if (comments != null) {
            for (int i = 0; i < comments.size(); i++) {
                output += commentToJson(comments.get(i));

                if (i < comments.size() - 1) {
                    output += ", ";
                }
            }
        }

        output += " ]";

        return output;
    }

    private String commentToJson(Comment comment) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(comment);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}"; // fallback
        }
    }





}
