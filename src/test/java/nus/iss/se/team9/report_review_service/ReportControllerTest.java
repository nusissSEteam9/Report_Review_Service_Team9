package nus.iss.se.team9.report_review_service;


import nus.iss.se.team9.report_review_service.controller.ReportController;
import nus.iss.se.team9.report_review_service.model.Member;
import nus.iss.se.team9.report_review_service.model.MemberReport;
import nus.iss.se.team9.report_review_service.model.RecipeReport;
import nus.iss.se.team9.report_review_service.request.ReportMemberRequest;
import nus.iss.se.team9.report_review_service.request.ReportRecipeRequest;
import nus.iss.se.team9.report_review_service.service.JwtService;
import nus.iss.se.team9.report_review_service.service.ReportService;
import nus.iss.se.team9.report_review_service.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ReportControllerTest {

    @InjectMocks
    private ReportController reportController;

    @Mock
    private ReportService reportService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Test
    public void testGetMemberReportsByMemberReported_Success() {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("memberId", 1);

        Member member = new Member();
        List<MemberReport> reports = List.of(new MemberReport());

        when(userService.getMemberById(1)).thenReturn(member);
        when(reportService.findApprovedMemberReportsByMemberReported(member)).thenReturn(reports);

        ResponseEntity<List<MemberReport>> response = reportController.getMemberReportsByMemberReported(requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetMemberReportsByMemberReported_MemberNotFound() {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("memberId", 1);

        when(userService.getMemberById(1)).thenReturn(null);

        ResponseEntity<List<MemberReport>> response = reportController.getMemberReportsByMemberReported(requestBody);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllRecipeReports_NoReports() {
        when(reportService.findAllPendingRecipeReports()).thenReturn(Collections.emptyList());

        ResponseEntity<List<RecipeReport>> response = reportController.getAllRecipeReports();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testGetAllRecipeReports_Success() {
        List<RecipeReport> reports = List.of(new RecipeReport());
        when(reportService.findAllPendingRecipeReports()).thenReturn(reports);

        ResponseEntity<List<RecipeReport>> response = reportController.getAllRecipeReports();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetRecipeReportById_Success() {
        RecipeReport report = new RecipeReport();
        when(reportService.getRecipeReportById(1)).thenReturn(report);

        ResponseEntity<RecipeReport> response = reportController.getRecipeReportById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(report, response.getBody());
    }

    @Test
    public void testGetRecipeReportById_NotFound() {
        when(reportService.getRecipeReportById(1)).thenReturn(null);

        ResponseEntity<RecipeReport> response = reportController.getRecipeReportById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testReportRecipe_Success() {
        ReportRecipeRequest request = new ReportRecipeRequest();
        request.setReason("Test Reason");
        request.setRecipeReportedId(1);

        when(jwtService.extractId("token")).thenReturn(1);
        when(reportService.reportRecipe(1, 1, "Test Reason")).thenReturn(1);

        ResponseEntity<String> response = reportController.reportRecipe(request, "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Recipe reported successfully", response.getBody());
    }

    @Test
    public void testReportMember_Success() {
        ReportMemberRequest request = new ReportMemberRequest();
        request.setReason("Test Reason");
        request.setMemberReportedId(1);

        when(jwtService.extractId("token")).thenReturn(1);
        when(reportService.reportMember(1, 1, "Test Reason")).thenReturn(1);

        ResponseEntity<String> response = reportController.reportMember(request, "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Member reported successfully", response.getBody());
    }

    @Test
    public void testGetMemberReportById_Success() {
        Optional<MemberReport> report = Optional.of(new MemberReport());
        when(reportService.getMemberReportById(1)).thenReturn(report);

        ResponseEntity<MemberReport> response = reportController.getMemberReportById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(report.get(), response.getBody());
    }

    @Test
    public void testGetMemberReportById_NotFound() {
        when(reportService.getMemberReportById(1)).thenReturn(Optional.empty());
        ResponseEntity<MemberReport> response = reportController.getMemberReportById(1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
