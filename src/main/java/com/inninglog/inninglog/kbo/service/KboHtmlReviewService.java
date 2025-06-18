package com.inninglog.inninglog.kbo.service;

import com.inninglog.inninglog.kbo.dto.HitterStatDto;
import com.inninglog.inninglog.kbo.dto.PitcherStatDto;
import com.inninglog.inninglog.kbo.dto.ReviewStatsDto;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KboHtmlReviewService {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public KboHtmlReviewService() {
        System.setProperty("webdriver.chrome.driver", "/Users/haeseung/chromedriver-mac-arm64/chromedriver");
        this.driver = new ChromeDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @PreDestroy
    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * 리뷰 페이지에서 선수별 투·타자 기록을 모두 파싱합니다.
     * @param reviewUrl "https://www.koreabaseball.com/...ReviewNew.aspx?gameId=XXX"
     * @return ReviewStatsDto (투수 목록 + 타자 목록)
     */
    public ReviewStatsDto getReviewStats(String reviewUrl) {
        ReviewStatsDto stats = new ReviewStatsDto();

        driver.get(reviewUrl);
        // Ajax로 로드된 테이블이 뜰 때까지 충분히 대기
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table.tbl")));

        // 모든 ".tbl" 테이블 순회
        List<WebElement> tables = driver.findElements(By.cssSelector("table.tbl"));
        for (WebElement table : tables) {
            // 1) 테이블의 헤더 텍스트 목록
            List<String> headers = table.findElements(By.cssSelector("thead th"))
                    .stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());

            boolean isPitcher = headers.contains("이닝") && headers.contains("자책");
            boolean isHitter  = headers.contains("타수") && headers.contains("안타");
            if (!isPitcher && !isHitter) {
                // 투수·타자 기록 테이블이 아니면 건너뜀
                continue;
            }

            // 2) 헤더명 → 열 인덱스 맵
            Map<String, Integer> colIndex = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                colIndex.put(headers.get(i), i);
            }

            // 3) 선수명 컬럼 헤더 찾기 ("선수명", "타자명", "투수명" 등)
            String nameKey = headers.stream()
                    .filter(h -> h.contains("선수명") || h.contains("타자명") || h.contains("투수명"))
                    .findFirst()
                    .orElse(headers.get(0));  // 못 찾으면 첫 컬럼

            String currentTeam = "";
            List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));

            for (WebElement row : rows) {
                // 4) 팀 헤더 감지: th[colspan] 또는 td[colspan]
                if (!row.findElements(By.cssSelector("th[colspan], td[colspan]")).isEmpty()) {
                    currentTeam = row.findElement(By.cssSelector("th[colspan], td[colspan]"))
                            .getText().trim();
                    continue;
                }

                // 5) 선수명(th[scope=row]) + 나머지 td 합치기
                List<WebElement> dataCells = new ArrayList<>();
                dataCells.addAll(row.findElements(By.cssSelector("th[scope=row]")));
                dataCells.addAll(row.findElements(By.tagName("td")));

                // 6) 선수명: 헤더 기반 인덱스로 꺼내기
                String playerName = dataCells
                        .get(colIndex.get(nameKey))
                        .getText().trim();

                if (isPitcher) {
                    // 투수: 이닝, 자책
                    String innings = dataCells.get(colIndex.get("이닝")).getText().trim();
                    int earned    = Integer.parseInt(
                            dataCells.get(colIndex.get("자책")).getText().trim()
                    );
                    stats.getPitchers()
                            .add(new PitcherStatDto(currentTeam, playerName, innings, earned));

                } else {
                    // 타자: 타수, 안타
                    int atBats = Integer.parseInt(
                            dataCells.get(colIndex.get("타수")).getText().trim()
                    );
                    int hits   = Integer.parseInt(
                            dataCells.get(colIndex.get("안타")).getText().trim()
                    );
                    stats.getBatters()
                            .add(new HitterStatDto(currentTeam, playerName, atBats, hits));
                }
            }
        }

        log.info("파싱 완료: 투수 {}명, 타자 {}명",
                stats.getPitchers().size(),
                stats.getBatters().size());
        return stats;
    }
}