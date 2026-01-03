package com.fit4sure.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsDTO {
    private String code; // e.g., "INS 211"
    private String name;
    private String riskTier; // RED, ORANGE, YELLOW, GREEN
    private double penalty;
    private List<String> aliases;
}
