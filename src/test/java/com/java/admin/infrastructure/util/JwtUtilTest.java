package com.java.admin.infrastructure.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JwtUtil Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Token creation</li>
 *   <li>Token parsing</li>
 *   <li>Exception handling (signature errors, format errors, expiration, etc.)</li>
 *   <li>Header and Claims extraction</li>
 * </ul>
 *
 * <p>Coverage Target: 100%
 */
@DisplayName("JwtUtil Unit Tests")
class JwtUtilTest {

    private static final long ONE_HOUR_MS = 60 * 60 * 1000;
    private static final long ONE_SECOND_MS = 1000;
    private static final String TEST_SUBJECT = "test-user-id";

    @Test
    @DisplayName("Should successfully create token with valid subject")
    void shouldCreateTokenWhenValidSubjectProvided() {
        // When
        String token = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.").length).isEqualTo(3); // JWT format: header.payload.signature
    }

    @Test
    @DisplayName("Should create tokens with different expiration times")
    void shouldCreateTokensWithDifferentExpirationWhenCalledMultipleTimes() {
        // When
        String tokenOneHour = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);
        String tokenOneSecond = JwtUtil.createToken(TEST_SUBJECT, ONE_SECOND_MS);

        // Then
        Claims claimsOneHour = JwtUtil.parseClaims(tokenOneHour);
        Claims claimsOneSecond = JwtUtil.parseClaims(tokenOneSecond);

