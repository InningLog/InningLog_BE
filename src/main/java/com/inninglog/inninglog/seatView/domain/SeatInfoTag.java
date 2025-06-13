package com.inninglog.inninglog.seatView.domain;

import com.inninglog.inninglog.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 생성
@NoArgsConstructor//매개변수가 없는 기본 생성자를 자동으로 생성
@Getter
@Setter
public class SeatInfoTag extends BaseTimeEntity{ //좌석 시야 정보 태그 분리한거
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //사용자 노출용
    private String tag;

}
