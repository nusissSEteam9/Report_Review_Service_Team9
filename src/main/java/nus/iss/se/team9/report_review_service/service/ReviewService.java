package nus.iss.se.team9.report_review_service.service;

import nus.iss.se.team9.report_review_service.model.*;
import jakarta.transaction.Transactional;
import nus.iss.se.team9.report_review_service.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ReviewService {
	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	RecipeRepository recipeRepository;
    @Autowired
    private RecipeReportRepository recipeReportRepository;

	public void createReview(Review review) {
		reviewRepository.save(review);
		Recipe recipe = review.getRecipe();
		recipe.setNumberOfRating(recipe.getNumberOfRating() + 1);
		// Update mean rating of recipe
		double meanRating = reviewRepository.getMeanRating(recipe.getId());
		double roundedMeanRating = Math.round(meanRating * 10.0) / 10.0;
		recipe.setRating(roundedMeanRating);
		recipeRepository.save(recipe);
	}
}
