package nus.iss.se.team9.report_review_service.controller;

import nus.iss.se.team9.report_review_service.model.*;
import nus.iss.se.team9.report_review_service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;

    // Report recipes
    @GetMapping("/reportRecipe/{recipeId}")
    public ResponseEntity<RecipeReport> getReportRecipe(@PathVariable(value = "recipeId") Integer recipeId, HttpSession sessionObj) {
        RecipeReport report = new RecipeReport();
        Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
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
    public ResponseEntity<MemberReport> getReportMember(@PathVariable Integer memberId, HttpSession sessionObj) {
        MemberReport report = new MemberReport();
        Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
        Member reportedMember = userService.getMemberById(memberId);
        report.setMember(member);
        report.setMemberReported(reportedMember);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/reportMember")
    public ResponseEntity<String> reportMember(@RequestBody MemberReport report) {
        reportService.reportMembers(report);
        return ResponseEntity.ok("Member reported successfully");  // 返回操作成功的消息
    }
}