package nus.iss.se.team9.report_review_service.controller;

import nus.iss.se.team9.report_review_service.model.*;
import nus.iss.se.team9.report_review_service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/searchByMember")
    public ResponseEntity<List<MemberReport>> getReportsByMember(@RequestBody Member member) {
        try {
            List<MemberReport> reports =reportService.findApprovedByMember(member);
            if (reports.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(reports);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Report recipes
    @GetMapping("/reportRecipe/{recipeId}")
    public ResponseEntity<RecipeReport> getReportRecipe(@PathVariable(value = "recipeId") Integer recipeId,@RequestHeader("Authorization") String token) {
        RecipeReport report = new RecipeReport();
        Member member = userService.getMemberById(jwtService.extractId(token));
        Recipe recipe = recipeService.getRecipeById(recipeId);
        report.setMember(member);
        report.setRecipeReported(recipe);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/reportRecipe")
    public ResponseEntity<String> reportRecipe(@RequestBody RecipeReport report) {
        reportService.reportRecipes(report);
        return ResponseEntity.ok("Recipe reported successfully");
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

    @PostMapping("/reportMember")
    public ResponseEntity<String> reportMember(@RequestBody MemberReport report) {
        reportService.reportMembers(report);
        return ResponseEntity.ok("Member reported successfully");
    }
}