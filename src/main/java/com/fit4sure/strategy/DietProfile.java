package com.fit4sure.strategy;

import com.fit4sure.model.Ingredient;
import com.fit4sure.model.Product;
import org.springframework.stereotype.Component;

@Component("dietProfile")
public class DietProfile implements ScoringProfile {

    @Override
    public String getProfileName() {
        return "Diet (Weight Loss)";
    }

    @Override
    public int calculateProteinPoints(double proteinGrams) {
        // Standard Protein Logic (Max 5)
        if (proteinGrams > 8.0) {
            return 5;
        }
        return (int) (proteinGrams / 1.6);
    }

    @Override
    public int calculateEnergyPoints(double kj) {
        // Diet Logic: Calories are bad. 2x Penalty.
        int points;
        if (kj <= 335)
            points = 0;
        else if (kj > 3350)
            points = 10;
        else
            points = (int) Math.ceil((kj - 335) / 335);

        return Math.min(points * 2, 20); // Double penalty, cap at 20 (arbitrary high cap to punish high calorie)
    }

    @Override
    public int calculateSugarPoints(double sugar) {
        // Diet Logic: Sugar is bad. 2x Penalty.
        if (sugar <= 4.5)
            return 0;
        // Standard cap is 10 points for > 45g.
        // We double the points.
        int points;
        if (sugar > 45)
            points = 10;
        else
            points = (int) Math.ceil((sugar - 4.5) / 4.5);

        return Math.min(points * 2, 20);
    }

    @Override
    public int calculateSatFatPoints(double satFat) {
        // Standard Logic
        if (satFat <= 1)
            return 0;
        if (satFat > 10)
            return 10;
        return (int) satFat;
    }

    @Override
    public int calculateSodiumPoints(double sodium) {
        // Standard Logic
        if (sodium <= 90)
            return 0;
        if (sodium > 900)
            return 10;
        return (int) Math.ceil((sodium - 90) / 90);
    }

    @Override
    public double getNutriScoreMin() {
        return -15.0; // Standard Scale
    }

    @Override
    public double getNutriScoreMax() {
        return 60.0; // Increased max because we can have 20 points for SatFat/Sugar now
    }

    @Override
    public double getNutriWeight() {
        return 0.5; // Balanced
    }

    @Override
    public double getSourceWeight() {
        return 0.3; // Clean label matters
    }

    @Override
    public double getPurityWeight() {
        return 0.2;
    }

    @Override
    public int getNegativePointThreshold() {
        return 11;
    }

    @Override
    public double getIngredientMultiplier(Ingredient ingredient) {
        return 1.0;
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
