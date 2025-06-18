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
import java.util.List;
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
        if (driver != null) driver.quit();
    }

    /**
     * 리뷰 페이지에서 선수별 투·타자 기록을 모두 파싱합니다.
     * @param reviewUrl "https://www.koreabaseball.com/...ReviewNew.aspx?gameId=XXX"
     * @return ReviewStatsDto (투수 목록 + 타자 목록)
     */
    public ReviewStatsDto getReviewStats(String reviewUrl) {
        ReviewStatsDto stats = new ReviewStatsDto();

        driver.get(reviewUrl);
        // 테이블이 최소 하나 로드될 때까지 대기
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table.tbl")));

        // 모든 ".tbl" 테이블 순회
        List<WebElement> tables = driver.findElements(By.cssSelector("table.tbl"));
        for (WebElement table : tables) {
            // 테이블 헤더 텍스트 목록
            List<String> headers = table.findElements(By.cssSelector("thead th"))
                    .stream().map(WebElement::getText).collect(Collectors.toList());

            boolean isPitcher = headers.contains("이닝") && headers.contains("자책");
            boolean isHitter  = headers.contains("타수") && headers.contains("안타");
            if (!isPitcher && !isHitter) {
                // 해당 테이블이 투·타자 기록 테이블이 아니면 건너뛰기
                continue;
            }

            String currentTeam = "";
            List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));
            for (WebElement row : rows) {
                // 팀 소속 헤더 (th) 로만 이루어진 행이면 팀 이름 갱신
                if (!row.findElements(By.tagName("th")).isEmpty()) {
                    currentTeam = row.findElement(By.tagName("th")).getText().trim();
                    continue;
                }

                // 일반 데이터 행
                List<WebElement> cells = row.findElements(By.tagName("td"));
                String playerName = cells.get(0).getText().trim();

                if (isPitcher) {
                    int idxInnings = headers.indexOf("이닝");
                    int idxEarned  = headers.indexOf("자책");
                    String innings = cells.get(idxInnings).getText().trim();
                    int earned    = Integer.parseInt(cells.get(idxEarned).getText().trim());
                    stats.getPitchers().add(new PitcherStatDto(currentTeam, playerName, innings, earned));

                } else {
                    int idxAB   = headers.indexOf("타수");
                    int idxHits = headers.indexOf("안타");
                    int atBats  = Integer.parseInt(cells.get(idxAB).getText().trim());
                    int hits    = Integer.parseInt(cells.get(idxHits).getText().trim());
                    stats.getBatters().add(new HitterStatDto(currentTeam, playerName, atBats, hits));
                }
            }
        }

        log.info("파싱 완료: 투수 {}명, 타자 {}명",
                stats.getPitchers().size(), stats.getBatters().size());
        return stats;
    }
}