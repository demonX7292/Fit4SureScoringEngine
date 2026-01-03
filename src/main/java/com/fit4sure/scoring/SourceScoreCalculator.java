package com.fit4sure.scoring;

import com.fit4sure.dto.IngredientDTO;
import com.fit4sure.model.Ingredient;
import com.fit4sure.model.Product;
import com.fit4sure.service.IngredientNormalizer;
import org.springframework.stereotype.Component;
import java.util.List;

import static com.fit4sure.config.Constants.INGREDIENT_WEIGHT_DECAY_FACTOR;

@Component
public class SourceScoreCalculator {

    private static final double DECAY_FACTOR = INGREDIENT_WEIGHT_DECAY_FACTOR;
    private final IngredientNormalizer normalizer;

    public SourceScoreCalculator(IngredientNormalizer normalizer) {
        this.normalizer = normalizer;
    }

    public double calculateSourceScore(Product product, com.fit4sure.strategy.ScoringProfile profile) {
        List<Ingredient> ingredients = product.getIngredients();
        if (ingredients == null || ingredients.isEmpty())
            return 0;

        int n = ingredients.size();
        IngredientDTO[] dtos = new IngredientDTO[n];
        double[] weights = new double[n];
        boolean[] isFixedPercentage = new boolean[n];

        double totalAssignedWeight = 0;
        int unknownCount = 0;

        // 1. Resolve DTOs and Identifying Weights
        for (int i = 0; i < n; i++) {
            Ingredient ing = ingredients.get(i);
            // Resolve DTO to check for Caps
            dtos[i] = normalizer.normalize(ing.getName());
            if (dtos[i] != null) {
                ing.setCanonicalId(dtos[i].getCanonicalId());
            }

            if (ing.getPercentage() != null) {
                weights[i] = ing.getPercentage() / 100.0;
                totalAssignedWeight += weights[i];
                isFixedPercentage[i] = true;
            } else {
                unknownCount++;
            }
        }

        // 2. Distribute remaining weight using Decay
        double remainingWeight = 1.0 - totalAssignedWeight;
        if (remainingWeight < 0)
            remainingWeight = 0;

        if (unknownCount > 0) {
            double totalDecaySum = 0;
            // Calculate decay sum based on position of unknowns
            for (int i = 0; i < n; i++) {
                if (!isFixedPercentage[i]) {
                    totalDecaySum += Math.pow(DECAY_FACTOR, i);
                }
            }

            for (int i = 0; i < n; i++) {
                if (!isFixedPercentage[i]) {
                    double rawWeight = Math.pow(DECAY_FACTOR, i);
                    weights[i] = (rawWeight / totalDecaySum) * remainingWeight;
                }
            }
        } else if (totalAssignedWeight > 0 && Math.abs(totalAssignedWeight - 1.0) > 0.01) {
            // Normalize if needed, though usually fixed weights are respected
            for (int i = 0; i < n; i++) {
                weights[i] = weights[i] / totalAssignedWeight;
            }
        }

        // 3. Apply Cap Logic (Redistribution)
        // We cap calculated weights if they exceed the maxPercentage defined in DTO
        double surplus = 0;
        boolean[] isCapped = new boolean[n];

        for (int i = 0; i < n; i++) {
            // Only cap calculated weights (or all? assuming calculated for now to fix
            // 'bias')
            if (!isFixedPercentage[i] && dtos[i] != null && dtos[i].getMaxPercentage() != null) {
                double cap = dtos[i].getMaxPercentage() / 100.0;
                if (weights[i] > cap) {
                    surplus += (weights[i] - cap);
                    weights[i] = cap;
                    isCapped[i] = true;
                    // System.out.println("Capped " + dtos[i].getStandardName() + " at " + (cap*100)
                    // + "%");
                }
            }
        }

        // 4. Redistribute surplus to eligible ingredients (Not capped, not fixed?)
        // Requirement: "Redistributed back to the primary 'Raw' ingredients"
        // We redistribute to anyone who isn't capped.
        if (surplus > 0) {
            double eligibleWeightSum = 0;
            for (int i = 0; i < n; i++) {
                if (!isCapped[i]) { // Redistribution target
                    eligibleWeightSum += weights[i];
                }
            }

            if (eligibleWeightSum > 0) {
                for (int i = 0; i < n; i++) {
                    if (!isCapped[i]) {
                        double share = weights[i] / eligibleWeightSum;
                        weights[i] += surplus * share;
                    }
                }
            }
        }

        // 5. Calculate Final Score
        double totalWeightedPurity = 0;
        double totalWeightCheck = 0;

        for (int i = 0; i < n; i++) {
            double fit4sureVal = 0;
            if (dtos[i] != null) {
                fit4sureVal = dtos[i].getFit4sureValue();
            } else {
                // Fallback / Unknown
                if (ingredients.get(i).getNovaGroup() != null) {
                    fit4sureVal = ingredients.get(i).getNovaGroup().getDefaultPurityScore();
                } else {
                    // Truly unknown.
                    fit4sureVal = profile.getUnknownIngredientPenalty();
                }
            }
            double multiplier = profile.getIngredientMultiplier(ingredients.get(i));
            totalWeightedPurity += (weights[i] * fit4sureVal * multiplier);
            totalWeightCheck += weights[i];
        }

        // Normalize sum to 1.0 if minor float errors
        if (totalWeightCheck > 0) {
            totalWeightedPurity /= totalWeightCheck;
        }

        // Normalize based on profile bounds
        // 4. Normalization (Maps -1.0 to 1.0 -> 0 to 100)
        double normalized = ((totalWeightedPurity + 1) / 2) * 100.0;
        return Math.max(0, Math.min(100, normalized)); // Clamp to be safe
    }
}
