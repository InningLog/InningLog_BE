package com.inninglog.inninglog.global.auth.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import com.inninglog.inninglog.kakao.Storage;
import org.springframework.stereotype.Component;

@Component
public class AuthTempStorage {

    private final Map<String, TempAuthData> storage = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public String save(Storage authResDto, long ttlSeconds) {
        String uuid = UUID.randomUUID().toString();
        TempAuthData data = new TempAuthData(authResDto, Instant.now().plusSeconds(ttlSeconds));
        storage.put(uuid, data);

        scheduler.schedule(() -> storage.remove(uuid), ttlSeconds, TimeUnit.SECONDS);

        return uuid;
    }

    public Storage get(String uuid) {
        TempAuthData data = storage.get(uuid);
        if (data == null || data.getExpiry().isBefore(Instant.now())) {
            storage.remove(uuid);
            return null;
        }
        return data.getAuthResDto();
    }

    public void remove(String uuid) {
        storage.remove(uuid);
    }

    private static class TempAuthData {
        private final Storage authResDto;
        private final Instant expiry;

        public TempAuthData(Storage authResDto, Instant expiry) {
            this.authResDto = authResDto;
            this.expiry = expiry;
        }

        public Storage getAuthResDto() {
            return authResDto;
        }

        public Instant getExpiry() {
            return expiry;
        }
    }
}