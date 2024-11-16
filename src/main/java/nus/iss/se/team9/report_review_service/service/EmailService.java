package nus.iss.se.team9.report_review_service.service;

import nus.iss.se.team9.report_review_service.model.EmailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class EmailService {
    private final String emailServiceUrl;
    private final RestTemplate restTemplate;
    public EmailService(@Value("${email.service.url}") String emailServiceUrl,RestTemplate restTemplate){
        this.emailServiceUrl = emailServiceUrl;
        this.restTemplate =restTemplate;
    }
    public void sendEmail(EmailDetails emailDetails) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<EmailDetails> request = new HttpEntity<>(emailDetails, headers);
        String url = emailServiceUrl + "/sendEmailOTP";
        restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }
}
