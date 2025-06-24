package com.inninglog.inninglog.member.domain;

import com.inninglog.inninglog.global.entity.BaseTimeEntity;
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
    private String kakao_nickname;

    //카카오 프로필 링크
    private String kakao_profile_url;

    //서비스 내 닉네임
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

}
