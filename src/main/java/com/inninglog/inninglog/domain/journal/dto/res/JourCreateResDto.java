package com.inninglog.inninglog.domain.journal.dto.res;

import com.inninglog.inninglog.domain.journal.domain.Journal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourCreateResDto {

    @Schema(description = "직관 일지 Id", example = "12")
    private Long journalId;

    public static JourCreateResDto from(Journal journal) {
        return JourCreateResDto.builder()
                .journalId(journal.getId())
                .build();
    }
}