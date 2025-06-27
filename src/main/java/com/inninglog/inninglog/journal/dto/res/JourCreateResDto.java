package com.inninglog.inninglog.journal.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourCreateResDto {

    @Schema(description = "직관 일지 Id", example = "12")
    private Long journalId;
}