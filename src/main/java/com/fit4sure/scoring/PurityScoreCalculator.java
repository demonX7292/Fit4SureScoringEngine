package com.fit4sure.scoring;

import com.fit4sure.dto.InsDTO;
import com.fit4sure.model.Ingredient;
import com.fit4sure.model.Product;
import com.fit4sure.service.InsRegistry;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PurityScoreCalculator {

    private final InsRegistry registry;

    public PurityScoreCalculator(InsRegistry registry) {
        this.registry = registry;
    }

    public double calculatePurityScore(Product product) {
        List<Ingredient> ingredients = product.getIngredients();
        if (ingredients == null)
            return 100.0;

        double totalPenalties = 0;
        int additiveCount = 0;

        for (Ingredient ing : ingredients) {
            // Check INS Code field
            String ins = ing.getInsCode();
            if (ins != null && !ins.isEmpty()) {
                additiveCount++;
                totalPenalties += getPenalty(ins);
            }
            // Also check Name against Registry (e.g. "Sodium Benzoate" without INS code)
            else {
                InsDTO match = registry.findByCodeOrAlias(ing.getName());
                if (match != null) {
                    additiveCount++;
                    totalPenalties += match.getPenalty();
                    ing.setInsCode(match.getCode()); // Enrich
                }
            }
        }

        // Multiplier Logic for Additive Count
        if (additiveCount >= 5) {
            totalPenalties *= 1.5;
        } else if (additiveCount >= 3) {
            totalPenalties *= 1.3;
        }

        double baseScore = 100.0 - totalPenalties;
        if (baseScore < 0)
            baseScore = 0;

        return baseScore;
    }

    private double getPenalty(String code) {
        InsDTO match = registry.findByCodeOrAlias(code);
        if (match != null) {
            return match.getPenalty();
        }
        // Fallback for unknown INS codes
        return 5.0;
    }
}
