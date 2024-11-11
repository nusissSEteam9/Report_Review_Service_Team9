package nus.iss.se.team9.report_review_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ReportReviewServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ReportReviewServiceApplication.class, args);
	}
	
}
