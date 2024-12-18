package nus.iss.se.team9.report_review_service.repo;

import nus.iss.se.team9.report_review_service.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Integer>{
	long countByStatus(Status status);
}
