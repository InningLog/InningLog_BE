package com.inninglog.inninglog.member;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;

@Data
@Entity
public class Member {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long kakaoId;

    private String nickname;
    private String profileImageUrl;
}
