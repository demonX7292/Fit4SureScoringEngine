package com.fit4sure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit4sure.model.Product;
import com.fit4sure.scoring.Fit4SureScoringEngine;
import com.fit4sure.strategy.ScoringProfile;
import com.fit4sure.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.io.File;
import java.io.IOException;

public class App {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar app.jar <products.json> [profile]");
            return;
        }

        String jsonFilePath = args[0];
        String profileName = "standard"; // Default to standard

        // Check for Profile
        if (args.length >= 2) {
            profileName = args[1];
        }

        System.out.println("Using Scoring Profile: " + profileName);

        ObjectMapper mapper = new ObjectMapper();

        // Initialize Spring Context
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            Fit4SureScoringEngine engine = context.getBean(Fit4SureScoringEngine.class);

            com.fit4sure.factory.ProfileFactory profileFactory = context.getBean(com.fit4sure.factory.ProfileFactory.class);
            ScoringProfile profile = profileFactory.getProfile(profileName);

            System.out.println("Active Strategy: " + profile.getProfileName());

            if (profile.getClass().getSimpleName().equalsIgnoreCase("StandardProfile")
                    && !profileName.equalsIgnoreCase("standard")) {
                System.out.println("Requested profile '" + profileName + "' not found. Defaulting to Standard.");
            }

            try {
                File inputFile = new File(jsonFilePath);
                com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(inputFile);

                if (rootNode.isArray()) {
                    System.out.println("Processing batch of " + rootNode.size() + " products...");
                    System.out.println("--------------------------------------------------");
                    Product[] products = mapper.treeToValue(rootNode, Product[].class);
                    java.util.List<ProductScore> results = new java.util.ArrayList<>();

                    for (Product product : products) {
                        System.out.println("Scoring: " + product.getName());
                        double score = engine.calculateTotalScore(product, profile);
                        results.add(new ProductScore(product, score));
                    }

                    // Sort Descending
                    results.sort((a, b) -> Double.compare(b.score, a.score));

                    // Print Table
                    System.out.println("Batch Scoring Results (Sorted by Score):");
                    System.out.printf("%-40s | %-10s%n", "Product Name", "Score");
                    System.out.println("-------------------------------------------------------");
                    for (ProductScore res : results) {
                        System.out.printf("%-40s | %6.2f%n",
                                res.product.getName(),
                                res.score);
                    }
                    System.out.println("-------------------------------------------------------");

                } else {
                    Product product = mapper.treeToValue(rootNode, Product.class);
                    scoreProduct(engine, product, profile);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ProductScore {
        Product product;
        double score;

        ProductScore(Product product, double score) {
            this.product = product;
            this.score = score;
        }
    }

    private static void scoreProduct(Fit4SureScoringEngine engine, Product product, ScoringProfile profile) {
        // Single product mode, just print
        System.out.println("Product: " + product.getName());
        double score = engine.calculateTotalScore(product, profile);
        System.out.printf("Final fit4sure Score: %.2f / 100%n", score);
    }
}
