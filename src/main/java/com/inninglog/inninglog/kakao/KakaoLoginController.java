package com.inninglog.inninglog.kakao;


import com.inninglog.inninglog.global.auth.JwtProvider;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;


    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        try {
            // Access Token 받아오기
            String kakaoAccessToken = kakaoService.getAccessToken(code);

            // 사용자 정보 가져오기
            KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(kakaoAccessToken);

            // 사용자 저장 및 업데이트
            Member member = memberService.saveOrUpdateMember(userInfo);

            // JWT 토큰 생성
            String jwtAccessToken = jwtProvider.createToken(member.getId()); // ← 수정
            String jwtRefreshToken = jwtProvider.createRefreshToken(member.getId()); // ← 수정

            // 헤더에 토큰과 kakaoId 추가
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtAccessToken);
            headers.set("Refresh-Token", jwtRefreshToken);
            headers.set("kakaoId", member.getKakaoId().toString()); // kakaoId 추가



            // 응답 데이터 생성
            KakaoLoginResponse response = new KakaoLoginResponse(
                    "로그인 성공",
                    member.getNickname()
            );

            return ResponseEntity.ok().headers(headers).body(response);

        } catch (Exception e) {
            log.error("Error during Kakao login process", e);
            return new ResponseEntity<>("로그인 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //헤더 토큰 테스트
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint(@RequestHeader("Authorization") String authorizationHeader) {
        // 로그에 Authorization 헤더 출력
        log.info("Authorization Header: {}", authorizationHeader);

        // Bearer 토큰 추출
        String token = authorizationHeader.replace("Bearer ", "");
        log.info("Token: {}", token);

        // 필요한 로직 수행
        return ResponseEntity.ok("Token received successfully");
    }
}

