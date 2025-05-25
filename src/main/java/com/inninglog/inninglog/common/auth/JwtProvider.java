package com.inninglog.inninglog.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final int expiration;
    private Key SECRET_KEY;
    private final long REFRESH_TOKEN_EXPIRATION_MS = 604800000; // 7일 (1주일)



    public JwtProvider(@Value("${jwt.secretKey}")String secretkey, @Value("${jwt.expiration}")int expiration) {
        this.expiration = expiration;
        this.SECRET_KEY = new SecretKeySpec(
                java.util.Base64.getDecoder().decode(secretkey),  // 디코딩해서 byte[]로 변환
                SignatureAlgorithm.HS512.getJcaName()); // 이 바이트 배열을 '서명용 키 객체'로 만듦
    }


    public String createToken(Long kakaoId){
        Claims claims = Jwts.claims().setSubject(kakaoId.toString()); //claims는 payload라고 생가하면됨, subject로 키값 설정

        Date now = new Date();
        String token = Jwts.builder()
                .setIssuedAt(now) //발행시간 설정
                .setExpiration(new Date(now.getTime()+expiration*60*1000L)) //만료일자 설정, 현재 시간에 + 토큰 만료 시간 ---> 밀리초 단위
                .signWith(SECRET_KEY) //서명
                .compact();
        return token;
    }


    // Refresh Token 생성 메서드
    public String createRefreshToken(Long kakaoId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(kakaoId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    // 토큰 유효성 검증 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 사용자 ID 추출 메서드
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
}
