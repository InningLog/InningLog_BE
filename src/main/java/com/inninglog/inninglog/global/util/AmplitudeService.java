package com.inninglog.inninglog.global.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Service
public class AmplitudeService {

    //엠플리튜드 키 값
    @Value("${amplitude.api-key}")
    private String apiKey;

    //엠플리튜드 이벤트를 보내는 서버 주소, 여기에 JSON을 post요청으로 보내면됨
    private static final String ENDPOINT = "https://api2.amplitude.com/2/httpapi";

    //이벤트를 전송하는 메서드
    // ex. amplitudeService.log("user_login", "user-123", Map.of("method", "kakao"))
    public void log(String eventType, String userId, Map<String, Object> eventProperties) {
        try {
            //이멘트 정보를 담을 JSON 객체 만듬
            JSONObject event = new JSONObject();

            //엠플리튜드가 요구하는 3가지 필드
            event.put("user_id", userId); //누가했는지
            event.put("event_type", eventType); //어떤 이벤트인지
            event.put("event_properties", new JSONObject(eventProperties)); //부가 정보

            //여러개의 이벤트를 보내야 할 수도 있기 때문에 리스트로 감싸줌 ?
            JSONArray events = new JSONArray();
            events.put(event);

            //엠플리튜드 서버에 보낼 최종 제이슨 구조를 만듬.
            JSONObject payload = new JSONObject();
            payload.put("api_key", this.apiKey);
            payload.put("events", events);

            //위에 만든 제이슨 데이터를 실제로 HTTP 요청으로 보내는 함수
            sendHttpPost(payload);
        } catch (Exception e) {
            e.printStackTrace(); // 운영 시에는 logger로 교체 추천
        }
    }

    // 엠플리튜드 서버에 데이터를 실제로 보내는 함수
    private void sendHttpPost(JSONObject payload) throws Exception {
        URL url = new URL(ENDPOINT);

        //위에서 정의한 https://api2.amplitude.com/2/httpapi 주소에 연결을 시작
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Post요청이란걸 설정, 보내는 데이터 형식이 Json이라고 알림
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        //만든 제이슨을 엠플리튜드 서버에 전송하는 부분
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.toString().getBytes());
            os.flush();
        }

        // 엠플리튜드가 응답을 보내면 그 상태 코드를 확인함.
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            System.err.println("⚠️ Amplitude 응답 코드: " + responseCode);
        }
    }
}