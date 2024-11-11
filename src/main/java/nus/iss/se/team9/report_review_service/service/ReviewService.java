package nus.iss.se.team9.report_review_service.service;
import nus.iss.se.team9.report_review_service.model.*;
import jakarta.transaction.Transactional;
import nus.iss.se.team9.report_review_service.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
@Transactional
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final RecipeService recipeService;

	@Autowired
	public ReviewService(ReviewRepository reviewRepository, RecipeService recipeService) {
		this.reviewRepository = reviewRepository;
		this.recipeService = recipeService;
	}
	public void createReview(Review review) {
		reviewRepository.save(review);
	}
	
	public void updateRecipeRating(Review review) {
		Recipe recipe = review.getRecipe();
		double meanRating = reviewRepository.getMeanRating(recipe.getId());
		double roundedMeanRating = Math.round(meanRating * 10.0) / 10.0;
		recipeService.updateRecipeRating(recipe.getId(), roundedMeanRating);
	}

}
