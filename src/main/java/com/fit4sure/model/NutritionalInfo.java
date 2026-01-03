package com.fit4sure.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NutritionalInfo {
    private double energyKcal;
    private double totalStarches; // Not used directly in core Algo but good to have
    private double totalSugar; // g
    private double addedSugar; // g
    private double totalFat; // g
    private double saturatedFat; // g
    private double transFat; // g
    private double sodium; // mg
    private double fiber; // g
    private double protein; // g
    private double fvnlPercentage; // Fruits, Vegetables, Nuts, Legumes %
}
