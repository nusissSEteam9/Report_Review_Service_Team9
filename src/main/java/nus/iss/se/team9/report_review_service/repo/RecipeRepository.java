package nus.iss.se.team9.report_review_service.repo;

import nus.iss.se.team9.report_review_service.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

}
