package com.inninglog.inninglog.seatView.domain;

import com.inninglog.inninglog.global.entity.BaseTimeEntity;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.stadium.domain.Stadium;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 생성
@NoArgsConstructor//매개변수가 없는 기본 생성자를 자동으로 생성
@Getter
@Setter
public class SeatView extends BaseTimeEntity { //직관 일지

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id")
    private Journal journal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    //시야 사진 url
    private String view_media_url;

    //좌석 정보 태그 원본
    private String seat_description;
}
