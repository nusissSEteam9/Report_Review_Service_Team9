package nus.iss.se.team9.report_review_service.repo;

import nus.iss.se.team9.report_review_service.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberReportRepository extends JpaRepository<MemberReport,Integer>{

	List<MemberReport> findByMemberReportedAndStatus(Member member, Status approved);

	List<MemberReport> findByStatus(Status pending);

}
