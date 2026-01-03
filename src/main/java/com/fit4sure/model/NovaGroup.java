package com.fit4sure.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum NovaGroup {
    UNPROCESSED(1, 1.0),
    PROCESSED_CULINARY(2, 0.4), // Average of +0.1 to +0.5
    PROCESSED(3, 0.0), // Average of -0.2 to +0.1
    ULTRA_PROCESSED(4, -0.7); // Average of -1.0 to -0.3

    private final int groupNumber;
    private final double defaultPurityScore;

    NovaGroup(int groupNumber, double defaultPurityScore) {
        this.groupNumber = groupNumber;
        this.defaultPurityScore = defaultPurityScore;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public double getDefaultPurityScore() {
        return defaultPurityScore;
    }

    @JsonCreator
    public static NovaGroup fromValue(Object value) {
        if (value instanceof Integer) {
            int v = (Integer) value;
            for (NovaGroup g : values()) {
                if (g.groupNumber == v)
                    return g;
            }
        } else if (value instanceof String) {
            String s = (String) value;
            try {
                return NovaGroup.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore, try parsing as int string
            }
        }
        return null;
    }
}
