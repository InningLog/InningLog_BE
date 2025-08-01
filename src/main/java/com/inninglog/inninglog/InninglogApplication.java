package com.inninglog.inninglog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(
		info = @Info(title = "이닝로그 API", version = "v1", description = "이닝로그 백엔드 API 문서"),
		servers = {
				@Server(url = "https://api.inninglog.shop", description = "Production server"),
				@Server(url = "http://localhost:8080", description = "Local server"),
				@Server(url = "https://dev-api.inninglog.shop", description = "Dev server") // ✅ 이 줄 추가!
		}
)
@EnableJpaAuditing
@SpringBootApplication
public class InninglogApplication {

	public static void main(String[] args) {
		SpringApplication.run(InninglogApplication.class, args);
	}
}