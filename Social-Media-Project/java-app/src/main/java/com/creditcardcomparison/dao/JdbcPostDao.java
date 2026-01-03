package com.creditcardcomparison.dao;

import com.creditcardcomparison.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcPostDao implements PostDao{

    private final JdbcTemplate jdbcTemplate;

    private final int HOURS_OF_POST_PRIORITY = 2;

    // Constructor
    public JdbcPostDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void makePost(int postingMemberId, String postTitle, String postContent, String postTag) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        String sql = """
            INSERT INTO post (member_id, title, content)
            VALUES (?, ?, ?)
            RETURNING id
        """;

        // Insert the post and get the generated ID
        Integer postId = jdbcTemplate.queryForObject(sql, Integer.class, postingMemberId, postTitle,  postContent);

        if (postId == null) {
            return;
        }

        addLike(postId, postingMemberId);

        // Add the tag only if it's not "All"
        if (!postTag.equalsIgnoreCase("All")) {
            String tagSql = """
                INSERT INTO post_tag (tag_name, post_id)
                VALUES (?, ?)
            """;
            jdbcTemplate.update(tagSql, postTag, postId);
        }
    }


    @Override
    public List<Post> getPostsForLoggedOutUser() throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException{
        List<Post> posts = new ArrayList<>();

        String sql = """
            SELECT
                p.id,
                p.title,
                p.content,
                ((EXTRACT(EPOCH FROM (NOW() - p.created_on_date)) / 3600) < ?) AS post_priority,
                COUNT(DISTINCT pl.member_id) AS num_of_likes,
                COUNT(DISTINCT c.id) AS num_of_comments,
                m.username AS author
            FROM post p
                LEFT JOIN member m ON p.member_id = m.id
                LEFT JOIN post_likes pl ON pl.post_id = p.id
                LEFT JOIN comment c ON c.root_post_id = p.id
            GROUP BY p.id, p.title, p.content, m.username
            ORDER BY post_priority DESC, p.num_of_likes DESC;
            
        """;

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, HOURS_OF_POST_PRIORITY);
        while (results.next()) {
            Post post = mapRowToPost(results);
            posts.add(post);
        }
        return posts;
    }

    @Override
    public List<Post> getPostsForLoggedInUser(int memberId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException{
        List<Post> posts = new ArrayList<>();
        String sql = """
            SELECT
                 p.id,
                 p.title,
                 p.content,
                ((EXTRACT(EPOCH FROM (NOW() - p.created_on_date)) / 3600) < ?) AS post_priority,
                 COUNT(DISTINCT pl.member_id) AS num_of_likes,
                 COUNT(DISTINCT c.id) AS num_of_comments,
                 m.username AS author,
                 CASE
                     WHEN ul.member_id IS NOT NULL THEN TRUE
                     ELSE FALSE
                 END AS liked_by_user
             FROM post p
             LEFT JOIN member m ON p.member_id = m.id
             LEFT JOIN post_likes pl ON pl.post_id = p.id
             LEFT JOIN post_likes ul ON ul.post_id = p.id AND ul.member_id = ?
             LEFT JOIN comment c ON c.root_post_id = p.id
             GROUP BY
                 p.id, p.title, p.content, m.username, ul.member_id
             ORDER BY post_priority DESC, p.num_of_likes DESC;
        """;

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, HOURS_OF_POST_PRIORITY, memberId);
        while (results.next()) {
            Post post = mapRowToPost(results);
            posts.add(post);
        }
        return posts;
    }

    @Override
    public Post getPostForLoggedOutUser(int postId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException{
        Post post = null;

        String sql = """
            SELECT post.id,
                post.title,
                post.content,
                COUNT(post_likes.member_id) AS num_of_likes,
                ( SELECT COUNT (*) FROM comment WHERE comment.root_post_id = post.id ) AS num_of_comments,
                ( SELECT member.username FROM member WHERE member.id = post.member_id ) AS author
            FROM post
            FULL OUTER JOIN post_likes ON post_likes.post_id = post.id
            WHERE post.id = ?
            GROUP BY post.id;
        """;
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, postId);

        if (result.next()) {
            post = mapRowToPost(result);
        }

        return post;
    }

    @Override
    public Post getPostForLoggedInUser(int postId, int memberId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException{
        Post post = null;

        String sql = """
            SELECT post.id,
                post.title,
                post.content,
                COUNT(post_likes.member_id) AS num_of_likes,
                ( SELECT member.username FROM member WHERE member.id = post.member_id ) AS author,
                ( SELECT COUNT (*) FROM comment WHERE comment.root_post_id = post.id ) AS num_of_comments,
                EXISTS ( SELECT 1 FROM post_likes WHERE post_id = post.id AND member_id = ? ) AS liked_by_user
            FROM post
            FULL OUTER JOIN post_likes ON post_likes.post_id = post.id
            WHERE post.id = ?
            GROUP BY post.id;
        """;
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, memberId, postId);

        if (result.next()) {
            post = mapRowToPost(result);
        }

        return post;
    }

    @Override
    public List<Post> getPostsWithSearch(String searchTerm, String searchtag) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException{
        List<Post> posts = new ArrayList<>();
        String sql;
        SqlRowSet results;

        if (searchtag.equalsIgnoreCase("All")){
            sql = """
                SELECT post.id,
                    post.title,
                    post.content,
                    COUNT(post_likes.member_id) AS num_of_likes,
                    ( SELECT member.username FROM member WHERE member.id = post.member_id ) AS author
                FROM post
                FULL OUTER JOIN post_likes ON post_likes.post_id = post.id
                GROUP BY post.id
                ORDER BY similarity(content, ?) DESC
                LIMIT 10;
            """;
            results = jdbcTemplate.queryForRowSet(sql, searchTerm);
        }
        else {
            sql = """
                SELECT post.id, post.title, post.content, COUNT(post_likes.member_id) AS num_of_likes
                    FROM post
                    FULL OUTER JOIN post_likes ON post_likes.post_id = post.id
                    FULL OUTER JOIN post_tag ON post_tag.post_id = post.id
                    WHERE post_tag.tag_name = ?
                    GROUP BY post.id
                    ORDER BY similarity(content, ?) DESC
                    LIMIT 10;
            """;
            results = jdbcTemplate.queryForRowSet(sql, searchtag, searchTerm);
        }

        while (results.next()) {
            Post post = mapRowToPost(results);
            posts.add(post);
        }
        return posts;
    }

    @Override
    public Post getRootPostOfCommentThread(int commentId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException{
        int idOfRootPost = 0;

        int currentCommentId = commentId;
        boolean thereAreMoreCommentsInTheThread = true;

        while (thereAreMoreCommentsInTheThread) {
            String commentSql = """
                SELECT parent_post_id, parent_comment_id
                FROM comment
                WHERE comment.id = ?;
            """;

            SqlRowSet result = jdbcTemplate.queryForRowSet(commentSql, currentCommentId);
            if (result.next()) {
                int parentPostId = result.getInt("parent_post_id");

                // This means we've added the last comment in the thread
                if (parentPostId > 0) {
                    thereAreMoreCommentsInTheThread = false;
                    idOfRootPost = parentPostId;
                }
                else {
                    currentCommentId = result.getInt("parent_comment_id");
                }
            }
        }

        return getPostForLoggedOutUser(idOfRootPost);
    }

    @Override
    public int getNumOfLikesForPost(int postId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        int numOfLikes = 0;
        String sql = """
                SELECT num_of_likes
                FROM post
                WHERE post.id = ?;
            """;

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, postId);

        if (result.next()) {
            numOfLikes = result.getInt("num_of_likes");
        }

        return numOfLikes;
    }

    @Override
    public boolean isPostLikedByUser(int postId, int memberId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        String sql = """
                SELECT *
                FROM post_likes
                WHERE post_id = ? AND member_id = ?;
            """;

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, postId, memberId);

        if (result.next()) {
            return true;
        }

        return false;
    }

    @Override
    public void addLike(int postId, int memberId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        String insertSql = """
                INSERT INTO post_likes
                (post_id, member_id)
                VALUES (?, ?)
                RETURNING post_id, member_id;
            """;

        SqlRowSet insertResult = jdbcTemplate.queryForRowSet(insertSql, postId, memberId);

        if (insertResult.next()) {
            String addLikeSql = """
                UPDATE post
                SET num_of_likes = num_of_likes + 1
                WHERE post.id = ?;
            """;
            jdbcTemplate.update(addLikeSql, postId);
        }
    }

    @Override
    public void removeLike(int postId, int memberId) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        String insertSql = """
                DELETE FROM post_likes
                WHERE post_id = ? AND member_id = ?
                RETURNING post_id, member_id;
            """;

        SqlRowSet insertResult = jdbcTemplate.queryForRowSet(insertSql, postId, memberId);

        if (insertResult.next()) {
            String addLikeSql = """
                UPDATE post
                SET num_of_likes = num_of_likes - 1
                WHERE post.id = ?;
            """;
            jdbcTemplate.update(addLikeSql, postId);
        }
    }


    /** --- HELPER METHODS --- **/
    private Post mapRowToPost(SqlRowSet result) {
        int id = result.getInt("id");
        String title = result.getString("title");
        String author = result.getString("author");
        String content = result.getString("content");
        int likes = result.getInt("num_of_likes");


        // TODO: Add Comments to the Post : List<Comment> comments =

        Post post = new Post(id);

        post.setTitle(title);
        post.setContent(content);
        post.setNumOfLikes(likes);
        post.setAuthor(author);

        // columns that are optional
        if (columnExists(result, "liked_by_user")) {
            boolean likedByUser = result.getBoolean("liked_by_user");
            post.setLikedByUser(likedByUser);
        }

        if (columnExists(result, "num_of_comments")) {
            int numberOfComments = result.getInt("num_of_comments");
            post.setNumberOfComments(numberOfComments);
        }

        return post;
    }

    private boolean columnExists(SqlRowSet result, String columnName) {
        SqlRowSetMetaData metaData = result.getMetaData();
        for (String name : metaData.getColumnNames()) {
            if (name.equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

}
