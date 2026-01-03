package com.fit4sure.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit4sure.dto.IngredientDTO;
import lombok.Getter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class IngredientRepository {
    private Map<String, IngredientDTO> canonicalMap = new HashMap<>();
    @Getter
    private Map<String, String> aliasIndex = new HashMap<>(); // Alias -> CanonicalID

    public IngredientRepository() {
        loadData();
    }

    private void loadData() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getResourceAsStream("/master_ingredients.json")) {
            if (is == null) {
                System.err.println("master_ingredients.json not found!");
                return;
            }
            List<IngredientDTO> list = mapper.readValue(is, new TypeReference<List<IngredientDTO>>() {
            });

            for (IngredientDTO dto : list) {
                if (dto.getCanonicalId() == null) {
                    System.err.println("Warning: IngredientDTO has null canonicalId: " + dto);
                    continue;
                }
                canonicalMap.put(dto.getCanonicalId(), dto);

                // Index Canonical ID as Alias too
                aliasIndex.put(dto.getCanonicalId().toLowerCase(), dto.getCanonicalId());
                if (dto.getStandardName() != null) {
                    aliasIndex.put(dto.getStandardName().toLowerCase(), dto.getCanonicalId());
                }

                if (dto.getAliases() != null) {
                    for (String alias : dto.getAliases()) {
                        aliasIndex.put(alias.toLowerCase().trim(), dto.getCanonicalId());
                    }
                }
            }
            System.out.println("Loaded " + canonicalMap.size() + " master ingredients.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IngredientDTO findByAlias(String alias) {
        if (alias == null)
            return null;
        String key = alias.toLowerCase().trim();
        String canonicalId = aliasIndex.get(key);
        if (canonicalId != null) {
            return canonicalMap.get(canonicalId);
        }
        return null;
    }

    public IngredientDTO getByCanonicalId(String id) {
        return canonicalMap.get(id);
    }
}
