package com.inninglog.inninglog.kbo.service;

import com.inninglog.inninglog.kbo.dto.KboGameDto;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

            String targetKboDate = toKboDateFormat(dateParam); // ex: "06.15"
            String url = "https://www.koreabaseball.com/Schedule/Schedule.aspx?date=" + dateParam;
            System.out.println(">> 최종 접속 URL: " + url);
            System.out.println(">> 찾고자 하는 날짜: " + targetKboDate);

            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.tbl")));

            List<WebElement> rows = driver.findElements(By.cssSelector("table.tbl tbody tr"));
            System.out.println(">> 크롤링된 행 수: " + rows.size());

            String currentDate = ""; // 현재 처리 중인 날짜를 저장
            boolean isTargetDate = false; // 목표 날짜인지 확인

            for (WebElement row : rows) {
                List<WebElement> tds = row.findElements(By.tagName("td"));
                if (tds.size() < 6) continue;

                try {
                    String firstTd = tds.get(0).getText().trim();

                    // 날짜가 있는 행인지 확인 (MM.dd 형식)
                    if (firstTd.matches("\\d{2}\\.\\d{2}.*")) {
                        // 새로운 날짜 시작
                        currentDate = firstTd.substring(0, 5); // "06.15" 형태로 저장
                        isTargetDate = currentDate.equals(targetKboDate);

                        System.out.println(">> 새로운 날짜 발견: " + currentDate + " (목표: " + targetKboDate + ", 매치: " + isTargetDate + ")");

                        // 목표 날짜가 아니면 스킵
                        if (!isTargetDate) {
                            continue;
                        }

                        // 이 행에 경기 정보도 있는지 확인
                        if (tds.size() >= 8) {
                            String time = tds.get(1).getText().trim();
                            String matchInfo = tds.get(2).getText().trim();
                            String stadium = tds.get(7).getText().trim();

                            KboGameDto game = parseGameInfo(currentDate, time, matchInfo, stadium);
                            if (game != null) {
                                list.add(game);
                            }
                        }
                    } else if (firstTd.matches("\\d{2}:\\d{2}") && isTargetDate) {
                        // 시간만 있는 행이면서 목표 날짜인 경우에만 처리
                        String time = firstTd;
                        String matchInfo = tds.get(1).getText().trim();
                        String stadium = tds.get(6).getText().trim();

                        KboGameDto game = parseGameInfo(currentDate, time, matchInfo, stadium);
                        if (game != null) {
                            list.add(game);
                        }
                    }

                } catch (Exception e) {
                    System.out.println(">> 행 파싱 실패 → 스킵: " + e.getMessage());
                }
            }

            System.out.println(">> 최종 필터링된 경기 수: " + list.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private KboGameDto parseGameInfo(String date, String time, String matchInfo, String stadium) {
        try {
            // 경기 정보 파싱: "팀명숫자vs숫자팀명" 형식
            // 예: "두산0vs1키움", "삼성6vs4LG"

            // vs를 기준으로 나누기
            if (!matchInfo.contains("vs")) {
                return null;
            }

            String[] parts = matchInfo.split("vs");
            if (parts.length != 2) {
                return null;
            }

            String leftPart = parts[0].trim();  // "두산0"
            String rightPart = parts[1].trim(); // "1키움"

            // 왼쪽에서 팀명과 점수 분리 (뒤에서부터 숫자 찾기)
            String awayTeam = "";
            String awayScore = "";
            for (int i = leftPart.length() - 1; i >= 0; i--) {
                if (Character.isDigit(leftPart.charAt(i))) {
                    awayScore = leftPart.substring(i) + awayScore;
                } else {
                    awayTeam = leftPart.substring(0, i + 1);
                    awayScore = leftPart.substring(i + 1);
                    break;
                }
            }

            // 오른쪽에서 점수와 팀명 분리 (앞에서부터 숫자 찾기)
            String homeScore = "";
            String homeTeam = "";
            for (int i = 0; i < rightPart.length(); i++) {
                if (Character.isDigit(rightPart.charAt(i))) {
                    homeScore += rightPart.charAt(i);
                } else {
                    homeTeam = rightPart.substring(i);
                    break;
                }
            }

            String score = awayScore + " vs " + homeScore;

            System.out.println(">> 파싱 결과: " + date + " " + time + " | " + awayTeam + " vs " + homeTeam + " (" + score + ") @ " + stadium);

            return new KboGameDto(awayTeam, homeTeam, score, stadium, time);

        } catch (Exception e) {
            System.out.println(">> 경기 정보 파싱 실패: " + matchInfo + " → " + e.getMessage());
            return null;
        }
    }

    // KboHtmlScheduleService에 추가할 메서드

    /**
     * 날짜 범위의 KBO 경기 일정을 조회합니다.
     *
     * @param startDate 시작 날짜 (YYYY-MM-DD)
     * @param endDate 종료 날짜 (YYYY-MM-DD)
     * @return KBO 경기 목록
     */
    public List<KboGameDto> getGamesByDateRange(String startDate, String endDate) {
        List<KboGameDto> allGames = new ArrayList<>();

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // 각 날짜마다 조회
            LocalDate current = start;
            while (!current.isAfter(end)) {
                List<KboGameDto> dailyGames = getGamesByDate(current.toString());
                allGames.addAll(dailyGames);
                current = current.plusDays(1);

                // 너무 많은 요청을 방지하기 위한 짧은 대기
                Thread.sleep(500);
            }

        } catch (Exception e) {
            log.error("날짜 범위 조회 중 오류 발생: {} ~ {}", startDate, endDate, e);
        }

        return allGames;
    }
}