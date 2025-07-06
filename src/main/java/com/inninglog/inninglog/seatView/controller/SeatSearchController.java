package com.inninglog.inninglog.seatView.controller;

import com.inninglog.inninglog.seatView.dto.SeatSearchRequest;
import com.inninglog.inninglog.seatView.dto.SeatSearchResponse;
import com.inninglog.inninglog.seatView.service.SeatSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatSearchController {

    private final SeatSearchService seatSearchService;

    @GetMapping("/search")
    public ResponseEntity<SeatSearchResponse> searchSeats(
            @RequestParam String stadiumShortCode,
            @RequestParam(required = false) String zoneShortCode,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String seatRow
    ) {
        SeatSearchRequest request = SeatSearchRequest.builder()
                .stadiumShortCode(stadiumShortCode)
                .zoneShortCode(zoneShortCode)
                .section(section)
                .seatRow(seatRow)
                .build();

        SeatSearchResponse response = seatSearchService.searchSeats(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<SeatSearchResponse> searchSeats(@RequestBody SeatSearchRequest request) {
        SeatSearchResponse response = seatSearchService.searchSeats(request);
        return ResponseEntity.ok(response);
    }
}
