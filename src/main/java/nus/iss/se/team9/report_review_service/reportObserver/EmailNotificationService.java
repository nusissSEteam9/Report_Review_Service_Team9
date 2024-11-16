package nus.iss.se.team9.report_review_service.reportObserver;

import nus.iss.se.team9.report_review_service.model.*;
import nus.iss.se.team9.report_review_service.service.AdminService;
import nus.iss.se.team9.report_review_service.service.EmailService;

import java.util.List;

public class EmailNotificationService implements ReportObserver {
    private final EmailService emailService;
    private final AdminService adminService;

    public EmailNotificationService(EmailService emailService, AdminService adminService) {
        this.emailService = emailService;
        this.adminService = adminService;
    }

    @Override
    public void onReportEvent(ReportEvent event) {
        Report report = event.report();
        String eventType = event.eventType();

        if (report instanceof RecipeReport recipeReport) {
            handleRecipeReportEvent(recipeReport, eventType);
        } else if (report instanceof MemberReport memberReport) {
            handleMemberReportEvent(memberReport, eventType);
        }
    }

    private void handleRecipeReportEvent(RecipeReport report, String eventType) {
        switch (eventType) {
            case "CREATED" -> notifyRecipeReportCreated(report);
            case "APPROVED" -> notifyRecipeReportApproved(report);
            case "REJECTED" -> notifyRecipeReportRejected(report);
        }
    }

    private void handleMemberReportEvent(MemberReport report, String eventType) {
        switch (eventType) {
            case "CREATED" -> notifyMemberReportCreated(report);
            case "APPROVED" -> notifyMemberReportApproved(report);
            case "REJECTED" -> notifyMemberReportRejected(report);
        }
    }

    private void notifyRecipeReportCreated(RecipeReport report) {
        Member recipeOwner = report.getRecipeReported().getMember();
        if (recipeOwner != null && recipeOwner.getEmail() != null) {
            emailService.sendEmail(
                    new EmailDetails(
                            recipeOwner.getEmail(),
                            "Your Recipe has been reported!",
                            "Dear " + recipeOwner.getUsername() + ",\nYour recipe has been reported!\nReason: \"" +
                                    report.getReason() + "\",\nPlease login to check."
                    )
            );
        }

        notifyAdmins(report, "A new recipe report has been created.");
    }

    private void notifyRecipeReportApproved(RecipeReport report) {
        Member recipeOwner = report.getRecipeReported().getMember();
        if (recipeOwner != null && recipeOwner.getEmail() != null) {
            emailService.sendEmail(
                    new EmailDetails(
                            recipeOwner.getEmail(),
                            "Your Recipe Report has been Approved!",
                            "Dear " + recipeOwner.getUsername() + ",\nYour recipe report has been approved.\nThank you for your patience!"
                    )
            );
        }
    }

    private void notifyRecipeReportRejected(RecipeReport report) {
        Member recipeOwner = report.getRecipeReported().getMember();
        if (recipeOwner != null && recipeOwner.getEmail() != null) {
            emailService.sendEmail(
                    new EmailDetails(
                            recipeOwner.getEmail(),
                            "Your Recipe Report has been Rejected!",
                            "Dear " + recipeOwner.getUsername() + ",\nYour recipe report has been rejected.\nPlease contact support for more details."
                    )
            );
        }
    }

    private void notifyMemberReportCreated(MemberReport report) {
        Member reportedMember = report.getMemberReported();
        if (reportedMember != null && reportedMember.getEmail() != null) {
            emailService.sendEmail(
                    new EmailDetails(
                            reportedMember.getEmail(),
                            "You have been reported!",
                            "Dear " + reportedMember.getUsername() + ",\nYou have been reported!\nReason: \"" +
                                    report.getReason() + "\",\nPlease login to check."
                    )
            );
        }

        notifyAdmins(report, "A new member report has been created.");
    }

    private void notifyMemberReportApproved(MemberReport report) {
        Member reportedMember = report.getMemberReported();
        if (reportedMember != null && reportedMember.getEmail() != null) {
            emailService.sendEmail(
                    new EmailDetails(
                            reportedMember.getEmail(),
                            "Your Member Report has been Approved!",
                            "Dear " + reportedMember.getUsername() + ",\nYour member report has been approved."
                    )
            );
        }
    }

    private void notifyMemberReportRejected(MemberReport report) {
        Member reportedMember = report.getMemberReported();
        if (reportedMember != null && reportedMember.getEmail() != null) {
            emailService.sendEmail(
                    new EmailDetails(
                            reportedMember.getEmail(),
                            "Your Member Report has been Rejected!",
                            "Dear " + reportedMember.getUsername() + ",\nYour member report has been rejected.\nPlease contact support for more details."
                    )
            );
        }
    }

    private void notifyAdmins(Report report, String message) {
        List<Admin> admins = adminService.getAllAdmin();
        for (Admin admin : admins) {
            emailService.sendEmail(
                    new EmailDetails(
                            admin.getEmail(),
                            "Report Notification",
                            message + "\nDetails:\n" + report.toString()
                    )
            );
        }
    }
}
