package com.inninglog.inninglog.kbo.controller;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.kbo.dto.gameReport.GameReportResDto;
import com.inninglog.inninglog.kbo.service.GameReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class GameReportController {

    private final GameReportService gameReportService;

    @GetMapping("/main")
    public ResponseEntity<?> main(
            @AuthenticationPrincipal CustomUserDetails user){
        GameReportResDto gameReportResDto = gameReportService.caculateWin(user.getMember().getId());

        return ResponseEntity.ok(gameReportResDto);
    }
}
