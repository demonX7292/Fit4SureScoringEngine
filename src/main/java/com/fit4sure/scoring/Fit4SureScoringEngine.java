package com.fit4sure.scoring;

import com.fit4sure.model.Product;
import com.fit4sure.strategy.ScoringProfile;
import org.springframework.stereotype.Component;

@Component
public class Fit4SureScoringEngine {

    private final NutriScoreCalculator nutriScoreCalc;
    private final SourceScoreCalculator sourceScoreCalc;
    private final PurityScoreCalculator purityScoreCalc;

    public Fit4SureScoringEngine(NutriScoreCalculator nutriScoreCalc, SourceScoreCalculator sourceScoreCalc,
                                 PurityScoreCalculator purityScoreCalc) {
        this.nutriScoreCalc = nutriScoreCalc;
        this.sourceScoreCalc = sourceScoreCalc;
        this.purityScoreCalc = purityScoreCalc;
    }

    public double calculateTotalScore(Product product, ScoringProfile profile) {

        double nutri = nutriScoreCalc.calculatefit4sureNutriScore(product.getNutritionalInfo(), profile);
        double source = sourceScoreCalc.calculateSourceScore(product, profile);
        double purity = purityScoreCalc.calculatePurityScore(product);

        System.out.printf("DEBUG: Raw Scores (0-100) -> Nutri: %.2f, Source: %.2f, Purity: %.2f%n", nutri, source,
                purity);

        return (nutri * profile.getNutriWeight()) + (source * profile.getSourceWeight())
                + (purity * profile.getPurityWeight());
    }
}
