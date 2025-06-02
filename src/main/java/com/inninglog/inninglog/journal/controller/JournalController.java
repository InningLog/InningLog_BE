package com.inninglog.inninglog.journal.controller;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.dto.JourCreateReqDto;
import com.inninglog.inninglog.journal.dto.JourCreateResDto;
import com.inninglog.inninglog.journal.service.JournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/journals")
@Tag(name = "Journal", description = "직관 일지 관련 API")
public class JournalController {

    private final JournalService journalService;

    //직관 일지 생성
    @Operation(
            summary = "직관 일지 생성",
            description = "JWT 토큰에서 유저 정보를 추출하여 직관 일지를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "직관 일지 생성 성공",
                    content = @Content(schema = @Schema(implementation = JourCreateResDto.class))),
            @ApiResponse(responseCode = "404", description = "회원 또는 팀/경기장 정보 없음",
                    content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<JourCreateResDto> createJournal(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "직관 일지 생성 요청 DTO",
                    required = true,
                    content = @Content(schema = @Schema(implementation = JourCreateReqDto.class))
            )
            @RequestBody JourCreateReqDto request
    ) {
        Journal journal = journalService.createJournal(user.getMember().getId(), request);
        return ResponseEntity.status(201).body(new JourCreateResDto(journal.getId()));
    }
}