package com.fit4sure.strategy;

import com.fit4sure.model.Ingredient;
import org.springframework.stereotype.Component;

@Component("puristProfile")
public class PuristProfile implements ScoringProfile {

    @Override
    public String getProfileName() {
        return "Purist (Clean Label)";
    }

    @Override
    public int calculateProteinPoints(double proteinGrams) {
        // Standard Logic
        if (proteinGrams > 8.0)
            return 5;
        return (int) (proteinGrams / 1.6);
    }

    @Override
    public int calculateEnergyPoints(double kj) {
        // Standard Logic
        if (kj <= 335)
            return 0;
        if (kj > 3350)
            return 10;
        return (int) Math.ceil((kj - 335) / 335);
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
        return -18.0;
    }

    @Override
    public double getNutriScoreMax() {
        return 40.0;
    }

    @Override
    public double getNutriWeight() {
        return 0.1; // Very Low importance on Macros
    }

    @Override
    public double getSourceWeight() {
        return 0.25; // Medium importance on Source
    }

    @Override
    public double getPurityWeight() {
        return 0.65; // Highest importance on Purity (Ingredients)
    }

    @Override
    public int getNegativePointThreshold() {
        return 11; // Standard
    }

    @Override
    public double getIngredientMultiplier(Ingredient ingredient) {
        return 1.0; // Standard
    }

    @Override
    public double getUnknownIngredientPenalty() {
        return -1.0; // SUSPICIOUS!
    }

    @Override
    public int getMaxProteinPointsOnThresholdBreach() {
        return 0; // If it's dirty, protein doesn't count.
    }

    @Override
    public int calculateFiberPoints(double fiber) {
        // Purists value fiber highly, but stick to std calculation for now
        if (fiber <= 0.7)
            return 0;
        if (fiber > 3.5)
            return 5;
        return (int) (fiber / 0.7);
    }

    @Override
    public int calculateFvnlPoints(double fvnl) {
        // fvnl is loved by purists
        if (fvnl > 80)
            return 8;
        if (fvnl > 60)
            return 5;
        if (fvnl > 40)
            return 4;
        if (fvnl > 20)
            return 2;
        return 0;
    }
}
