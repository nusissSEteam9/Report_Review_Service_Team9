package nus.iss.se.team9.report_review_service.controller;

import jakarta.servlet.http.HttpSession;
import nus.iss.se.team9.report_review_service.model.*;
import nus.iss.se.team9.report_review_service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createReview(@RequestBody Review review, HttpSession sessionObj) {
        Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
        review.setMember(member);
        reviewService.createReview(review);
        return ResponseEntity.ok("Review created successfully for recipe ID: " + review.getRecipe().getId());
    }
}
