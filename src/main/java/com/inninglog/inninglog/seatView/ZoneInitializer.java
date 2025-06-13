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
                Zone.builder().name("중앙석(VIP)").shortCode("JAM_VIP").stadium(s.get("JAM")).build(),
                Zone.builder().name("테이블석").shortCode("JAM_TABLE").stadium(s.get("JAM")).build(),
                Zone.builder().name("익사이팅존").shortCode("JAM_EXCITING").stadium(s.get("JAM")).build(),
                Zone.builder().name("블루석").shortCode("JAM_BLUE").stadium(s.get("JAM")).build(),
                Zone.builder().name("레드석").shortCode("JAM_RED").stadium(s.get("JAM")).build(),
                Zone.builder().name("네이비석").shortCode("JAM_NAVY").stadium(s.get("JAM")).build(),
                Zone.builder().name("그린석(외야)").shortCode("JAM_GREEN").stadium(s.get("JAM")).build(),

                Zone.builder().name("VIP석").shortCode("GOC_VIP").stadium(s.get("GOC")).build(),
                Zone.builder().name("테이블석").shortCode("GOC_TABLE").stadium(s.get("GOC")).build(),
                Zone.builder().name("내야 지정석").shortCode("GOC_INFIELD").stadium(s.get("GOC")).build(),
                Zone.builder().name("외야 지정석").shortCode("GOC_OUTFIELD").stadium(s.get("GOC")).build(),
                Zone.builder().name("외야 비지정석").shortCode("GOC_OUTFIELD_FREE").stadium(s.get("GOC")).build(),
                Zone.builder().name("로얄 다이아몬드 클럽").shortCode("GOC_ROYAL").stadium(s.get("GOC")).build(),

                Zone.builder().name("내야석").shortCode("ICN_INFIELD").stadium(s.get("ICN")).build(),
                Zone.builder().name("외야 지정석").shortCode("ICN_OUTFIELD").stadium(s.get("ICN")).build(),
                Zone.builder().name("바비큐존").shortCode("ICN_BBQ").stadium(s.get("ICN")).build(),
                Zone.builder().name("홈런 커플존").shortCode("ICN_COUPLE").stadium(s.get("ICN")).build(),

                Zone.builder().name("내야석").shortCode("SUW_INFIELD").stadium(s.get("SUW")).build(),
                Zone.builder().name("외야자유석").shortCode("SUW_OUTFIELD_FREE").stadium(s.get("SUW")).build(),
                Zone.builder().name("테이블석").shortCode("SUW_TABLE").stadium(s.get("SUW")).build(),

                Zone.builder().name("내야석").shortCode("DJN_INFIELD").stadium(s.get("DJN")).build(),
                Zone.builder().name("외야석").shortCode("DJN_OUTFIELD").stadium(s.get("DJN")).build(),

                Zone.builder().name("VIP석").shortCode("DAE_VIP").stadium(s.get("DAE")).build(),
                Zone.builder().name("중앙테이블석").shortCode("DAE_CENTRAL_TABLE").stadium(s.get("DAE")).build(),
                Zone.builder().name("내야 테이블(1·3루)").shortCode("DAE_INFIELD_TABLE").stadium(s.get("DAE")).build(),
                Zone.builder().name("블루존").shortCode("DAE_BLUE").stadium(s.get("DAE")).build(),
                Zone.builder().name("익사이팅석").shortCode("DAE_EXCITING").stadium(s.get("DAE")).build(),
                Zone.builder().name("원정응원석").shortCode("DAE_AWAY").stadium(s.get("DAE")).build(),
                Zone.builder().name("SKY존").shortCode("DAE_SKY").stadium(s.get("DAE")).build(),
                Zone.builder().name("외야지정석").shortCode("DAE_OUTFIELD").stadium(s.get("DAE")).build(),
                Zone.builder().name("외야테이블석").shortCode("DAE_OUTFIELD_TABLE").stadium(s.get("DAE")).build(),
                Zone.builder().name("파티플로어 테이블").shortCode("DAE_PARTY").stadium(s.get("DAE")).build(),
                Zone.builder().name("잔디그린존").shortCode("DAE_GREEN").stadium(s.get("DAE")).build(),

                Zone.builder().name("내야석").shortCode("BUS_INFIELD").stadium(s.get("BUS")).build(),
                Zone.builder().name("외야석").shortCode("BUS_OUTFIELD").stadium(s.get("BUS")).build(),
                Zone.builder().name("테이블석").shortCode("BUS_TABLE").stadium(s.get("BUS")).build(),

                Zone.builder().name("응원존").shortCode("CHW_CHEER").stadium(s.get("CHW")).build(),
                Zone.builder().name("네이비존").shortCode("CHW_NAVY").stadium(s.get("CHW")).build(),
                Zone.builder().name("티블루석").shortCode("CHW_TEABLUE").stadium(s.get("CHW")).build(),
                Zone.builder().name("그린존").shortCode("CHW_GREEN").stadium(s.get("CHW")).build(),
                Zone.builder().name("스카이박스").shortCode("CHW_SKYBOX").stadium(s.get("CHW")).build(),
                Zone.builder().name("프리미엄석").shortCode("CHW_PREMIUM").stadium(s.get("CHW")).build(),
                Zone.builder().name("가족석").shortCode("CHW_FAMILY").stadium(s.get("CHW")).build(),
                Zone.builder().name("바베큐석").shortCode("CHW_BBQ").stadium(s.get("CHW")).build(),

                Zone.builder().name("내야석").shortCode("GWJ_INFIELD").stadium(s.get("GWJ")).build(),
                Zone.builder().name("외야석").shortCode("GWJ_OUTFIELD").stadium(s.get("GWJ")).build(),
                Zone.builder().name("테이블석").shortCode("GWJ_TABLE").stadium(s.get("GWJ")).build()
        ));
    }
}