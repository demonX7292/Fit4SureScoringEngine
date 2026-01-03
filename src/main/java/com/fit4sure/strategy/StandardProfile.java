package com.fit4sure.strategy;

import com.fit4sure.model.Ingredient;
import com.fit4sure.model.Product;
import org.springframework.stereotype.Component;

@Component("standardProfile")
public class StandardProfile implements ScoringProfile {

    @Override
    public String getProfileName() {
        return "Standard";
    }

    @Override
    public int calculateProteinPoints(double proteinGrams) {
        if (proteinGrams > 8.0) {
            return 5;
        }
        return (int) (proteinGrams / 1.6);
    }

    @Override
    public int calculateEnergyPoints(double kj) {
        // Standard UK FSA Energy Logic:
        // <= 335 kJ : 0
        // > 3350 kJ : 10
        // Blocks of 335 kJ
        if (kj <= 335)
            return 0;
        if (kj > 3350)
            return 10;
        return (int) Math.ceil((kj - 335) / 335);
    }

    @Override
    public int calculateSugarPoints(double sugar) {
        if (sugar <= 4.5)
            return 0;
        if (sugar > 45)
            return 10; // Capped at 10 for > 45
        return (int) Math.ceil((sugar - 4.5) / 4.5);
    }

    @Override
    public int calculateSatFatPoints(double satFat) {
        if (satFat <= 1)
            return 0;
        if (satFat > 10)
            return 10;
        return (int) satFat;
    }

    @Override
    public int calculateSodiumPoints(double sodium) {
        if (sodium <= 90)
            return 0;
        if (sodium > 900)
            return 10;
        return (int) Math.ceil((sodium - 90) / 90);
    }

    @Override
    public double getNutriScoreMin() {
        return -15.0; // Standard Best Score
    }

    @Override
    public double getNutriScoreMax() {
        return 40.0; // Standard Worst Score
    }

    @Override
    public double getNutriWeight() {
        return 0.3;
    }

    @Override
    public double getSourceWeight() {
        return 0.5;
    }

    @Override
    public double getPurityWeight() {
        return 0.2;
    }

    @Override
    public int getNegativePointThreshold() {
        return 11; // Standard FSA Threshold
    }

    @Override
    public double getIngredientMultiplier(Ingredient ingredient) {
        return 1.0; // Standard has no bias
    }

    @Override
    public double getUnknownIngredientPenalty() {
        return 0.0;
    }

    @Override
    public int getMaxProteinPointsOnThresholdBreach() {
        return 5;
    }

    @Override
    public int calculateFiberPoints(double fiber) {
        if (fiber <= 0.7)
            return 0;
        if (fiber > 3.5)
            return 5;
        // Simplified linear approximation (0.7 to 3.5 -> 0 to 5)
        return (int) (fiber / 0.7);
    }

    @Override
    public int calculateFvnlPoints(double fvnl) {
        if (fvnl > 80)
            return 5;
        if (fvnl > 60)
            return 2;
        if (fvnl > 40)
            return 1;
        return 0;
    }
}
