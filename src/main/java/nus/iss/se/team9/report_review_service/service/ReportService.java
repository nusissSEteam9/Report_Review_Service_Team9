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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReportService {
	@Autowired
	AdminService adminService;
	@Autowired
	RecipeReportRepository recipeReportRepository;
	@Autowired
	MemberReportRepository memberReportRepository;
	@Autowired
	ReportRepository reportRepository;
	@Value("${email.service.url}")
	private String emailServiceUrl;

	public List<MemberReport> findApprovedMemberReportsByMemberReported(Member member){
		return memberReportRepository.findByMemberReportedAndStatus(member, Status.APPROVED);
	}

	public List<RecipeReport> findAllPendingRecipeReports() {
		List<RecipeReport> reports = recipeReportRepository.findByStatus(Status.PENDING);
		return reports != null ? reports : Collections.emptyList();
	}

	public List<MemberReport> findAllPendingMemberReports() {
		List<MemberReport> reports = memberReportRepository.findByStatus(Status.PENDING);
		return reports != null ? reports : Collections.emptyList();
	}

	public Optional<RecipeReport> getRecipeReportById(Integer recipeReportId){
		return recipeReportRepository.findById(recipeReportId);
	}

	public Optional<MemberReport> getMemberReportById(Integer memberReportId){
		return memberReportRepository.findById(memberReportId);
	}

	public Optional<RecipeReport> approveRecipeReportById(Integer recipeReportId) {
		Optional<RecipeReport> reportOptional = recipeReportRepository.findById(recipeReportId);
		if (reportOptional.isPresent()) {
			RecipeReport report = reportOptional.get();
			report.setStatus(Status.APPROVED);
			recipeReportRepository.save(report);
			return Optional.of(report);
		} else {
			return Optional.empty();
		}
	}

	public Optional<RecipeReport> rejectRecipeReportById(Integer recipeReportId) {
		Optional<RecipeReport> reportOptional = recipeReportRepository.findById(recipeReportId);
		if (reportOptional.isPresent()) {
			RecipeReport report = reportOptional.get();
			report.setStatus(Status.REJECTED);
			recipeReportRepository.save(report);
			return Optional.of(report);
		} else {
			return Optional.empty();
		}
	}

	public Optional<MemberReport> approveMemberReportById(Integer memberReportId) {
		Optional<MemberReport> reportOptional = memberReportRepository.findById(memberReportId);
		if (reportOptional.isPresent()) {
			MemberReport report = reportOptional.get();
			report.setStatus(Status.APPROVED);
			memberReportRepository.save(report);
			return Optional.of(report);
		} else {
			return Optional.empty();
		}
	}

	public Optional<MemberReport> rejectMemberReportById(Integer memberReportId) {
		Optional<MemberReport> reportOptional = memberReportRepository.findById(memberReportId);
		if (reportOptional.isPresent()) {
			MemberReport report = reportOptional.get();
			report.setStatus(Status.REJECTED);
			memberReportRepository.save(report);
			return Optional.of(report);
		} else {
			return Optional.empty();
		}
	}

	public void reportRecipe(RecipeReport report) {
		report.setStatus(Status.PENDING);
		report.setReason(report.getReason().trim());
		recipeReportRepository.save(report);

		//send email to member who created the recipe
		if(report.getRecipeReported().getMember().getEmail()!=null) {
			EmailDetails emailDetailsToMember = new EmailDetails();
			emailDetailsToMember.setTo(report.getRecipeReported().getMember().getEmail());
			emailDetailsToMember.setSubject("Your Recipe has been reported！");
			emailDetailsToMember.setBody("Dear member " + report.getMember().getUsername() + ",\n" + "Your recipe has been reported!\n"
					+ "Reason:\"" + report.getReason() + "\",\n" + "Please login to check!");
			sendEmail(emailDetailsToMember);
		}
		try {
			List<Admin> admins = adminService.getAllAdmin();

			// send email to each admin
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
		}catch (RuntimeException e) {
			System.out.println("Error occurred while fetching admin list and sending email: " + e.getMessage());
		}
	}

	public void reportMember(MemberReport report) {
		report.setStatus(Status.PENDING);
		report.setReason(report.getReason().trim());
		memberReportRepository.save(report);

		// send email to member who is reported
		if(report.getMemberReported().getEmail()!=null) {
			EmailDetails emailDetailsToMember = new EmailDetails();
			emailDetailsToMember.setTo(report.getMemberReported().getEmail());
			emailDetailsToMember.setSubject("You've been reported! please login to check!");
			emailDetailsToMember.setBody("Dear member " + report.getMemberReported().getUsername() + ",\n"
					+ "You have been reported!\n" + "Reason:\"" + report.getReason() + "\",\n"
					+ "Please login to check!");
			sendEmail(emailDetailsToMember);
		}

		try {
			List<Admin> admins = adminService.getAllAdmin();

			// send email to each admin
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
		} catch (RuntimeException e) {
			System.out.println("Error occurred while fetching admin list and sending email: " + e.getMessage());
		}
	}

	public void sendEmail(EmailDetails emailDetails){
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<EmailDetails> request = new HttpEntity<>(emailDetails, headers);
		String url = emailServiceUrl + "/sendEmailOTP";
		ResponseEntity<String> emailResponse = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
	}
}
