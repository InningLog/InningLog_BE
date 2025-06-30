package com.inninglog.inninglog.seatView;

import com.inninglog.inninglog.seatView.domain.Zone;
import com.inninglog.inninglog.seatView.repository.ZoneRepository;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.stadium.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(3)
public class ZoneInitializer implements ApplicationRunner {

    private final ZoneRepository zoneRepository;
    private final StadiumRepository stadiumRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (zoneRepository.count() != 0) return;

        Map<String, Stadium> s = stadiumRepository.findAll().stream()
                .collect(Collectors.toMap(Stadium::getShortCode, st -> st));

        zoneRepository.saveAll(List.of(
                // LG 트윈스 & 두산 베어스 (JAM - 잠실야구장)
                Zone.builder().name("중앙석 (프리미엄석)").shortCode("JAM_PREMIUM").stadium(s.get("JAM")).build(),
                Zone.builder().name("테이블석").shortCode("JAM_TABLE").stadium(s.get("JAM")).build(),
                Zone.builder().name("익사이팅존").shortCode("JAM_EXCITING").stadium(s.get("JAM")).build(),
                Zone.builder().name("블루석").shortCode("JAM_BLUE").stadium(s.get("JAM")).build(),
                Zone.builder().name("오렌지석").shortCode("JAM_ORANGE").stadium(s.get("JAM")).build(),
                Zone.builder().name("레드석").shortCode("JAM_RED").stadium(s.get("JAM")).build(),
                Zone.builder().name("네이비석").shortCode("JAM_NAVY").stadium(s.get("JAM")).build(),
                Zone.builder().name("그린석 (좌석)").shortCode("JAM_GREEN").stadium(s.get("JAM")).build(),

                // 키움 히어로즈 (GOC - 고척스카이돔)
                Zone.builder().name("스카이박스").shortCode("GOC_SKYBOX").stadium(s.get("GOC")).build(),
                Zone.builder().name("R.d-dub").shortCode("GOC_RDDUB").stadium(s.get("GOC")).build(),
                Zone.builder().name("LEXUS 1층 테이블석").shortCode("GOC_LEXUS1").stadium(s.get("GOC")).build(),
                Zone.builder().name("LEXUS 2층 테이블석").shortCode("GOC_LEXUS2").stadium(s.get("GOC")).build(),
                Zone.builder().name("NAVER 2층 테이블석").shortCode("GOC_NAVER").stadium(s.get("GOC")).build(),
                Zone.builder().name("내야커플석").shortCode("GOC_INFIELD_COUPLE").stadium(s.get("GOC")).build(),
                Zone.builder().name("외야커플석").shortCode("GOC_OUTFIELD_COUPLE").stadium(s.get("GOC")).build(),
                Zone.builder().name("다크버건디석").shortCode("GOC_DARK_BURGUNDY").stadium(s.get("GOC")).build(),
                Zone.builder().name("버건디석").shortCode("GOC_BURGUNDY").stadium(s.get("GOC")).build(),
                Zone.builder().name("3층 지정석").shortCode("GOC_3F").stadium(s.get("GOC")).build(),
                Zone.builder().name("4층 지정석").shortCode("GOC_4F").stadium(s.get("GOC")).build(),
                Zone.builder().name("휠체어석").shortCode("GOC_WHEELCHAIR").stadium(s.get("GOC")).build(),
                Zone.builder().name("외야 지정석").shortCode("GOC_OUTFIELD").stadium(s.get("GOC")).build(),
                Zone.builder().name("외야 패밀리석").shortCode("GOC_OUTFIELD_FAMILY").stadium(s.get("GOC")).build(),
                Zone.builder().name("외야 유아동반석").shortCode("GOC_OUTFIELD_BABY").stadium(s.get("GOC")).build(),

                // SSG 랜더스 (ICN - 인천SSG랜더스필드)
                Zone.builder().name("4층 SKY뷰석").shortCode("ICN_SKY_VIEW").stadium(s.get("ICN")).build(),
                Zone.builder().name("내야 필드석").shortCode("ICN_INFIELD_FIELD").stadium(s.get("ICN")).build(),
                Zone.builder().name("외야 필드석").shortCode("ICN_OUTFIELD_FIELD").stadium(s.get("ICN")).build(),
                Zone.builder().name("SKY탁자석").shortCode("ICN_SKY_TABLE").stadium(s.get("ICN")).build(),
                Zone.builder().name("미니스카이박스").shortCode("ICN_MINI_SKYBOX").stadium(s.get("ICN")).build(),
                Zone.builder().name("외야패밀리존").shortCode("ICN_OUTFIELD_FAMILY").stadium(s.get("ICN")).build(),
                Zone.builder().name("이마트 프렌들리존").shortCode("ICN_EMART_FRIENDLY").stadium(s.get("ICN")).build(),
                Zone.builder().name("랜더스 라이브존").shortCode("ICN_LANDERS_LIVE").stadium(s.get("ICN")).build(),
                Zone.builder().name("피코크 테이블석(1층)").shortCode("ICN_PEACOCK_1F").stadium(s.get("ICN")).build(),
                Zone.builder().name("노브랜드 테이블석(2층)").shortCode("ICN_NOBRAND_2F").stadium(s.get("ICN")).build(),
                Zone.builder().name("덕아웃 상단석").shortCode("ICN_DUGOUT_UPPER").stadium(s.get("ICN")).build(),
                Zone.builder().name("몰리스 그린존").shortCode("ICN_MOLLIS_GREEN").stadium(s.get("ICN")).build(),
                Zone.builder().name("으쓱이존").shortCode("ICN_EUSSEUK").stadium(s.get("ICN")).build(),
                Zone.builder().name("원정응원석").shortCode("ICN_AWAY").stadium(s.get("ICN")).build(),
                Zone.builder().name("홈런커플존").shortCode("ICN_HOMERUN_COUPLE").stadium(s.get("ICN")).build(),
                Zone.builder().name("스카이박스").shortCode("ICN_SKYBOX").stadium(s.get("ICN")).build(),
                Zone.builder().name("오픈 바비큐존").shortCode("ICN_OPEN_BBQ").stadium(s.get("ICN")).build(),
                Zone.builder().name("이마트바비큐존").shortCode("ICN_EMART_BBQ").stadium(s.get("ICN")).build(),
                Zone.builder().name("요기요 내야패밀리존").shortCode("ICN_YOGIYO_FAMILY").stadium(s.get("ICN")).build(),
                Zone.builder().name("초가정자").shortCode("ICN_CHOGA").stadium(s.get("ICN")).build(),
                Zone.builder().name("로케트배터리 외야파티덱").shortCode("ICN_ROCKET_PARTY").stadium(s.get("ICN")).build(),

                // KT 위즈 (SUW - 수원KT위즈파크)
                Zone.builder().name("포수 뒤 테이블석").shortCode("SUW_CATCHER_TABLE").stadium(s.get("SUW")).build(),
                Zone.builder().name("중앙 테이블석").shortCode("SUW_CENTER_TABLE").stadium(s.get("SUW")).build(),
                Zone.builder().name("1루/3루 테이블석").shortCode("SUW_BASE_TABLE").stadium(s.get("SUW")).build(),
                Zone.builder().name("하이파이브존").shortCode("SUW_HIGH_FIVE").stadium(s.get("SUW")).build(),
                Zone.builder().name("익사이팅석").shortCode("SUW_EXCITING").stadium(s.get("SUW")).build(),
                Zone.builder().name("중앙 지정석").shortCode("SUW_CENTER").stadium(s.get("SUW")).build(),
                Zone.builder().name("응원 지정석").shortCode("SUW_CHEER").stadium(s.get("SUW")).build(),
                Zone.builder().name("내야 지정석").shortCode("SUW_INFIELD").stadium(s.get("SUW")).build(),
                Zone.builder().name("스카이존").shortCode("SUW_SKY").stadium(s.get("SUW")).build(),
                Zone.builder().name("외야 테이블석").shortCode("SUW_OUTFIELD_TABLE").stadium(s.get("SUW")).build(),
                Zone.builder().name("외야 잔디자유석").shortCode("SUW_OUTFIELD_GRASS").stadium(s.get("SUW")).build(),

                // 한화 이글스 (DJN - 한화생명이글스파크)
                Zone.builder().name("포수 후면석").shortCode("DJN_CATCHER_BACK").stadium(s.get("DJN")).build(),
                Zone.builder().name("중앙 지정석").shortCode("DJN_CENTER").stadium(s.get("DJN")).build(),
                Zone.builder().name("중앙 탁자석").shortCode("DJN_CENTER_TABLE").stadium(s.get("DJN")).build(),
                Zone.builder().name("내야 지정석A").shortCode("DJN_INFIELD_A").stadium(s.get("DJN")).build(),
                Zone.builder().name("내야 지정석B").shortCode("DJN_INFIELD_B").stadium(s.get("DJN")).build(),
                Zone.builder().name("내야 박스석").shortCode("DJN_INFIELD_BOX").stadium(s.get("DJN")).build(),
                Zone.builder().name("내야 커플석").shortCode("DJN_INFIELD_COUPLE").stadium(s.get("DJN")).build(),
                Zone.builder().name("내야 탁자석(4층)").shortCode("DJN_INFIELD_TABLE_4F").stadium(s.get("DJN")).build(),
                Zone.builder().name("카스존(응원단석)").shortCode("DJN_CASS_CHEER").stadium(s.get("DJN")).build(),
                Zone.builder().name("이닝스 VIP바 & 룸/테라스").shortCode("DJN_INNINGS_VIP").stadium(s.get("DJN")).build(),
                Zone.builder().name("스카이박스").shortCode("DJN_SKYBOX").stadium(s.get("DJN")).build(),
                Zone.builder().name("외야지정석").shortCode("DJN_OUTFIELD").stadium(s.get("DJN")).build(),
                Zone.builder().name("밤켈존(잔디석)").shortCode("DJN_BAMBKEL_GRASS").stadium(s.get("DJN")).build(),
                Zone.builder().name("외야탁자석").shortCode("DJN_OUTFIELD_TABLE").stadium(s.get("DJN")).build(),

                // 삼성 라이온즈 (DAE - 대구삼성라이온즈파크)
                Zone.builder().name("SKY 요기보 패밀리존").shortCode("DAE_SKY_YOGIBO").stadium(s.get("DAE")).build(),
                Zone.builder().name("SKY 하단 지정석").shortCode("DAE_SKY_LOWER").stadium(s.get("DAE")).build(),
                Zone.builder().name("3루 SKY 상단 지정석").shortCode("DAE_3B_SKY_UPPER").stadium(s.get("DAE")).build(),
                Zone.builder().name("중앙 SKY 상단 지정석").shortCode("DAE_CENTER_SKY_UPPER").stadium(s.get("DAE")).build(),
                Zone.builder().name("1루 SKY 상단 지정석").shortCode("DAE_1B_SKY_UPPER").stadium(s.get("DAE")).build(),
                Zone.builder().name("스윗박스").shortCode("DAE_SWEET_BOX").stadium(s.get("DAE")).build(),
                Zone.builder().name("파티플로어 라이브석").shortCode("DAE_PARTY_LIVE").stadium(s.get("DAE")).build(),
                Zone.builder().name("VIP석").shortCode("DAE_VIP").stadium(s.get("DAE")).build(),
                Zone.builder().name("으뜸병원 중앙 테이블석").shortCode("DAE_EUTEUM_CENTER").stadium(s.get("DAE")).build(),
                Zone.builder().name("이수그룹 3루 테이블석").shortCode("DAE_ISU_3B").stadium(s.get("DAE")).build(),
                Zone.builder().name("이수페타시스 1루 테이블석").shortCode("DAE_ISU_PETASYS_1B").stadium(s.get("DAE")).build(),
                Zone.builder().name("3루 익사이팅석").shortCode("DAE_3B_EXCITING").stadium(s.get("DAE")).build(),
                Zone.builder().name("1루 익사이팅석").shortCode("DAE_1B_EXCITING").stadium(s.get("DAE")).build(),
                Zone.builder().name("블루존").shortCode("DAE_BLUE").stadium(s.get("DAE")).build(),
                Zone.builder().name("원정 응원석").shortCode("DAE_AWAY").stadium(s.get("DAE")).build(),
                Zone.builder().name("1루 내야지정석").shortCode("DAE_1B_INFIELD").stadium(s.get("DAE")).build(),
                Zone.builder().name("휠체어 장애인석").shortCode("DAE_WHEELCHAIR").stadium(s.get("DAE")).build(),
                Zone.builder().name("외야 패밀리석").shortCode("DAE_OUTFIELD_FAMILY").stadium(s.get("DAE")).build(),
                Zone.builder().name("외야 테이블석").shortCode("DAE_OUTFIELD_TABLE").stadium(s.get("DAE")).build(),
                Zone.builder().name("외야 지정석").shortCode("DAE_OUTFIELD").stadium(s.get("DAE")).build(),
                Zone.builder().name("외야 커플 테이블석").shortCode("DAE_OUTFIELD_COUPLE").stadium(s.get("DAE")).build(),
                Zone.builder().name("루프탑 테이블석").shortCode("DAE_ROOFTOP").stadium(s.get("DAE")).build(),

                // 롯데 자이언츠 (BUS - 사직야구장)
                Zone.builder().name("그라운드석").shortCode("BUS_GROUND").stadium(s.get("BUS")).build(),
                Zone.builder().name("중앙탁자석").shortCode("BUS_CENTER_TABLE").stadium(s.get("BUS")).build(),
                Zone.builder().name("와이드탁자석").shortCode("BUS_WIDE_TABLE").stadium(s.get("BUS")).build(),
                Zone.builder().name("응원탁자석").shortCode("BUS_CHEER_TABLE").stadium(s.get("BUS")).build(),
                Zone.builder().name("내야탁자석").shortCode("BUS_INFIELD_TABLE").stadium(s.get("BUS")).build(),
                Zone.builder().name("3루 단체석").shortCode("BUS_3B_GROUP").stadium(s.get("BUS")).build(),
                Zone.builder().name("내야필드석").shortCode("BUS_INFIELD_FIELD").stadium(s.get("BUS")).build(),
                Zone.builder().name("내야상단석").shortCode("BUS_INFIELD_UPPER").stadium(s.get("BUS")).build(),
                Zone.builder().name("로케트 배터리존").shortCode("BUS_ROCKET_BATTERY").stadium(s.get("BUS")).build(),
                Zone.builder().name("외야석").shortCode("BUS_OUTFIELD").stadium(s.get("BUS")).build(),
                Zone.builder().name("중앙상단석").shortCode("BUS_CENTER_UPPER").stadium(s.get("BUS")).build(),
                Zone.builder().name("휠체어석").shortCode("BUS_WHEELCHAIR").stadium(s.get("BUS")).build(),

                // 기아 타이거즈 (GWJ - 광주기아챔피언스필드)
                Zone.builder().name("챔피언석").shortCode("GWJ_CHAMPION").stadium(s.get("GWJ")).build(),
                Zone.builder().name("중앙테이블석").shortCode("GWJ_CENTER_TABLE").stadium(s.get("GWJ")).build(),
                Zone.builder().name("장애인지정석").shortCode("GWJ_DISABLED").stadium(s.get("GWJ")).build(),
                Zone.builder().name("K9").shortCode("GWJ_K9").stadium(s.get("GWJ")).build(),
                Zone.builder().name("K8").shortCode("GWJ_K8").stadium(s.get("GWJ")).build(),
                Zone.builder().name("K5").shortCode("GWJ_K5").stadium(s.get("GWJ")).build(),
                Zone.builder().name("서프라이즈석").shortCode("GWJ_SURPRISE").stadium(s.get("GWJ")).build(),
                Zone.builder().name("타이거즈가족석").shortCode("GWJ_TIGERS_FAMILY").stadium(s.get("GWJ")).build(),
                Zone.builder().name("휠체어석").shortCode("GWJ_WHEELCHAIR").stadium(s.get("GWJ")).build(),
                Zone.builder().name("4층파티석").shortCode("GWJ_4F_PARTY").stadium(s.get("GWJ")).build(),
                Zone.builder().name("스카이박스").shortCode("GWJ_SKYBOX").stadium(s.get("GWJ")).build(),
                Zone.builder().name("스카이피크닉석").shortCode("GWJ_SKY_PICNIC").stadium(s.get("GWJ")).build(),
                Zone.builder().name("EV").shortCode("GWJ_EV").stadium(s.get("GWJ")).build(),
                Zone.builder().name("5층 테이블석").shortCode("GWJ_5F_TABLE").stadium(s.get("GWJ")).build(),
                Zone.builder().name("외야석").shortCode("GWJ_OUTFIELD").stadium(s.get("GWJ")).build(),
                Zone.builder().name("외야테이블석").shortCode("GWJ_OUTFIELD_TABLE").stadium(s.get("GWJ")).build(),

                // NC 다이노스 (CHW - 창원NC파크)
                Zone.builder().name("내야석").shortCode("CHW_INFIELD").stadium(s.get("CHW")).build(),
                Zone.builder().name("테이블석").shortCode("CHW_TABLE").stadium(s.get("CHW")).build(),
                Zone.builder().name("라운드 테이블석").shortCode("CHW_ROUND_TABLE").stadium(s.get("CHW")).build(),
                Zone.builder().name("외야잔디석").shortCode("CHW_OUTFIELD_GRASS").stadium(s.get("CHW")).build(),
                Zone.builder().name("외야석").shortCode("CHW_OUTFIELD").stadium(s.get("CHW")).build(),
                Zone.builder().name("3·4층 내야석").shortCode("CHW_3_4F_INFIELD").stadium(s.get("CHW")).build(),
                Zone.builder().name("휠체어석").shortCode("CHW_WHEELCHAIR").stadium(s.get("CHW")).build(),
                Zone.builder().name("미니테이블석").shortCode("CHW_MINI_TABLE").stadium(s.get("CHW")).build(),
                Zone.builder().name("가족석").shortCode("CHW_FAMILY").stadium(s.get("CHW")).build(),
                Zone.builder().name("스카이박스").shortCode("CHW_SKYBOX").stadium(s.get("CHW")).build(),
                Zone.builder().name("불펜 가족석").shortCode("CHW_BULLPEN_FAMILY").stadium(s.get("CHW")).build(),
                Zone.builder().name("불펜석").shortCode("CHW_BULLPEN").stadium(s.get("CHW")).build(),
                Zone.builder().name("카운터석").shortCode("CHW_COUNTER").stadium(s.get("CHW")).build(),
                Zone.builder().name("ABL생명 프리미엄석").shortCode("CHW_ABL_PREMIUM").stadium(s.get("CHW")).build(),
                Zone.builder().name("ABL생명 프리미엄 테이블석").shortCode("CHW_ABL_PREMIUM_TABLE").stadium(s.get("CHW")).build(),
                Zone.builder().name("바베큐석").shortCode("CHW_BBQ").stadium(s.get("CHW")).build(),
                Zone.builder().name("피크닉테이블석").shortCode("CHW_PICNIC_TABLE").stadium(s.get("CHW")).build(),
                Zone.builder().name("노스피크캠핑석").shortCode("CHW_NORTH_PEAK_CAMPING").stadium(s.get("CHW")).build()
        ));
    }
}