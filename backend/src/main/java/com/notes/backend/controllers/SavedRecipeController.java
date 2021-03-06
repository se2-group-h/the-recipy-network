package com.notes.backend.controllers;

import com.notes.backend.entities.RecipeLink;
import com.notes.backend.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Saving Recipes")
@RestController
@AllArgsConstructor
@RequestMapping("/api/saved_recipes")
public class SavedRecipeController {

    private UserService userService;

    @PostMapping
	@ApiImplicitParam(name = "Authorization", paramType = "header", example = "Bearer $JWT", dataTypeClass = String.class)
    public ResponseEntity<?> saveRecipeLink(@RequestBody RecipeLink link) {
        try {
            userService.saveUserRecipe(link);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }

    @DeleteMapping
	@ApiImplicitParam(name = "Authorization", paramType = "header", example = "Bearer $JWT", dataTypeClass = String.class)
    public ResponseEntity<?> deleteLink(@RequestParam Integer userId, @RequestParam Integer recipeId) {
        try {
            userService.deleteUserRecipe(userId, recipeId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }

}
