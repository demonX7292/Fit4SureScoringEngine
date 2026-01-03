package com.fit4sure.strategy;

import com.fit4sure.model.Ingredient;
import com.fit4sure.model.Product;
import org.springframework.stereotype.Component;

@Component("muscleProfile")
public class MuscleBuilderProfile implements ScoringProfile {

    @Override
    public String getProfileName() {
        return "Muscle Builder";
    }

    @Override
    public int calculateProteinPoints(double proteinGrams) {
        // High Protein Logic: Uncapped up to 20 points
        // Linear scale: protein / 1.5 -> Max 20
        return (int) Math.min((proteinGrams / 1.5), 20);
    }

    @Override
    public int calculateEnergyPoints(double kj) {
        // Muscle Builder Logic: Calories are fuel. 50% discount on penalty.
        // Standard Energy Points are calculated then halved.
        // Standard:
        int points;
        if (kj <= 335)
            points = 0;
        else if (kj > 3350)
            points = 10;
        else
            points = (int) Math.ceil((kj - 335) / 335);

        return (int) (points * 0.7);
    }

    @Override
    public int calculateSugarPoints(double sugar) {
        // Standard Logic
        if (sugar <= 4.5)
            return 0;
        if (sugar > 45)
            return 10;
        return (int) Math.ceil((sugar - 4.5) / 4.5);
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
        return -30.0; // Extended scale for high protein
    }

    @Override
    public double getNutriScoreMax() {
        return 40.0;
    }

    @Override
    public double getNutriWeight() {
        return 0.7; // 70% Nutri
    }

    @Override
    public double getSourceWeight() {
        return 0.2; // 20% Source
    }

    @Override
    public double getPurityWeight() {
        return 0.1; // 10% Purity
    }

    @Override
    public int getNegativePointThreshold() {
        return 18; // Higher threshold allows protein points to count despite high calories
    }

    @Override
    public double getIngredientMultiplier(Ingredient ingredient) {
        if (ingredient.getName() != null) {
            String nameLower = ingredient.getName().toLowerCase();
            if (nameLower.contains("whey") || nameLower.contains("protein")) {
                return 1.2; // 20% Bonus for protein sources
            }
        }
        return 1.0;
    }

    @Override
    public double getUnknownIngredientPenalty() {
        return 0.0;
    }

    @Override
    public int getMaxProteinPointsOnThresholdBreach() {
        return 20; // PROTEIN IS KING. Ignore the wash.
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
