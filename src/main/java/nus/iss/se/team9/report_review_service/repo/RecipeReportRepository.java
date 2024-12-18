package nus.iss.se.team9.report_review_service.repo;

import nus.iss.se.team9.report_review_service.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeReportRepository extends JpaRepository<RecipeReport,Integer>{

	List<RecipeReport> findByStatus(Status pending);

}
