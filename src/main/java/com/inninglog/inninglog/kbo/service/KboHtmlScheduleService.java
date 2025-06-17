package com.inninglog.inninglog.kbo.service;

import com.inninglog.inninglog.kbo.dto.KboGameDto;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KboHtmlScheduleService {

    private final WebDriver driver;

    public KboHtmlScheduleService() {
        System.setProperty("webdriver.chrome.driver", "/Users/haeseung/chromedriver-mac-arm64/chromedriver");
        this.driver = new ChromeDriver();
    }

    @PreDestroy
    public void closeDriver() {
        if (driver != null) driver.quit();
    }

    private String toKboDateFormat(String inputDate) {
        LocalDate date = LocalDate.parse(inputDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
        return formatter.format(date);
    }

    public List<KboGameDto> getGamesByDate(String dateString) {
        List<KboGameDto> list = new ArrayList<>();

        try {
            String dateParam = (dateString == null || dateString.isBlank())
                    ? LocalDate.now().toString()
                    : dateString;

            String targetKboDate = toKboDateFormat(dateParam);
            String url = "https://www.koreabaseball.com/Schedule/Schedule.aspx?date=" + dateParam;
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.tbl")));

            List<WebElement> rows = driver.findElements(By.cssSelector("table.tbl tbody tr"));
            String currentDate = "";
            boolean isTargetDate = false;

            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                WebElement row = rows.get(rowIndex);
                List<WebElement> tds = row.findElements(By.tagName("td"));
                if (tds.size() < 6) continue;

                try {
                    String firstTd = tds.get(0).getText().trim();
                    log.debug("Row {}: First TD = '{}', Total TDs = {}", rowIndex, firstTd, tds.size());

                    // 날짜가 포함된 행 (첫 번째 경기)
                    if (firstTd.matches("\\d{2}\\.\\d{2}.*")) {
                        currentDate = firstTd.substring(0, 5);
                        isTargetDate = currentDate.equals(targetKboDate);

                        if (!isTargetDate) continue;

                        // 첫 번째 경기 정보 파싱
                        KboGameDto game = parseFirstGameRow(tds, currentDate);
                        if (game != null) {
                            list.add(game);
                            log.info("첫 번째 경기 파싱 성공: {}", game);
                        }

                    }
                    // 시간만 있는 행 (두 번째 이후 경기)
                    else if (firstTd.matches("\\d{2}:\\d{2}") && isTargetDate) {
                        KboGameDto game = parseSubsequentGameRow(tds, currentDate);
                        if (game != null) {
                            list.add(game);
                            log.info("후속 경기 파싱 성공: {}", game);
                        }
                    }

                } catch (Exception e) {
                    log.error("Row {} 파싱 실패: {}", rowIndex, e.getMessage());
                    // 디버깅을 위해 해당 행의 모든 TD 내용 출력
                    debugRowContents(tds, rowIndex);
                }
            }

        } catch (Exception e) {
            log.error("전체 크롤링 실패", e);
        }

        log.info("총 {}개 경기 파싱 완료", list.size());
        return list;
    }

    /**
     * 첫 번째 경기 행 파싱 (날짜 포함)
     * 구조: [날짜] [시간] [경기정보] [기타...] [게임센터] [기타...] [구장]
     */
    private KboGameDto parseFirstGameRow(List<WebElement> tds, String currentDate) {
        try {
            if (tds.size() < 8) {
                log.warn("첫 번째 경기 행의 TD 개수 부족: {}", tds.size());
                return null;
            }

            String time = tds.get(1).getText().trim();
            String matchInfo = tds.get(2).getText().trim();
            String stadium = tds.get(7).getText().trim();

            // 리뷰 URL은 여러 위치를 시도해서 찾기
            String reviewUrl = findReviewUrlInRow(tds);

            return parseGameInfo(currentDate, time, matchInfo, stadium, reviewUrl);

        } catch (Exception e) {
            log.error("첫 번째 경기 행 파싱 실패", e);
            return null;
        }
    }

    /**
     * 두 번째 이후 경기 행 파싱 (날짜 없음)
     * 구조: [시간] [경기정보] [기타...] [게임센터] [기타...] [구장]
     */
    private KboGameDto parseSubsequentGameRow(List<WebElement> tds, String currentDate) {
        try {
            if (tds.size() < 7) {
                log.warn("후속 경기 행의 TD 개수 부족: {}", tds.size());
                return null;
            }

            String time = tds.get(0).getText().trim();
            String matchInfo = tds.get(1).getText().trim();
            String stadium = tds.get(6).getText().trim();

            // 리뷰 URL은 여러 위치를 시도해서 찾기
            String reviewUrl = findReviewUrlInRow(tds);

            return parseGameInfo(currentDate, time, matchInfo, stadium, reviewUrl);

        } catch (Exception e) {
            log.error("후속 경기 행 파싱 실패", e);
            return null;
        }
    }

    /**
     * 행 전체에서 리뷰 URL을 찾는 메서드
     * 여러 TD를 순회하며 "게임센터" 또는 "리뷰" 링크를 찾음
     */
    private String findReviewUrlInRow(List<WebElement> tds) {
        // 가능한 모든 TD에서 링크 찾기
        for (int i = 0; i < tds.size(); i++) {
            try {
                WebElement td = tds.get(i);
                List<WebElement> links = td.findElements(By.tagName("a"));

                for (WebElement link : links) {
                    String href = link.getAttribute("href");
                    String linkText = link.getText().trim();

                    // 게임센터, 리뷰, 하이라이트 등의 링크 찾기
                    if (href != null && (
                            href.contains("gameId=") ||
                                    href.contains("section=HIGHLIGHT") ||
                                    href.contains("section=REVIEW") ||
                                    linkText.contains("게임센터") ||
                                    linkText.contains("리뷰")
                    )) {
                        // HIGHLIGHT → REVIEW 로 강제 치환
                        if (href.contains("section=HIGHLIGHT")) {
                            href = href.replace("section=HIGHLIGHT", "section=REVIEW");
                        }

                        String finalUrl = href.startsWith("http") ? href : "https://www.koreabaseball.com" + href;
                        log.debug("리뷰 URL 발견 (TD {}): {}", i, finalUrl);
                        return finalUrl;
                    }
                }
            } catch (Exception e) {
                // 해당 TD에서 링크 찾기 실패 - 다음 TD 시도
                continue;
            }
        }

        log.warn("리뷰 URL을 찾을 수 없음");
        return null;
    }

    /**
     * 디버깅용: 행의 모든 TD 내용 출력
     */
    private void debugRowContents(List<WebElement> tds, int rowIndex) {
        log.debug("=== Row {} Debug Info ===", rowIndex);
        for (int i = 0; i < tds.size(); i++) {
            try {
                String text = tds.get(i).getText().trim();
                List<WebElement> links = tds.get(i).findElements(By.tagName("a"));
                String linkInfo = links.isEmpty() ? "No links" : links.size() + " links";
                log.debug("  TD[{}]: '{}' ({})", i, text, linkInfo);
            } catch (Exception e) {
                log.debug("  TD[{}]: Error reading content", i);
            }
        }
        log.debug("========================");
    }

    private KboGameDto parseGameInfo(String date, String time, String matchInfo, String stadium, String reviewUrl) {
        try {
            if (!matchInfo.contains("vs")) return null;
            String[] parts = matchInfo.split("vs");
            if (parts.length != 2) return null;

            String leftPart = parts[0].trim();
            String rightPart = parts[1].trim();

            String awayTeam = "", awayScore = "";
            for (int i = leftPart.length() - 1; i >= 0; i--) {
                if (Character.isDigit(leftPart.charAt(i))) {
                    awayScore = leftPart.substring(i) + awayScore;
                } else {
                    awayTeam = leftPart.substring(0, i + 1);
                    awayScore = leftPart.substring(i + 1);
                    break;
                }
            }

            String homeScore = "", homeTeam = "";
            for (int i = 0; i < rightPart.length(); i++) {
                if (Character.isDigit(rightPart.charAt(i))) {
                    homeScore += rightPart.charAt(i);
                } else {
                    homeTeam = rightPart.substring(i);
                    break;
                }
            }

            int awayScoreInt = Integer.parseInt(awayScore);
            int homeScoreInt = Integer.parseInt(homeScore);

            return new KboGameDto(awayTeam, homeTeam, awayScoreInt, homeScoreInt, stadium, time, reviewUrl);

        } catch (Exception e) {
            log.error("경기 정보 파싱 실패: {} → {}", matchInfo, e.getMessage());
            return null;
        }
    }

    /**
     * 날짜 범위의 KBO 경기 일정을 조회합니다.
     */
    public List<KboGameDto> getGamesByDateRange(String startDate, String endDate) {
        List<KboGameDto> allGames = new ArrayList<>();

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            LocalDate current = start;
            while (!current.isAfter(end)) {
                log.info("크롤링 중: {}", current);
                List<KboGameDto> dailyGames = getGamesByDate(current.toString());
                allGames.addAll(dailyGames);
                current = current.plusDays(1);

                // 서버 부하 방지를 위한 대기
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            log.error("날짜 범위 조회 중 오류 발생: {} ~ {}", startDate, endDate, e);
        }

        log.info("전체 크롤링 완료: {}개 경기", allGames.size());
        return allGames;
    }
}