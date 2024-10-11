package nus.iss.se.team9.report_review_service.controller;

import nus.iss.se.team9.report_review_service.model.*;
import nus.iss.se.team9.report_review_service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;
    private final RecipeService recipeService;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public ReportController(ReportService reportService, RecipeService recipeService, UserService userService, JwtService jwtService) {
        this.reportService = reportService;
        this.recipeService = recipeService;
        this.userService = userService;
        this.jwtService = jwtService;
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
            Optional<RecipeReport> report = reportService.getRecipeReportById(id);
            return report.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
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

    @PostMapping(" /rejectRecipeReportById")
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

    @PostMapping(" /rejectMemberReportById")
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
    public ResponseEntity<String> reportRecipe(@RequestBody RecipeReport report) {
        reportService.reportRecipe(report);
        return ResponseEntity.ok("Recipe reported successfully");
    }

    @PostMapping("/reportMember")
    public ResponseEntity<String> reportMember(@RequestBody MemberReport report) {
        reportService.reportMember(report);
        return ResponseEntity.ok("Member reported successfully");
    }

    @GetMapping("/reportRecipe/{recipeId}")
    public ResponseEntity<RecipeReport> getReportRecipe(@PathVariable(value = "recipeId") Integer recipeId,@RequestHeader("Authorization") String token) {
        RecipeReport report = new RecipeReport();
        Member member = userService.getMemberById(jwtService.extractId(token));
        Recipe recipe = recipeService.getRecipeById(recipeId);
        report.setMember(member);
        report.setRecipeReported(recipe);
        return ResponseEntity.ok(report);
    }

    // Report members
    @GetMapping("/reportMember/{memberId}")
    public ResponseEntity<MemberReport> getReportMember(@PathVariable Integer memberId,@RequestHeader("Authorization") String token) {
        MemberReport report = new MemberReport();
        Member member = userService.getMemberById(jwtService.extractId(token));
        Member reportedMember = userService.getMemberById(memberId);
        report.setMember(member);
        report.setMemberReported(reportedMember);
        return ResponseEntity.ok(report);
    }


}