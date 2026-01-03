package com.fit4sure.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IngredientDTO {
    private String canonicalId;
    private String standardName;
    private double fit4sureValue;
    private int novaGroup;
    private Double maxPercentage; // Optional cap for ingredients like Salt/Stabilizers
    private List<String> aliases;
}
