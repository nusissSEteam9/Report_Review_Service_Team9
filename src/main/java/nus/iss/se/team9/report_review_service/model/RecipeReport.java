package nus.iss.se.team9.report_review_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
@Entity
public class RecipeReport extends Report{
	@ManyToOne
	@JsonBackReference(value = "recipe-reportsToRecipe")
	private Recipe recipeReported;

	// getter and setter
	public Recipe getRecipeReported() {
		return recipeReported;
	}

	public void setRecipeReported(Recipe recipeReported) {
		this.recipeReported = recipeReported;
	}
	
}
