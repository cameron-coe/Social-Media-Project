package com.creditcardcomparison.dao;

import com.creditcardcomparison.model.Member;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Random;

@Component
public class JdbcMemberDao implements MemberDao {


    private final JdbcTemplate jdbcTemplate;

    // Constructor
    public JdbcMemberDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Member createNewMember(String username, String password) throws CannotGetJdbcConnectionException, DaoException, DuplicateKeyException {
        Member newMember = null;
        String sql = "INSERT INTO member (username, salt, stored_hash) " +
                "VALUES (?, ?, ?) " +
                "RETURNING username;";

        String salt = generateSalt();
        String stringToHash = salt + password;
        String storedHash = makeHash(stringToHash);


            String newMemberUserName = jdbcTemplate.queryForObject(sql, String.class, username, salt, storedHash);
            newMember = getMemberByUsernameAndPassword(newMemberUserName, password);

//        catch (DuplicateKeyException e) {
//            if (e.getMessage().contains("member_username_key")) {
//                System.out.println("This username is already taken.");
//            }

        return newMember;
    }

    @Override
    public Member getMemberByUsernameAndPassword(String userName, String password) throws CannotGetJdbcConnectionException, DaoException {
        Member member = null;

        String sql = """
                SELECT id, username, salt, stored_hash
                FROM member
                WHERE username = ?;
        """;

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userName);

        if (result.next()) {
            String storedHash = result.getString("stored_hash");
            String storedSalt = result.getString("salt");

            String stringToHash = storedSalt + password;
            String newHash = makeHash(stringToHash);

            // Set the member if the password produced a matching hash
            if (storedHash.equals(newHash)) {
                member = mapRowToMember(result);
            }
        }

        return member;
    }


    /** --- Generate Salt --- **/
    private String generateSalt() {
        String saltCharacters = "abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKMLNOPQRSTUVWXYZ" +
                "01234567890" +
                "~!@#$%^&*()<>_=+.,| :";

        String salt = "";
        for (int i = 0; i < 255; i++) {
            salt += getRandomCharacterInString(saltCharacters);
        }

        return salt;
    }

    /** --- Make Hash --- **/
    private String makeHash(String input) {
        return "" + input.hashCode();
    }

    private char getRandomCharacterInString(String string) {
        Random random = new Random();
        int randomIndex = random.nextInt(string.length());
        return string.charAt(randomIndex);
    }

    /** --- Map Row to Member --- **/
    private Member mapRowToMember(SqlRowSet row) {
        Member member = new Member();

        String username = row.getString("username");
        int id = row.getInt("id");

        member.setUsername( username );
        member.setId(id);

        return member;
    }

}
