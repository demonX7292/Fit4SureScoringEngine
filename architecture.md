# Fit4Sure Scoring Engine Architecture

## High-Level Overview

The application follows a **Strategy Pattern** where the core scoring logic is decoupled from specific nutritional goals (Profiles). The `Fit4SureScoringEngine` orchestrates three sub-calculators, which in turn rely on the active `ScoringProfile` to normalize values and apply penalties.

## Component Diagram

```mermaid
classDiagram
    class App {
        +main(args)
        +scoreProduct(engine, product, profile)
    }

    class Fit4SureScoringEngine {
        -NutriScoreCalculator nutriCalc
        -SourceScoreCalculator sourceCalc
        -PurityScoreCalculator purityCalc
        +calculateTotalScore(Product, ScoringProfile)
    }

    class ProfileFactory {
        -Map~String, ScoringProfile~ profiles
        +getProfile(name)
    }

    class ScoringProfile {
        <<interface>>
        +calculateProteinPoints()
        +calculateEnergyPoints()
        +getNegativePointThreshold()
        +calculateFiberPoints()
    }

    class StandardProfile
    class MuscleBuilderProfile
    class DietProfile
    class PuristProfile

    ScoringProfile <|-- StandardProfile
    ScoringProfile <|-- MuscleBuilderProfile
    ScoringProfile <|-- DietProfile
    ScoringProfile <|-- PuristProfile

    class NutriScoreCalculator {
        +calculateFit4SureNutriScore(NutritionalInfo, ScoringProfile)
    }
    class SourceScoreCalculator {
        +calculateSourceScore(Product, ScoringProfile)
    }
    class PurityScoreCalculator {
        +calculatePurityScore(Product)
    }

    App ..> ProfileFactory : uses
    App ..> Fit4SureScoringEngine : uses
    Fit4SureScoringEngine --> NutriScoreCalculator
    Fit4SureScoringEngine --> SourceScoreCalculator
    Fit4SureScoringEngine --> PurityScoreCalculator
    
    NutriScoreCalculator ..> ScoringProfile : uses strategies
    SourceScoreCalculator ..> ScoringProfile : uses strategies
```

## Data Flow

```mermaid
sequenceDiagram
    participant App
    participant Factory as ProfileFactory
    participant Engine as Fit4SureScoringEngine
    participant Nutri as NutriScoreCalculator
    participant Profile as ScoringProfile

    App->>Factory: getProfile("muscle")
    Factory-->>App: MuscleBuilderProfile

    loop For Each Product
        App->>Engine: calculateTotalScore(Product, Profile)
        
        rect rgb(200, 220, 240)
            note right of Engine: Component Scoring
            Engine->>Nutri: calculateFit4SureNutriScore(Info, Profile)
            Nutri->>Profile: calculateProteinPoints(grams)
            Profile-->>Nutri: points (boosted for muscle)
            Nutri-->>Engine: Nutri Score
            
            Engine->>Engine: Source Score (Standard Norm)
            Engine->>Engine: Purity Score
        end
        
        Engine-->>App: Final Weighted Score
    end
    
    App->>App: Sort & Display
```

## Key Design Principles
1.  **Strategy Pattern**: Use of `ScoringProfile` allows swapping logic (e.g., how Protein is valued) without changing the core engine.
2.  **Factory Pattern**: `ProfileFactory` abstracts the creation and retrieval of these strategies.
3.  **Dependency Injection**: The `Fit4SureScoringEngine` receives its sub-calculators via constructor, making unit testing easier.
