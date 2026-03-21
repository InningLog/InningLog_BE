package com.inninglog.inninglog.domain.kakao.service;

import com.inninglog.inninglog.domain.kakao.dto.KakaoUserInfoResDTO;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @Value("${kakao.admin_key}")
    private String adminKey;

    private final WebClient kakaoWebClient;   // https://kauth.kakao.com
    private final WebClient kakaoApiClient;   // https://kapi.kakao.com

    public String getAccessToken(String code) {
        String rawResponse = kakaoWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        String token = rawResponse.split("\"access_token\":\"")[1].split("\"")[0];
        return token;
    }

    public KakaoUserInfoResDTO getUserInfo(String accessToken) {
        return kakaoApiClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfoResDTO.class)
                .block();
    }

    public void unlinkKakaoUser(Long kakaoId) {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("target_id_type", "user_id");
            body.add("target_id", String.valueOf(kakaoId));

            kakaoApiClient.post()
                    .uri("/v1/user/unlink")
                    .header("Authorization", "KakaoAK " + adminKey)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("📌 [unlinkKakaoUser] kakaoId={} 카카오 연동 해제 완료", kakaoId);
        } catch (WebClientResponseException e) {
            log.error("📌 [unlinkKakaoUser] kakaoId={} 카카오 연동 해제 실패: {}", kakaoId, e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_UNLINK_FAILED);
        }
    }
}