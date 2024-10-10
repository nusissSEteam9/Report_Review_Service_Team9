package nus.iss.se.team9.report_review_service.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.report_review_service.model.Member;
import nus.iss.se.team9.report_review_service.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
@Transactional
public class UserService {
    private final String userServiceUrl;
    private final RestTemplate restTemplate;
    @Autowired
    public UserService(RestTemplate restTemplate,@Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public Member getMemberById(int id) {
        String url = userServiceUrl + "/member/" + id;
        try {
            ResponseEntity<Member> response = restTemplate.exchange(url, HttpMethod.GET, null, Member.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Member not found with ID: " + id);
            return null;
        } catch (HttpClientErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            throw e;
        } catch (Exception e) {
            System.out.println("Error occurred while retrieving member: " + e.getMessage());
            throw new RuntimeException("Error occurred while retrieving member: " + e.getMessage());
        }
    }
}