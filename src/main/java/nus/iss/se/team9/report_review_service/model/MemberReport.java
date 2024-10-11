package nus.iss.se.team9.report_review_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class MemberReport extends Report {
	@ManyToOne
	@JsonBackReference(value = "member-reportsToMember")
	private Member memberReported;

	//getter and setter
	public Member getMemberReported() {
		return memberReported;
	}

	public void setMemberReported(Member memberReported) {
		this.memberReported = memberReported;
	}
	
}
