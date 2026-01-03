package com.fit4sure.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {
    private String name;
    private Double percentage; // Can be null if not listed
    private NovaGroup novaGroup; // Optional: Can be inferred from Repository
    private String insCode; // For Purity Score (e.g., "INS 211")
    private String canonicalId; // Filled by Normalizer

    public Ingredient(String name, Double percentage, NovaGroup novaGroup) {
        this.name = name;
        this.percentage = percentage;
        this.novaGroup = novaGroup;
    }
}
