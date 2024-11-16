package nus.iss.se.team9.report_review_service.reportObserver;

import lombok.Getter;
import nus.iss.se.team9.report_review_service.model.Report;

public record ReportEvent(Report report, String eventType) {

}