package com.fit4sure.strategy;

import com.fit4sure.model.Ingredient;

public interface ScoringProfile {
    String getProfileName(); // e.g., "Muscle Builder"

    // 1. Controls Nutri-Score Logic
    int calculateEnergyPoints(double kj); // To allow "discounting" calories

    int calculateSugarPoints(double sugarGrams);

    int calculateSatFatPoints(double satFatGrams);

    int calculateSodiumPoints(double sodiumMg);

    int calculateProteinPoints(double proteinGrams);

    double getNutriScoreMin(); // Returns -15 or -30 (for scaling)

    double getNutriScoreMax(); // Returns 40

    // 2. Controls Aggregation Weights
    double getNutriWeight(); // e.g., 0.70

    double getSourceWeight(); // e.g., 0.20

    double getPurityWeight(); // e.g., 0.10

    int getNegativePointThreshold(); // Default 11, Muscle 18

    // 4. Ingredient Logic
    double getIngredientMultiplier(Ingredient ingredient); // Default 1.0

    double getUnknownIngredientPenalty(); // Default 0.0, Purist -2.0

    // 5. Advanced Thresholds
    int getMaxProteinPointsOnThresholdBreach(); // Default 5, Muscle 20, Purist 0

    // 6. Normalization Bounds

    // 7. Extended Nutri Logic (Previously Hardcoded)
    int calculateFiberPoints(double fiberGrams);

    int calculateFvnlPoints(double fvnlPercentage);
}
