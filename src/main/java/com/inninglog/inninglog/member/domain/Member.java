package com.inninglog.inninglog.member.domain;

import com.inninglog.inninglog.global.entity.BaseTimeEntity;
import com.inninglog.inninglog.kakao.KakaoUserInfoResponseDto;
import com.inninglog.inninglog.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 생성
@NoArgsConstructor//매개변수가 없는 기본 생성자를 자동으로 생성
@Getter
@Setter
public class Member extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long kakaoId;

    //카카오 닉네임
    @Column(unique = true)
    private String kakao_nickname;

    //카카오 프로필 링크
    @Column(unique = true)
    private String kakao_profile_url;

    //서비스 내 닉네임
    @Column(unique = true)
    private String nickname;

    //서비스 내 프로필 링크
    private String profile_url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MemberType memberType = MemberType.USER;

    //응원하는 팀
    @ManyToOne(fetch = FetchType.LAZY) //지연 로딩
    @JoinColumn(name = "team_id")
    private Team team;

    //기존 멤버 업데이트
    public void updateInfo(KakaoUserInfoResponseDto dto) {
        this.kakao_nickname = dto.getKakaoAccount().getProfile().getNickName();
        this.kakao_profile_url = dto.getKakaoAccount().getProfile().getProfileImageUrl();
    }

    //신규 멤버 생성
    public static Member fromKakaoDto(KakaoUserInfoResponseDto dto) {
        return Member.builder()
                .kakaoId(dto.getId())
                .kakao_nickname(dto.getKakaoAccount().getProfile().getNickName())
                .kakao_profile_url(dto.getKakaoAccount().getProfile().getProfileImageUrl())
                .nickname(dto.getKakaoAccount().getProfile().getNickName()) // 최초 닉네임은 카카오 닉네임
                .profile_url(dto.getKakaoAccount().getProfile().getProfileImageUrl()) // 최초 프로필 이미지
                .build();
    }

}
