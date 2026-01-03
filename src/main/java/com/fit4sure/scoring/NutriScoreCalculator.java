package com.fit4sure.scoring;

import com.fit4sure.model.NutritionalInfo;
import com.fit4sure.strategy.ScoringProfile;
import org.springframework.stereotype.Component;

@Component
public class NutriScoreCalculator {

    private final ScoringProfile defaultProfile;

    public NutriScoreCalculator(
            @org.springframework.beans.factory.annotation.Qualifier("standardProfile") ScoringProfile defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    public double calculatefit4sureNutriScore(NutritionalInfo info) {
        return calculatefit4sureNutriScore(info, defaultProfile);
    }

    public double calculatefit4sureNutriScore(NutritionalInfo info, ScoringProfile profile) {
        // N Points
        int energyPoints = profile.calculateEnergyPoints(info.getEnergyKcal() * 4.184); // Convert kcal to kJ
        int sugarPoints = profile.calculateSugarPoints(info.getTotalSugar());
        int satFatPoints = profile.calculateSatFatPoints(info.getSaturatedFat());
        int sodiumPoints = profile.calculateSodiumPoints(info.getSodium());
        int nPoints = energyPoints + sugarPoints + satFatPoints + sodiumPoints;

        // P Points Components
        int fvnlPoints = profile.calculateFvnlPoints(info.getFvnlPercentage());
        int fiberPoints = profile.calculateFiberPoints(info.getFiber());

        // Calculate Protein Points using the profile
        int proteinPoints = profile.calculateProteinPoints(info.getProtein());

        // PROTEIN WASH GUARDRAIL:
        if (nPoints >= profile.getNegativePointThreshold() && fvnlPoints < 5) {
            proteinPoints = Math.min(proteinPoints, profile.getMaxProteinPointsOnThresholdBreach());
        }

        int rawScore = calculateRawScore(nPoints, fvnlPoints, fiberPoints, proteinPoints,
                profile.getNegativePointThreshold());

        return mapTofit4sureScale(rawScore, profile);
    }

    private int calculateRawScore(int nPoints, int fvnlPoints, int fiberPoints, int proteinPoints, int threshold) {
        // If N-points are less than threshold, we subtract all P-points
        if (nPoints < threshold) {
            return nPoints - (fvnlPoints + fiberPoints + proteinPoints);
        }

        // If N-points >= threshold, we check FVNL
        if (fvnlPoints == 5) {
            // High FVNL allows protein retention (Standard rule)
            return nPoints - (fvnlPoints + fiberPoints + proteinPoints);
        }

        // Otherwise (N >= threshold and FVNL < 5):
        // The 'proteinPoints' variable passed here has ALREADY been clamped by the
        // Guardrail upstream.
        // So we can safely subtract it. This fixes the confusion where logic was
        // duplicated or ignored.
        return nPoints - (fvnlPoints + fiberPoints + proteinPoints);
    }

    private double mapTofit4sureScale(int rawScore, ScoringProfile profile) {
        // Use scaling from profile
        double best = profile.getNutriScoreMin(); // e.g., -15
        double worst = profile.getNutriScoreMax(); // e.g., 40
        double range = worst - best;

        double normalized = 100.0 * (1.0 - ((rawScore - best) / range));

        // Clamp to 0-100
        if (normalized < 0)
            return 0.0;
        if (normalized > 100)
            return 100.0;

        return normalized;
    }
}
