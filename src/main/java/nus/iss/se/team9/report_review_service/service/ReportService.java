package nus.iss.se.team9.report_review_service.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.report_review_service.factory.ReportFactory;
import nus.iss.se.team9.report_review_service.model.*;
import nus.iss.se.team9.report_review_service.repo.*;
import nus.iss.se.team9.report_review_service.reportObserver.ReportEvent;
import nus.iss.se.team9.report_review_service.reportObserver.ReportObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
	private final List<ReportObserver> observers = new ArrayList<>();

	public void addObserver(ReportObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(ReportObserver observer) {
		observers.remove(observer);
	}
	private void notifyObservers(Report report, String eventType) {
		ReportEvent event = new ReportEvent(report, eventType);
		for (ReportObserver observer : observers) {
			observer.onReportEvent(event);
		}
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
			notifyObservers(report, "APPROVED");
			return Optional.of(report);
		}
		return Optional.empty();
	}
	
	public Optional<RecipeReport> rejectRecipeReportById(Integer recipeReportId) {
		Optional<RecipeReport> reportOptional = recipeReportRepository.findById(recipeReportId);
		if (reportOptional.isPresent()) {
			RecipeReport report = reportOptional.get();
			report.setStatus(Status.REJECTED);
			recipeReportRepository.save(report);
			notifyObservers(report, "REJECTED");
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
			notifyObservers(report, "APPROVED");
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
			notifyObservers(report, "REJECTED");
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

			RecipeReport report = (RecipeReport) ReportFactory.createReport(ReportType.RECIPE, recipeReported, member, reason);
			recipeReportRepository.save(report);

			notifyObservers(report, "CREATED");

			return report.getId();
		} catch (Exception e) {
			System.out.println("Error occurred while reporting recipe: " + e.getMessage());
			throw new RuntimeException("Error occurred while reporting recipe: " + e.getMessage());
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

			MemberReport report = (MemberReport) ReportFactory.createReport(ReportType.MEMBER, memberReported, member, reason);
			memberReportRepository.save(report);

			notifyObservers(report, "CREATED");

			return report.getId();
		} catch (Exception e) {
			System.out.println("Error occurred while reporting member: " + e.getMessage());
			throw new RuntimeException("Error occurred while reporting member: " + e.getMessage());
		}
	}
}
