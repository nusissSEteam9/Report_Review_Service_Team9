package nus.iss.se.team9.report_review_service.request;
public class ReportRecipeRequest {
    private String reason;
    private Integer recipeReportedId;

    // Getters and Setters
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getRecipeReportedId() {
        return recipeReportedId;
    }

    public void setRecipeReportedId(Integer recipeReportedId) {
        this.recipeReportedId = recipeReportedId;
    }
}