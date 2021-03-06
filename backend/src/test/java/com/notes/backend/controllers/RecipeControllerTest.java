package com.notes.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notes.backend.entities.Recipe;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecipeControllerTest {

    @Autowired
    private WebApplicationContext context;

    private Recipe someRecipe = new Recipe(0, 1, "Some Meal", "Some description", "meal", 2.2f, List.of(), List.of());

    @Test
    void getAllRecipes() throws Exception {
        MockMvc mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        mvc.perform(get("/api/recipes/"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getRecipeInfo() throws Exception {
        MockMvc mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        mvc.perform(get("/api/recipes/3"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.recipeId").value(3));
    }

    @Test
    void getNonExistingRecipeInfo() throws Exception {
        MockMvc mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        mvc.perform(get("/api/recipes/121"))
                .andExpect(status().is4xxClientError());
    }


	@Test
	void getAllUserRecipesCheckCode() throws Exception {
		MockMvc mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.build();
		mvc.perform(get("/api/recipes/user/1"))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void getAllUserRecipesCheckContent() throws Exception {
		MockMvc mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.build();
		mvc.perform(get("/api/recipes/user/2"))
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	@Test
	void getAllUserRecipesWithNonExistingUser() throws Exception {
		MockMvc mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.build();
		mvc.perform(get("/api/recipes/user/9999"))
				.andExpect(content().json("[]"));
	}

	@Test
	void getAllUserRecipesWithoutUser() throws Exception {
		MockMvc mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.build();
		mvc.perform(get("/api/recipes/user/"))
				.andExpect(status().is4xxClientError());
	}

    @Test
    void saveRecipe() throws Exception {
        MockMvc mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        mvc.perform(post("/api/recipes").content(asJsonString(someRecipe)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.recipeId", not(0)));
    }

    @Test
    void saveRecipeWithID() throws Exception {
        someRecipe.setRecipeId(5);
        MockMvc mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        mvc.perform(post("/api/recipes").content(asJsonString(someRecipe)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateRecipe() throws Exception {
        someRecipe.setRating(5.0f);
        MockMvc mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        mvc.perform(post("/api/recipes").content(asJsonString(someRecipe)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.rating").value(5.0));
    }

    @Test
	@AfterAll
    static void deleteRecipe(@Autowired WebApplicationContext context) throws Exception {
        MockMvc mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        mvc.perform(delete("/api/recipes/1"))
                .andExpect(status().is2xxSuccessful());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}