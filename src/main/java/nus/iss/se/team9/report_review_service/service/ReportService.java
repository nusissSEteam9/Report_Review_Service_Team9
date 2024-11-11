package nus.iss.se.team9.report_review_service.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.report_review_service.model.*;
import nus.iss.se.team9.report_review_service.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReportService {
	private final AdminService adminService;
	private final RecipeReportRepository recipeReportRepository;
	private final MemberReportRepository memberReportRepository;
	private final ReportRepository reportRepository;
	private final String emailServiceUrl;
	private final UserService userService;
	private final RecipeService recipeService;
	
	@Autowired
	public ReportService(AdminService adminService, RecipeReportRepository recipeReportRepository,
						 MemberReportRepository memberReportRepository, ReportRepository reportRepository,
						 @Value("${email.service.url}") String emailServiceUrl, UserService userService,
						 RecipeService recipeService) {
		this.adminService = adminService;
		this.recipeReportRepository = recipeReportRepository;
		this.memberReportRepository = memberReportRepository;
		this.reportRepository = reportRepository;
		this.emailServiceUrl = emailServiceUrl;
		this.userService = userService;
		this.recipeService = recipeService;
	}
	
	public List<MemberReport> findApprovedMemberReportsByMemberReported(Member member) {
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
	
	public RecipeReport getRecipeReportById(Integer id) {
		return recipeReportRepository.findById(id)
									 .orElse(null);
	}
	
	public Optional<MemberReport> getMemberReportById(Integer memberReportId) {
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
	
	public int reportRecipe(Integer recipeReportedId, Integer memberId, String reason) {
		try {
			Recipe recipeReported = recipeService.getRecipeById(recipeReportedId);
			if (recipeReported == null) {
				throw new RuntimeException("Recipe not found with id: " + recipeReportedId);
			}
			Member member = userService.getMemberById(memberId);
			if (member == null) {
				throw new RuntimeException("Member not found with id: " + memberId);
			}
			System.out.println("Member found, member: " + member);
			System.out.println("Recipe found, recipe: " + recipeReported);
			
			RecipeReport report = new RecipeReport();
			report.setRecipeReported(recipeReported);
			report.setMember(member);
			report.setStatus(Status.PENDING);
			report.setReason(reason.trim());
			recipeReportRepository.save(report);
			return report.getId();
		} catch (Exception e) {
			System.out.println("Error occurred while reporting recipe: " + e.getMessage());
			throw new RuntimeException("Error occurred while reporting recipe: " + e.getMessage());
		}
	}
	
	@Async
	public void sendEmailsForRecipe(Integer recipeReportedId, Integer reportId) {
		Member recipeOwner = userService.getMemberById(recipeService.getMemberByRecipeId(recipeReportedId));
		RecipeReport report = recipeReportRepository.findById(reportId)
													.orElseThrow(() -> new RuntimeException(
															"Report not found with id: " + reportId));
		//send email to member who created the recipe
		if (recipeOwner.getEmail() != null) {
			EmailDetails emailDetailsToMember = new EmailDetails();
			emailDetailsToMember.setTo(recipeOwner.getEmail());
			emailDetailsToMember.setSubject("Your Recipe has been reported！");
			emailDetailsToMember.setBody(
					"Dear member " + recipeOwner.getUsername() + ",\n" + "Your recipe has been reported!\n" + "Reason:\"" + report.getReason() + "\",\n" + "Please login to check!");
			sendEmail(emailDetailsToMember);
		} else {
			System.out.println("Email not found for member: " + recipeOwner.getUsername());
			throw new RuntimeException("Email not found for member: " + recipeOwner.getUsername());
		}
		
		try {
			List<Admin> admins = adminService.getAllAdmin();
			
			// send email to each admin
			for (Admin admin : admins) {
				EmailDetails emailDetailsToAdmin = new EmailDetails();
				emailDetailsToAdmin.setTo(admin.getEmail());
				emailDetailsToAdmin.setSubject("\"One new report is created!\"");
				emailDetailsToAdmin.setBody(
						"Dear admin, There is a new recipe report created by member \"" + report.getMember()
																								.getUsername() + "\",\n" + "The number of reports pending for approval : " + reportRepository.countByStatus(
								Status.PENDING) + ",\n" + "Please login to check!");
				sendEmail(emailDetailsToAdmin);
			}
		} catch (Exception e) {
			System.out.println("Error occurred while fetching admin list and sending email: " + e.getMessage());
			throw new RuntimeException("Error occurred while fetching admin list and sending email: " + e.getMessage());
		}
	}
	
	public int reportMember(Integer memberReportedId, Integer memberId, String reason) {
		try {
			Member memberReported = userService.getMemberById(memberReportedId);
			if (memberReported == null) {
				throw new RuntimeException("Member reported not found with id: " + memberId);
			}
			Member member = userService.getMemberById(memberId);
			if (member == null) {
				throw new RuntimeException("Member not found with id: " + memberId);
			}
			
			MemberReport report = new MemberReport();
			report.setMemberReported(memberReported);
			report.setMember(member);
			report.setStatus(Status.PENDING);
			report.setReason(reason.trim());
			memberReportRepository.save(report);
			return report.getId();
		} catch (Exception e) {
			System.out.println("Error occurred while reporting member: " + e.getMessage());
			throw new RuntimeException("Error occurred while reporting member: " + e.getMessage());
		}
	}
	
	@Async
	public void sendEmailsForMember(Integer memberReportedId, Integer reportId) {
		Member memberReported = userService.getMemberById(memberReportedId);
		MemberReport report = memberReportRepository.findById(reportId)
													.orElseThrow(() -> new RuntimeException(
															"Report not found with id: " + reportId));
		Member member = report.getMember();
		// send email to member who is reported
		if (memberReported.getEmail() != null) {
			EmailDetails emailDetailsToMember = new EmailDetails();
			emailDetailsToMember.setTo(memberReported.getEmail());
			emailDetailsToMember.setSubject("You've been reported! please login to check!");
			emailDetailsToMember.setBody("Dear member " + report.getMemberReported()
																.getUsername() + ",\n" + "You have been reported!\n" + "Reason:\"" + report.getReason() + "\",\n" + "Please login to check!");
			sendEmail(emailDetailsToMember);
		}
		
		try {
			List<Admin> admins = adminService.getAllAdmin();
			
			// send email to each admin
			for (Admin admin : admins) {
				EmailDetails emailDetailsToAdmin = new EmailDetails();
				emailDetailsToAdmin.setTo(admin.getEmail());
				emailDetailsToAdmin.setSubject("One new report is created!");
				emailDetailsToAdmin.setBody(
						"Dear admin, There is a new member report created by member \"" + member.getUsername() + "\",\n" + "The number of reports pending for approval : " + reportRepository.countByStatus(
								Status.PENDING) + ",\n" + "Please login to check!");
				sendEmail(emailDetailsToAdmin);
			}
		} catch (RuntimeException e) {
			System.out.println("Error occurred while fetching admin list and sending email: " + e.getMessage());
		}
	}
	
	public void sendEmail(EmailDetails emailDetails) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<EmailDetails> request = new HttpEntity<>(emailDetails, headers);
		String url = emailServiceUrl + "/sendEmailOTP";
		restTemplate.exchange(url, HttpMethod.POST, request, String.class);
	}
}
