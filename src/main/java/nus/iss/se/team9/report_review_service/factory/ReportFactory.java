package nus.iss.se.team9.report_review_service.factory;

import nus.iss.se.team9.report_review_service.model.*;

public class ReportFactory {
    public static Report createReport(ReportType reportType, Object... args) {
        return switch (reportType) {
            case RECIPE -> createRecipeReport(args);
            case MEMBER -> createMemberReport(args);
            default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
        };
    }

    private static RecipeReport createRecipeReport(Object... args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Invalid arguments for RecipeReport");
        }
        Recipe recipeReported = (Recipe) args[0];
        Member member = (Member) args[1];
        String reason = (String) args[2];

        RecipeReport recipeReport = new RecipeReport();
        recipeReport.setRecipeReported(recipeReported);
        recipeReport.setMember(member);
        recipeReport.setStatus(Status.PENDING);
        recipeReport.setReason(reason.trim());
        return recipeReport;
    }

    private static MemberReport createMemberReport(Object... args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Invalid arguments for MemberReport");
        }
        Member memberReported = (Member) args[0];
        Member member = (Member) args[1];
        String reason = (String) args[2];

        MemberReport memberReport = new MemberReport();
        memberReport.setMemberReported(memberReported);
        memberReport.setMember(member);
        memberReport.setStatus(Status.PENDING);
        memberReport.setReason(reason.trim());
        return memberReport;
    }
}

