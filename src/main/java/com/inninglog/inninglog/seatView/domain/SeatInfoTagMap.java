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
public class SeatInfoTagMap extends BaseTimeEntity { //좌석 시야 정보 중간 매핑

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seatView_id")
    private SeatView seatView;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seatInfoTag_id")
    private SeatInfoTag seatInfoTag;
}
