package nus.iss.se.team9.report_review_service.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.report_review_service.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Transactional
public class AdminService {
    private final String adminServiceUrl;
    private final RestTemplate restTemplate;
    @Autowired
    public AdminService(RestTemplate restTemplate,@Value("${admin.service.url}") String adminServiceUrl) {
        this.restTemplate = restTemplate;
        this.adminServiceUrl = adminServiceUrl;
    }

    public List<Admin> getAllAdmin(){
        System.out.println("Go to admin-api");
        String url = adminServiceUrl + "/getAdminList";
        ResponseEntity<List<Admin>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch admins from admin-api. Status code: " + response.getStatusCode());
        }
    }
}
