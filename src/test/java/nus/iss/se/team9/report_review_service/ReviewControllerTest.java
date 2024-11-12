package nus.iss.se.team9.report_review_service;

import nus.iss.se.team9.report_review_service.controller.ReviewController;
import nus.iss.se.team9.report_review_service.model.Member;
import nus.iss.se.team9.report_review_service.model.Review;
import nus.iss.se.team9.report_review_service.model.Recipe;
import nus.iss.se.team9.report_review_service.service.JwtService;
import nus.iss.se.team9.report_review_service.service.ReviewService;
import nus.iss.se.team9.report_review_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ReviewControllerTest {
    @InjectMocks
    private ReviewController reviewController;

    @Mock
    private ReviewService reviewService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    private Review review;
    private Member member;
    private Recipe recipe;

    @BeforeEach
    public void setUp() {
        // Initialize mock objects
        member = new Member();
        member.setId(1);
        member.setUsername("Test Member");

        recipe = new Recipe();
        recipe.setId(1);
        recipe.setName("Test Recipe");

        review = new Review();
        review.setId(1);
        review.setRecipe(recipe);
        review.setComment("Great Recipe!");
    }

    @Test
    public void testCreateReview_Success() {
        String token = "Bearer testToken";
        when(jwtService.extractId(token)).thenReturn(1);
        when(userService.getMemberById(1)).thenReturn(member);
        ResponseEntity<String> response = reviewController.createReview(review, token);
        verify(userService, times(1)).getMemberById(1);
        verify(reviewService, times(1)).createReview(review);
        verify(reviewService, times(1)).updateRecipeRating(review);

        assertEquals("Review created successfully for recipe ID: 1", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
