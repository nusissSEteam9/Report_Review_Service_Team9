package nus.iss.se.team9.report_review_service.controller;

import nus.iss.se.team9.report_review_service.model.*;
import nus.iss.se.team9.report_review_service.request.ReportMemberRequest;
import nus.iss.se.team9.report_review_service.request.ReportRecipeRequest;
import nus.iss.se.team9.report_review_service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public ReportController(ReportService reportService, RecipeService recipeService, UserService userService, JwtService jwtService) {
        this.reportService = reportService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/health")
    public String checkHealth(){
        return "API is connected";
    }

    @PostMapping("/getMemberReportsByMemberReported")
    public ResponseEntity<List<MemberReport>> getMemberReportsByMemberReported(@RequestBody Map<String, Integer> requestBody) {
        System.out.println("get member reports by id of member reported");
        try {
            Integer memberId = requestBody.get("memberId");
            if (memberId == null) {
                return ResponseEntity.badRequest().body(null);
            }
            Member member = userService.getMemberById(memberId);
            if (member == null) {
                System.out.println("Member not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            System.out.println("Member found");
            List<MemberReport> reports = reportService.findApprovedMemberReportsByMemberReported(member);
            if (reports.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getAllRecipeReports")
    public ResponseEntity<List<RecipeReport>> getAllRecipeReports(){
        try {
            List<RecipeReport> reports =reportService.findAllPendingRecipeReports();
            if (reports.isEmpty()) {
                System.out.println("No recipe reports are pending for approval!");
                return ResponseEntity.noContent().build();
            }
            System.out.println("recipe reports pending for approval are found and displayed!");
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getAllMemberReports")
    public ResponseEntity<List<MemberReport>> getAllMemberReports(){
        try {
            List<MemberReport> reports =reportService.findAllPendingMemberReports();
            if (reports.isEmpty()) {
                System.out.println("No member reports are pending for approval!");
                return ResponseEntity.noContent().build();
            }
            System.out.println("member reports pending for approval are found and displayed!");
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getRecipeReportById")
    public ResponseEntity<RecipeReport> getRecipeReportById(@RequestParam("id") Integer id) {
        try {
            RecipeReport report = reportService.getRecipeReportById(id);
            if (report != null) {
                return ResponseEntity.ok(report);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getMemberReportById")
    public ResponseEntity<MemberReport> getMemberReportById(@RequestParam("id") Integer id) {
        try {
            Optional<MemberReport> report = reportService.getMemberReportById(id);
            return report.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/approveRecipeReportById")
    public ResponseEntity<RecipeReport> approveRecipeReportById(@RequestParam("id") Integer id) {
        try {
            Optional<RecipeReport> approvedReport = reportService.approveRecipeReportById(id);
            return approvedReport.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/rejectRecipeReportById")
    public ResponseEntity<RecipeReport> rejectRecipeReportById(@RequestParam("id") Integer id) {
        try {
            Optional<RecipeReport> approvedReport = reportService.rejectRecipeReportById(id);
            return approvedReport.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/approveMemberReportById")
    public ResponseEntity<MemberReport> approveMemberReportById(@RequestParam("id") Integer id) {
        try {
            Optional<MemberReport> approvedReport = reportService.approveMemberReportById(id);
            return approvedReport.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/rejectMemberReportById")
    public ResponseEntity<MemberReport> rejectMemberReportById(@RequestParam("id") Integer id) {
        try {
            Optional<MemberReport> approvedReport = reportService.rejectMemberReportById(id);
            return approvedReport.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/reportRecipe")
    public ResponseEntity<String> reportRecipe(@RequestBody ReportRecipeRequest request,
                                               @RequestHeader("Authorization") String token) {
        System.out.println("Processing recipe report:");
        System.out.println("Reason: " + request.getReason());
        System.out.println("Recipe reported Id: " + request.getRecipeReportedId());
        try {
            reportService.reportRecipe(request.getRecipeReportedId(), jwtService.extractId(token), request.getReason());
            return ResponseEntity.ok("Recipe reported successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + e.getMessage());
        }
    }

    @PostMapping("/reportMember")
    public ResponseEntity<String> reportMember(@RequestBody ReportMemberRequest request,
                                               @RequestHeader("Authorization") String token) {
        System.out.println("Processing member report:");
        System.out.println("Reason: " + request.getReason());
        System.out.println("Recipe reported Id: " + request.getMemberReportedId());
        try {
            Integer reporterId = jwtService.extractId(token);
            reportService.reportMember(request.getMemberReportedId(), reporterId, request.getReason());
            return ResponseEntity.ok("Member reported successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reporting member: " + e.getMessage());
        }
    }

    @GetMapping("/getRecipeIdByRecipeReportId")
    public ResponseEntity<Map<String, Integer>> getRecipeIdByRecipeReportId(@RequestParam("id") Integer reportId) {
        try {
            RecipeReport recipeReport = reportService.getRecipeReportById(reportId);
            if (recipeReport != null && recipeReport.getRecipeReported() != null) {
                Integer recipeId = recipeReport.getRecipeReported().getId();
                Map<String, Integer> response = new HashMap<>();
                response.put("recipeReportedId", recipeId);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}