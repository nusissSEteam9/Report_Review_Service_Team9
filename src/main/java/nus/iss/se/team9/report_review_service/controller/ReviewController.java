package nus.iss.se.team9.report_review_service.controller;

import nus.iss.se.team9.report_review_service.model.*;
import nus.iss.se.team9.report_review_service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService, JwtService jwtService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createReview(@RequestBody Review review,@RequestHeader("Authorization") String token) {
        Member member = userService.getMemberById(jwtService.extractId(token));
        review.setMember(member);
        reviewService.createReview(review);
        return ResponseEntity.ok("Review created successfully for recipe ID: " + review.getRecipe().getId());
    }
}
