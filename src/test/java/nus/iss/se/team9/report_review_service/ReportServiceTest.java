package nus.iss.se.team9.report_review_service;

import nus.iss.se.team9.report_review_service.model.*;
import nus.iss.se.team9.report_review_service.repo.*;
import nus.iss.se.team9.report_review_service.service.AdminService;
import nus.iss.se.team9.report_review_service.service.RecipeService;
import nus.iss.se.team9.report_review_service.service.ReportService;
import nus.iss.se.team9.report_review_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private AdminService adminService;

    @Mock
    private RecipeReportRepository recipeReportRepository;

    @Mock
    private MemberReportRepository memberReportRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserService userService;

    @Mock
    private RecipeService recipeService;

    private Member member;
    private Recipe recipe;
    private RecipeReport recipeReport;
    private MemberReport memberReport;

    @BeforeEach
    public void setUp() {
        member = new Member();
        member.setId(1);
        member.setUsername("TestUser");

        recipe = new Recipe();
        recipe.setId(1);
        recipe.setName("Test Recipe");

        recipeReport = new RecipeReport();
        recipeReport.setId(1);
        recipeReport.setRecipeReported(recipe);
        recipeReport.setMember(member);
        recipeReport.setStatus(Status.PENDING);
        recipeReport.setReason("Test Reason");

        memberReport = new MemberReport();
        memberReport.setId(1);
        memberReport.setMemberReported(member);
        memberReport.setMember(member);
        memberReport.setStatus(Status.PENDING);
        memberReport.setReason("Test Reason");
    }

    @Test
    public void testFindApprovedMemberReportsByMemberReported() {
        when(memberReportRepository.findByMemberReportedAndStatus(member, Status.APPROVED))
                .thenReturn(Collections.singletonList(memberReport));

        List<MemberReport> reports = reportService.findApprovedMemberReportsByMemberReported(member);

        assertEquals(1, reports.size());
        assertEquals(memberReport, reports.getFirst());
    }

    @Test
    public void testFindAllPendingRecipeReports() {
        when(recipeReportRepository.findByStatus(Status.PENDING))
                .thenReturn(Collections.singletonList(recipeReport));

        List<RecipeReport> reports = reportService.findAllPendingRecipeReports();

        assertEquals(1, reports.size());
        assertEquals(recipeReport, reports.getFirst());
    }

    @Test
    public void testFindAllPendingMemberReports() {
        when(memberReportRepository.findByStatus(Status.PENDING))
                .thenReturn(Collections.singletonList(memberReport));

        List<MemberReport> reports = reportService.findAllPendingMemberReports();

        assertEquals(1, reports.size());
        assertEquals(memberReport, reports.getFirst());
    }

    @Test
    public void testGetRecipeReportById_Found() {
        when(recipeReportRepository.findById(1)).thenReturn(Optional.of(recipeReport));

        RecipeReport foundReport = reportService.getRecipeReportById(1);

        assertNotNull(foundReport);
        assertEquals(recipeReport, foundReport);
    }

    @Test
    public void testGetRecipeReportById_NotFound() {
        when(recipeReportRepository.findById(1)).thenReturn(Optional.empty());

        RecipeReport foundReport = reportService.getRecipeReportById(1);

        assertNull(foundReport);
    }

    @Test
    public void testGetMemberReportById_Found() {
        when(memberReportRepository.findById(1)).thenReturn(Optional.of(memberReport));

        Optional<MemberReport> foundReport = reportService.getMemberReportById(1);

        assertTrue(foundReport.isPresent());
        assertEquals(memberReport, foundReport.get());
    }

    @Test
    public void testApproveRecipeReportById_Found() {
        when(recipeReportRepository.findById(1)).thenReturn(Optional.of(recipeReport));

        Optional<RecipeReport> approvedReport = reportService.approveRecipeReportById(1);

        assertTrue(approvedReport.isPresent());
        assertEquals(Status.APPROVED, approvedReport.get().getStatus());
        verify(recipeReportRepository, times(1)).save(recipeReport);
    }

    @Test
    public void testApproveRecipeReportById_NotFound() {
        when(recipeReportRepository.findById(1)).thenReturn(Optional.empty());

        Optional<RecipeReport> approvedReport = reportService.approveRecipeReportById(1);

        assertFalse(approvedReport.isPresent());
    }

    @Test
    public void testRejectRecipeReportById_Found() {
        when(recipeReportRepository.findById(1)).thenReturn(Optional.of(recipeReport));

        Optional<RecipeReport> rejectedReport = reportService.rejectRecipeReportById(1);

        assertTrue(rejectedReport.isPresent());
        assertEquals(Status.REJECTED, rejectedReport.get().getStatus());
        verify(recipeReportRepository, times(1)).save(recipeReport);
    }

    @Test
    public void testRejectRecipeReportById_NotFound() {
        when(recipeReportRepository.findById(1)).thenReturn(Optional.empty());

        Optional<RecipeReport> rejectedReport = reportService.rejectRecipeReportById(1);

        assertFalse(rejectedReport.isPresent());
    }

    @Test
    public void testReportRecipe_Success() {
        when(recipeService.getRecipeById(1)).thenReturn(recipe);
        when(userService.getMemberById(1)).thenReturn(member);
        when(recipeReportRepository.save(any(RecipeReport.class))).thenAnswer(invocation -> {
            RecipeReport savedReport = invocation.getArgument(0);
            savedReport.setId(1);
            return savedReport;
        });

        int reportId = reportService.reportRecipe(1, 1, "Test Reason");

        assertEquals(1, reportId);
        verify(recipeReportRepository, times(1)).save(any(RecipeReport.class));
    }

    @Test
    public void testReportMember_Success() {
        when(userService.getMemberById(1)).thenReturn(member);
        when(memberReportRepository.save(any(MemberReport.class))).thenAnswer(invocation -> {
            MemberReport savedReport = invocation.getArgument(0);
            savedReport.setId(1);
            return savedReport;
        });

        int reportId = reportService.reportMember(1, 1, "Test Reason");

        assertEquals(1, reportId);
        verify(memberReportRepository, times(1)).save(any(MemberReport.class));
    }
}
