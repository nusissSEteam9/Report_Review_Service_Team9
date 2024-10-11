package nus.iss.se.team9.report_review_service.request;

public class ReportMemberRequest {
    private Integer memberReportedId;
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getMemberReportedId() {
        return memberReportedId;
    }

    public void setMemberReportedId(Integer memberReportedId) {
        this.memberReportedId = memberReportedId;
    }
}