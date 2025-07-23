package com.java.admin.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    private static final String SECRET = "abcdefghijklmnopqrstuvwxyz1234567890";

    public static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String createToken(String subject, long expire) {

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .claims()
                .id(UUID.randomUUID().toString())
                .issuer("admin")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expire))
                .subject(subject)
                .and()
                .signWith(KEY, Jwts.SIG.HS256)
                .compact();
    }

    public static Jws<Claims> parseToken(String token) {
        return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token);
    }

    public static JwsHeader parseHeader(String token) {
        return parseToken(token).getHeader();
    }

    public static Claims parseClaims(String token) {
        return parseToken(token).getPayload();
    }
}
