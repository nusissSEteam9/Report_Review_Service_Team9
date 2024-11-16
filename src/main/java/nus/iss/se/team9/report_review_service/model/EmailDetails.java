package nus.iss.se.team9.report_review_service.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailDetails {
    // Getters and Setters
    private String to;
    private String subject;
    private String body;

    public EmailDetails(){
    }
    public EmailDetails(String to,String subject,String body){
        this.body=body;
        this.to=to;
        this.subject=subject;
    }

}
