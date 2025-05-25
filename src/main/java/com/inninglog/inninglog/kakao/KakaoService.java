package com.inninglog.inninglog.kakao;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientCodecCustomizer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;



@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    private final String KAUTH_TOKEN_URL = "https://kauth.kakao.com";
    private final String KAPI_USER_URL = "https://kapi.kakao.com";

    public String getAccessToken(String code) {
        String rawResponse = WebClient.create(KAUTH_TOKEN_URL)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(System.out::println) // ✅ 카카오 응답 전체 확인
                .block();

        // 👉 JSON 파싱해서 access_token 추출 (Jackson 사용 가능)
        // 일단 테스트니까 이거 대충 파싱
        String token = rawResponse.split("\"access_token\":\"")[1].split("\"")[0];
        System.out.println("✅ 추출된 AccessToken: " + token);
        return token;
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        return WebClient.create(KAPI_USER_URL)
                .get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();
    }
}