        assertThat(claimsOneHour.getExpiration().getTime())
                .isGreaterThan(claimsOneSecond.getExpiration().getTime());
    }

    @Test
    @DisplayName("Should correctly set issuer in token")
    void shouldSetIssuerWhenCreatingToken() {
        // When
        String token = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);

        // Then
        Claims claims = JwtUtil.parseClaims(token);
        assertThat(claims.getIssuer()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should correctly set issuedAt time in token")
    void shouldSetIssuedAtTimeWhenCreatingToken() {
        // When
        String token = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);
        long afterCreateTime = System.currentTimeMillis();

        // Then
        Claims claims = JwtUtil.parseClaims(token);
        assertThat(claims.getIssuedAt()).isNotNull();
        // Allow 1 second time deviation (Date precision may be truncated to seconds)
        assertThat(claims.getIssuedAt().getTime())
                .isGreaterThan(afterCreateTime - 2000)
                .isLessThanOrEqualTo(afterCreateTime);
    }

    @Test
    @DisplayName("Should successfully parse valid token")
    void shouldParseTokenWhenTokenIsValid() {
        // Given
        String token = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);

        // When
        Claims claims = JwtUtil.parseClaims(token);

        // Then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(TEST_SUBJECT);
    }

    @Test
    @DisplayName("Should correctly parse subject from token")
    void shouldParseSubjectWhenTokenIsValid() {
        // Given
        String customSubject = "custom-user-123";
        String token = JwtUtil.createToken(customSubject, ONE_HOUR_MS);

        // When
        Claims claims = JwtUtil.parseClaims(token);

        // Then
        assertThat(claims.getSubject()).isEqualTo(customSubject);
    }

    @Test
    @DisplayName("Should correctly parse ID from token")
    void shouldParseIdWhenTokenIsValid() {
        // Given
        String token = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);

        // When
        Claims claims = JwtUtil.parseClaims(token);

        // Then
        assertThat(claims.getId()).isNotNull();
        assertThat(claims.getId()).isNotEmpty();
    }

    @Test
    @DisplayName("Should correctly parse token header")
    void shouldParseHeaderWhenTokenIsValid() {
        // Given
        String token = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);

        // When
        JwsHeader header = JwtUtil.parseHeader(token);

        // Then
        assertThat(header).isNotNull();
        assertThat(header.get("typ")).isEqualTo("JWT");
        assertThat(header.get("alg")).isEqualTo("HS256");
    }

    @Test
    @DisplayName("Header should contain correct algorithm type")
    void shouldContainCorrectAlgorithmWhenParsingHeader() {
        // Given
        String token = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);

        // When
        JwsHeader header = JwtUtil.parseHeader(token);

        // Then
        assertThat(header.getAlgorithm()).isEqualTo("HS256");
    }

    @Test
    @DisplayName("Should reject token with invalid signature")
    void shouldRejectTokenWhenSignatureIsInvalid() {
        // Given - Create a token with different secret key
        String differentSecret = "differentsecretkey1234567890abcdefghij";
        SecretKey differentKey = Keys.hmacShaKeyFor(differentSecret.getBytes());
        String fakeToken = io.jsonwebtoken.Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .claims()
                .id(java.util.UUID.randomUUID().toString())
                .issuer("admin")
                .issuedAt(new Date())
                .subject(TEST_SUBJECT)
                .expiration(new Date(System.currentTimeMillis() + ONE_HOUR_MS))
                .and()
                .signWith(differentKey, io.jsonwebtoken.Jwts.SIG.HS256)
                .compact();

        // When & Then
        assertThatThrownBy(() -> JwtUtil.parseToken(fakeToken))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    @DisplayName("Should reject malformed token")
    void shouldRejectTokenWhenTokenIsMalformed() {
        // Given - Various malformed tokens
        String[] invalidTokens = {
                "invalid.token",       // Missing part
                "invalid",             // No dot separator
                "a.b.c.d",            // Too many parts
                "invalid.token.format" // Fake token
        };

        // Then
        for (String invalidToken : invalidTokens) {
            assertThatThrownBy(() -> JwtUtil.parseToken(invalidToken))
                    .isInstanceOf(io.jsonwebtoken.JwtException.class);
        }

        // Empty string and null throw IllegalArgumentException
        assertThatThrownBy(() -> JwtUtil.parseToken(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> JwtUtil.parseToken(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should reject expired token")
    void shouldRejectTokenWhenTokenIsExpired() {
        // Given - Create an already expired token
        String expiredToken = JwtUtil.createToken(TEST_SUBJECT, -1000); // Negative means expired

        // When & Then
        // Wait a short time to ensure token is actually expired
        assertThatThrownBy(() -> {
            Thread.sleep(10);
            JwtUtil.parseToken(expiredToken);
        }).isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }

    @Test
    @DisplayName("Should reject token without signature")
    void shouldRejectTokenWhenTokenMissingSignature() {
        // Given - Manually construct a token without signature
        String tokenWithoutSignature = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LXVzZXItaWQifQ";

        // When & Then
        assertThatThrownBy(() -> JwtUtil.parseToken(tokenWithoutSignature))
                .isInstanceOf(io.jsonwebtoken.JwtException.class);
    }

    @Test
    @DisplayName("Should handle token with zero expiration")
    void shouldRejectTokenWhenExpirationIsZero() {
        // Given
        String token = JwtUtil.createToken(TEST_SUBJECT, 0);

        // When & Then
        // Token with 0 expiration should expire immediately
        assertThatThrownBy(() -> JwtUtil.parseToken(token))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }

    @Test
    @DisplayName("Should handle subject with special characters")
    void shouldAcceptTokenWhenSubjectHasSpecialCharacters() {
        // Given
        String specialSubject = "user@domain.com#123$%^&*()";

        // When
        String token = JwtUtil.createToken(specialSubject, ONE_HOUR_MS);
        Claims claims = JwtUtil.parseClaims(token);

        // Then
        assertThat(claims.getSubject()).isEqualTo(specialSubject);
    }

    @Test
    @DisplayName("Should handle long subject string")
    void shouldAcceptTokenWhenSubjectIsLong() {
        // Given
        String longSubject = "a".repeat(1000);

        // When
        String token = JwtUtil.createToken(longSubject, ONE_HOUR_MS);
        Claims claims = JwtUtil.parseClaims(token);

        // Then
        assertThat(claims.getSubject()).hasSize(1000);
    }

    @Test
    @DisplayName("Should generate unique IDs when creating tokens")
    void shouldGenerateUniqueIdsWhenCreatingTokens() {
        // When
        String token1 = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);
        String token2 = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);

        // Then
        Claims claims1 = JwtUtil.parseClaims(token1);
        Claims claims2 = JwtUtil.parseClaims(token2);

        assertThat(claims1.getId()).isNotNull();
        assertThat(claims2.getId()).isNotNull();
        assertThat(claims1.getId()).isNotEqualTo(claims2.getId());
    }

    @Test
    @DisplayName("parseToken should return complete Jws object")
    void shouldReturnCompleteJwsWhenParsingToken() {
        // Given
        String token = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);

        // When
        var jws = JwtUtil.parseToken(token);

        // Then
        assertThat(jws).isNotNull();
        assertThat(jws.getHeader()).isNotNull();
        assertThat(jws.getPayload()).isNotNull();
        // Note: getSignature() is deprecated in JJWT 0.11+, but JWS object validation ensures signature is valid
    }

    @Test
    @DisplayName("parseClaims should return payload directly")
    void shouldReturnPayloadWhenParsingClaims() {
        // Given
        String token = JwtUtil.createToken(TEST_SUBJECT, ONE_HOUR_MS);

        // When
        Claims claims = JwtUtil.parseClaims(token);

        // Then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(TEST_SUBJECT);
    }

    @Test
    @DisplayName("Should calculate expiration time correctly")
    void shouldCalculateExpirationWhenCreatingToken() {
        // Given
        long expirationTime = ONE_HOUR_MS;

        // When
        String token = JwtUtil.createToken(TEST_SUBJECT, expirationTime);
        Claims claims = JwtUtil.parseClaims(token);

        // Then
        long actualExpiration = claims.getExpiration().getTime();
        long issuedAt = claims.getIssuedAt().getTime();
        long expectedExpiration = issuedAt + expirationTime;

        // Verify expiration ≈ creation time + expiration duration
        assertThat(actualExpiration).isEqualTo(expectedExpiration);

        // Verify expiration is in the future
        assertThat(actualExpiration).isGreaterThan(System.currentTimeMillis());
    }
}
