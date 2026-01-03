package com.creditcardcomparison.http;

import com.creditcardcomparison.model.Member;
import io.jsonwebtoken.*;

import java.util.Base64;
import java.util.Date;

public class TokenManager {

    private final int SECOND = 1000;
    private final int MINUTE = SECOND * 60;
    private final int HOUR = MINUTE * 60;
    private final int DAY = HOUR * 24;
    private final int WEEK = DAY * 7;

    private final int EXPIRATION_TIME = 2 * DAY;

    private static final String SECRET_KEY = "my_secret_key";  // TODO: Change this later to actual secret key that updates

    public String generateToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()))
                .compact();
    }

    public boolean isTokenValid(String token) {
        if (token == null || token.equals("null")) {
            return false;
        }

        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()))
                    .parseClaimsJws(token);

            // Check if the token is expired
            Date expiration = claims.getBody().getExpiration();
            return expiration.after(new Date());
        } catch (ExpiredJwtException e) {
            return false; // Token is expired
        } catch (Exception e) {
            return false; // Token is invalid
        }
    }
}
