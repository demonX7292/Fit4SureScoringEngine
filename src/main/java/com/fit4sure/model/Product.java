package com.fit4sure.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private String name;
    private String category; // e.g., "Snack", "Beverage"
    private List<Ingredient> ingredients;
    private NutritionalInfo nutritionalInfo;
}
