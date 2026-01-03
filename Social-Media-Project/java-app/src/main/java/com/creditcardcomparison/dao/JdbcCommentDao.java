package com.creditcardcomparison.dao;

import com.creditcardcomparison.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.dao.DataIntegrityViolationException;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcCommentDao implements CommentDao{

    private final JdbcTemplate jdbcTemplate;

    // Constructor
    public JdbcCommentDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public void addCommentToPost(int parentPostId, int commentingMemberId, String content) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        String sql = """
            INSERT INTO comment (root_post_id, parent_post_id, member_id, content)
            VALUES (?, ?, ?, ?);
            """;

        jdbcTemplate.update(sql, parentPostId, parentPostId, commentingMemberId, content);
    }

    @Override
    public void addCommentToComment(int rootPostId, int parentCommentId, int commentingMemberId, String content) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        String sql = """
                INSERT INTO comment (root_post_id, parent_comment_id, member_id, content)
                VALUES (?, ?, ?, ?);
                """;

        jdbcTemplate.update(sql, rootPostId, parentCommentId, commentingMemberId, content);
    }

    @Override
    public List<Comment> getCommentsOfPostForLoggedOutUser(int postId, int numberOfEmbeddedComments)
            throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {

        List<Comment> comments = new ArrayList();
        String sql = """
                SELECT comment.id,
                    comment.content,
                    COUNT(comment_likes.member_id) AS num_of_likes,
                    ( SELECT member.username FROM member WHERE member.id = comment.member_id ) AS author,
                    comment.created_on_date,
                    FALSE AS liked_by_user
                FROM comment
                FULL OUTER JOIN comment_likes ON comment_likes.comment_id = comment.id
                WHERE parent_post_id = ?
                GROUP BY comment.id;
            """;

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, postId);
        while (results.next()) {
            Comment comment = mapRowToComment(results);
            comments.add(comment);
        }

        if (numberOfEmbeddedComments > 0) {
            for (Comment comment : comments) {
                List<Comment> commentReplies = getCommentsOfCommentForLoggedOutUser(
                        comment.getId(),
                        numberOfEmbeddedComments - 1 // decrease depth for recursion
                );
                comment.setComments(commentReplies);
            }
        }

        return comments;
    }

    @Override
    public List<Comment> getCommentsOfPostForLoggedInUser(int postId, int memberId, int numberOfEmbeddedComments)
            throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {

        List<Comment> comments = new ArrayList();
        String sql = """
                SELECT comment.id,
                    comment.content,
                    COUNT(comment_likes.member_id) AS num_of_likes,
                    ( SELECT member.username FROM member WHERE member.id = comment.member_id ) AS author,
                    comment.created_on_date,
                    EXISTS ( SELECT 1 FROM comment_likes WHERE comment_id = comment.id AND member_id = ? ) AS liked_by_user
                FROM comment
                FULL OUTER JOIN comment_likes ON comment_likes.comment_id = comment.id
                WHERE parent_post_id = ?
                GROUP BY comment.id;
            """;

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, memberId, postId);
        while (results.next()) {
            Comment comment = mapRowToComment(results);
            comments.add(comment);
        }

        if (numberOfEmbeddedComments > 0) {
            for (Comment comment : comments) {
                List<Comment> commentReplies = getCommentsOfCommentForLoggedInUser(
                        comment.getId(),
                        memberId,
                        numberOfEmbeddedComments - 1 // decrease depth for recursion
                );
                comment.setComments(commentReplies);
            }
        }

        return comments;
    }

    @Override
    public List<Comment> getCommentsOfCommentForLoggedOutUser(int parentCommentId, int depthRemaining)
            throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {

        List<Comment> comments = new ArrayList<>();
        String sql = """
            SELECT comment.id,
                comment.content,
                COUNT(comment_likes.member_id) AS num_of_likes,
                ( SELECT member.username FROM member WHERE member.id = comment.member_id ) AS author,
                comment.created_on_date,
                FALSE AS liked_by_user
            FROM comment
            FULL OUTER JOIN comment_likes ON comment_likes.comment_id = comment.id
            WHERE parent_comment_id = ?
            GROUP BY comment.id;
        """;

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parentCommentId);
        while (results.next()) {
            Comment comment = mapRowToComment(results);
            comments.add(comment);
        }

        if (depthRemaining > 0) {
            for (Comment comment : comments) {
                List<Comment> nestedReplies = getCommentsOfCommentForLoggedOutUser(
                        comment.getId(),
                        depthRemaining - 1
                );
                comment.setComments(nestedReplies);
            }
        }

        return comments;
    }

    @Override
    public List<Comment> getCommentsOfCommentForLoggedInUser(int parentCommentId, int memberId, int depthRemaining)
            throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {

        List<Comment> comments = new ArrayList<>();
        String sql = """
            SELECT comment.id,
                comment.content,
                COUNT(comment_likes.member_id) AS num_of_likes,
                ( SELECT member.username FROM member WHERE member.id = comment.member_id ) AS author,
                comment.created_on_date,
                EXISTS ( SELECT 1 FROM comment_likes WHERE comment_id = comment.id AND member_id = ? ) AS liked_by_user
            FROM comment
            FULL OUTER JOIN comment_likes ON comment_likes.comment_id = comment.id
            WHERE parent_comment_id = ?
            GROUP BY comment.id;
        """;

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, memberId, parentCommentId);
        while (results.next()) {
            Comment comment = mapRowToComment(results);
            comments.add(comment);
        }

        if (depthRemaining > 0) {
            for (Comment comment : comments) {
                List<Comment> nestedReplies = getCommentsOfCommentForLoggedInUser(
                        comment.getId(),
                        memberId,
                        depthRemaining - 1
                );
                comment.setComments(nestedReplies);
            }
        }

        return comments;
    }



    @Override
    public List<Comment> getCommentThread(int commentId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        List<Comment> commentThread = new ArrayList();

        boolean thereAreMoreCommentsInTheThread = true;
        int currentCommentId = commentId;

        while (thereAreMoreCommentsInTheThread) {
            String sql = """
                SELECT comment.id,
                    comment.content,
                    COUNT(comment_likes.member_id) AS num_of_likes, parent_comment_id,
                    ( SELECT member.username FROM member WHERE member.id = comment.member_id ) AS author,
                    comment.created_on_date,
                    FALSE AS liked_by_user
                FROM comment
                FULL OUTER JOIN comment_likes ON comment_likes.comment_id = comment.id
                WHERE comment.id = ?
                GROUP BY comment.id;
            """;

            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, currentCommentId);
            if (result.next()) {
                Comment comment = mapRowToComment(result);
                commentThread.add(0, comment); // Adds comment to the front of the list

                int parentCommentId = result.getInt("parent_comment_id");

                // This means we've added the last comment in the thread
                if (parentCommentId <= 0) {
                    thereAreMoreCommentsInTheThread = false;
                }
                else {
                    currentCommentId = parentCommentId;
                }
            }
        }

        return commentThread;
    }

    @Override
    public int getNumOfLikesForComment(int commentId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        int numOfLikes = 0;
        String sql = """
                SELECT num_of_likes
                FROM comment
                WHERE comment.id = ?;
            """;

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, commentId);

        if (result.next()) {
            numOfLikes = result.getInt("num_of_likes");
        }

        return numOfLikes;
    }

    @Override
    public boolean isCommentLikedByUser(int commentId, int memberId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        String sql = """
                SELECT *
                FROM comment_likes
                WHERE comment_id = ? AND member_id = ?;
            """;

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, commentId, memberId);

        if (result.next()) {
            return true;
        }

        return false;
    }

    @Override
    public void addLike(int commentId, int memberId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        String insertSql = """
                INSERT INTO comment_likes
                (comment_id, member_id)
                VALUES (?, ?)
                RETURNING comment_id, member_id;
            """;

        SqlRowSet insertResult = jdbcTemplate.queryForRowSet(insertSql, commentId, memberId);

        if (insertResult.next()) {
            String addLikeSql = """
                UPDATE comment
                SET num_of_likes = num_of_likes + 1
                WHERE comment.id = ?;
            """;
            jdbcTemplate.update(addLikeSql, commentId);
        }
    }

    @Override
    public void removeLike(int commentId, int memberId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        String insertSql = """
                DELETE FROM comment_likes
                WHERE comment_id = ? AND member_id = ?
                RETURNING comment_id, member_id;
            """;

        SqlRowSet insertResult = jdbcTemplate.queryForRowSet(insertSql, commentId, memberId);

        if (insertResult.next()) {
            String addLikeSql = """
                UPDATE comment
                SET num_of_likes = num_of_likes - 1
                WHERE comment.id = ?;
            """;
            jdbcTemplate.update(addLikeSql, commentId);
        }
    }


    /** --- HELPER METHODS --- **/
    private Comment mapRowToComment (SqlRowSet result) {
        int id = result.getInt("id");
        String content = result.getString("content");
        String author = result.getString("author");
        int likes = result.getInt("num_of_likes");
        String createdOn = result.getString("created_on_date");
        boolean likedByUser = result.getBoolean("liked_by_user");

        Comment comment = new Comment(id);
        comment.setContent(content);
        comment.setAuthor(author);
        comment.setNumOfLikes(likes);
        comment.setCreatedOn(createdOn);
        comment.setLikedByUser(likedByUser);

        return comment;
    }
}
