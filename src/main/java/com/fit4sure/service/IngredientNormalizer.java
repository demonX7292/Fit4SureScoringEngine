package com.fit4sure.service;

import com.fit4sure.dto.IngredientDTO;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class IngredientNormalizer {

    private static final double FUZZY_THRESHOLD = 0.85; // 85% similarity
    private final IngredientRepository repository;
    private final LevenshteinDistance levenshtein = new LevenshteinDistance();

    public IngredientNormalizer(IngredientRepository repository) {
        this.repository = repository;
    }

    public IngredientDTO normalize(String rawName) {
        if (rawName == null || rawName.isEmpty())
            return null;

        // 1. Clean String
        String cleaned = cleanString(rawName);

        // 2. Direct Lookup
        IngredientDTO match = repository.findByAlias(cleaned);
        if (match != null)
            return match;

        // 3. Fuzzy Lookup
        return findFuzzyMatch(cleaned);
    }

    private String cleanString(String input) {
        String s = input.toLowerCase().trim();
        // Simple cleaning: remove common noise words
        // In a real app, this list would be in a file
        s = s.replaceAll("\\b(organic|natural|premium|fresh|raw)\\b", "").trim();
        // Remove contents in parenthesis e.g. "Wheat Flour (Refined)" -> "Wheat Flour"
        s = s.replaceAll("\\(.*?\\)", "").trim();
        return s;
    }

    private IngredientDTO findFuzzyMatch(String target) {
        Map<String, String> index = repository.getAliasIndex();
        String bestAlias = null;
        double bestScore = 0.0;

        for (String alias : index.keySet()) {
            double similarity = calculateSimilarity(target, alias);
            if (similarity > bestScore) {
                bestScore = similarity;
                bestAlias = alias;
            }
        }

        if (bestScore >= FUZZY_THRESHOLD && bestAlias != null) {
            System.out.println(
                    "Fuzzy Match: '" + target + "' -> '" + bestAlias + "' (" + String.format("%.2f", bestScore) + ")");
            return repository.findByAlias(bestAlias);
        }

        return null;
    }

    private double calculateSimilarity(String s1, String s2) {
        // Normalized Levenshtein: 1 - (dist / maxLen)
        int dist = levenshtein.apply(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0)
            return 1.0;
        return 1.0 - ((double) dist / maxLen);
    }
}
