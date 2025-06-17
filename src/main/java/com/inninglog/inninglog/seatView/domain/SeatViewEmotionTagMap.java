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
public class SeatViewEmotionTagMap extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seatView_id")
    private SeatView seatView;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seatViewEmotionTag_id")
    private SeatViewEmotionTag seatViewEmotionTag;
}
