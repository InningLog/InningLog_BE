package com.inninglog.inninglog.kakao;

import com.inninglog.inninglog.global.auth.service.AuthTempStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthTempController {

    private final AuthTempStorage authTempStorage;

    @GetMapping("/temp")
    public ResponseEntity<?> getTempAuth(@RequestParam("id") String uuid) {
        Storage dto = authTempStorage.get(uuid);
        if (dto == null) {
            log.info("에러");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 토큰이 존재하지 않거나 만료되었습니다.");
        }

        // 사용 후 삭제 원하면 아래 주석 해제
        // authTempStorage.remove(uuid);

        log.info(uuid + "요청 들어옴");
        return ResponseEntity.ok(dto);
    }
}