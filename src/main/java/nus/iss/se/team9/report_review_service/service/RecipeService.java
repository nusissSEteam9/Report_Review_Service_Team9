package nus.iss.se.team9.report_review_service.service;

import jakarta.transaction.Transactional;
import nus.iss.se.team9.report_review_service.model.Recipe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Transactional
public class RecipeService {
    private final String recipeServiceUrl;
    private final RestTemplate restTemplate;

    public RecipeService(RestTemplate restTemplate, @Value("recipe.service.url") String recipeServiceUrl) {
        this.recipeServiceUrl =recipeServiceUrl;
        this.restTemplate =restTemplate;
    }

    public Recipe getRecipeById(Integer id) {
        String url = recipeServiceUrl + "/" + id;
        try {
            ResponseEntity<Recipe> response = restTemplate.getForEntity(url, Recipe.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Recipe not found or deleted, status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Recipe with ID " + id + " not found.");
            } else {
                throw new RuntimeException("Error while fetching recipe: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }
}
