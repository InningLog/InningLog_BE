package com.inninglog.inninglog.global.auth;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final int expiration;
    private Key SECRET_KEY;
    private final long REFRESH_TOKEN_EXPIRATION_MS = 604800000; // 7일 (1주일)

    private final MemberRepository memberRepository;


    public JwtProvider(@Value("${jwt.secretKey}")String secretkey, @Value("${jwt.expiration}")int expiration, MemberRepository memberRepository) {
        this.expiration = expiration;
        this.SECRET_KEY = new SecretKeySpec(
                java.util.Base64.getDecoder().decode(secretkey),  // 디코딩해서 byte[]로 변환
                SignatureAlgorithm.HS512.getJcaName()); // 이 바이트 배열을 '서명용 키 객체'로 만듦
        this.memberRepository = memberRepository;
    }


    public String createToken(Long kakaoId){
        Claims claims = Jwts.claims().setSubject(kakaoId.toString()); //claims는 payload라고 생가하면됨, subject로 키값 설정

        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
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

    public Authentication getAuthentication(String token) {
        Long userId = getUserIdFromToken(token);

        // MemberService 통해 사용자 정보 조회
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserDetails userDetails = new CustomUserDetails(member);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    // Request의 Authorization 헤더에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
