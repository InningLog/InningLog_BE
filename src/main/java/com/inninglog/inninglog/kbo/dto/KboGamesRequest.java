// KboGamesRequest.java
package com.inninglog.inninglog.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KboGamesRequest {

    private String gameDate; // YYYY-MM-DD

    private List<KboGameDto> games;
}
