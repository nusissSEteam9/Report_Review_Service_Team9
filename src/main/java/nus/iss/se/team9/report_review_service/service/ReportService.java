package nus.iss.se.team9.report_review_service.service;


import jakarta.transaction.Transactional;
import nus.iss.se.team9.report_review_service.model.*;
import nus.iss.se.team9.report_review_service.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Transactional
public class ReportService {
	@Autowired
	RecipeReportRepository recipeReportRepository;
	@Autowired
	MemberReportRepository memberReportRepository;
	@Autowired
	ReportRepository reportRepository;
	@Autowired
	AdminRepository adminRepository;
	@Value("${email.service.url}")
	private String emailServiceUrl;

	// report inappropriate recipes
	public void reportRecipes(RecipeReport report) {
		report.setStatus(Status.PENDING);
		report.setReason(report.getReason().trim());
		recipeReportRepository.save(report);
		if(report.getRecipeReported().getMember().getEmail()!=null) {
			EmailDetails emailDetailsToMember = new EmailDetails();
			emailDetailsToMember.setTo(report.getRecipeReported().getMember().getEmail());
			emailDetailsToMember.setSubject("Your Recipe has been reported！");
			emailDetailsToMember.setBody("Dear member " + report.getMember().getUsername() + ",\n" + "Your recipe has been reported!\n"
					+ "Reason:\"" + report.getReason() + "\",\n" + "Please login to check!");
			sendEmail(emailDetailsToMember);
		}
		List<Admin> admins = adminRepository.findAll();
		for (Admin admin : admins) {
			EmailDetails emailDetailsToAdmin = new EmailDetails();
			emailDetailsToAdmin.setTo(admin.getEmail());
			emailDetailsToAdmin.setSubject("\"One new report is created!\"");
			emailDetailsToAdmin.setBody("Dear admin, There is a new recipe report created by member \""
					+ report.getMember().getUsername() + "\",\n"
					+ "The number of reports pending for approval : " + reportRepository.countByStatus(Status.PENDING)
					+ ",\n" + "Please login to check!");
			sendEmail(emailDetailsToAdmin);
		}
	}

	// report inappropriate members
	public void reportMembers(MemberReport report) {
		report.setStatus(Status.PENDING);
		report.setReason(report.getReason().trim());
		memberReportRepository.save(report);
		if(report.getMemberReported().getEmail()!=null) {
			EmailDetails emailDetailsToMember = new EmailDetails();
			emailDetailsToMember.setTo(report.getMemberReported().getEmail());
			emailDetailsToMember.setSubject("You've been reported! please login to check!");
			emailDetailsToMember.setBody("Dear member " + report.getMemberReported().getUsername() + ",\n"
					+ "You have been reported!\n" + "Reason:\"" + report.getReason() + "\",\n"
					+ "Please login to check!");
			sendEmail(emailDetailsToMember);
		}
		List<Admin> admins = adminRepository.findAll();
		for (Admin admin : admins) {
			EmailDetails emailDetailsToAdmin = new EmailDetails();
			emailDetailsToAdmin.setTo(admin.getEmail());
			emailDetailsToAdmin.setSubject("One new report is created!");
			emailDetailsToAdmin.setBody("Dear admin, There is a new member report created by member \""
					+ report.getMember().getUsername() + "\",\n"
					+ "The number of reports pending for approval : " + reportRepository.countByStatus(Status.PENDING)
					+ ",\n" + "Please login to check!");
			sendEmail(emailDetailsToAdmin);
		}
	}
	public void sendEmail(EmailDetails emailDetails){
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<EmailDetails> request = new HttpEntity<>(emailDetails, headers);
		String url = emailServiceUrl;
		ResponseEntity<String> emailResponse = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
	}
}
