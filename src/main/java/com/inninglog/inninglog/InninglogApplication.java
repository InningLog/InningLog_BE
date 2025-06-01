package com.inninglog.inninglog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(
		info = @Info(title = "이닝로그 API", version = "v1", description = "이닝로그 백엔드 API 문서")
)
@EnableJpaAuditing
@SpringBootApplication
public class InninglogApplication {

	public static void main(String[] args) {
		SpringApplication.run(InninglogApplication.class, args);
	}

}
