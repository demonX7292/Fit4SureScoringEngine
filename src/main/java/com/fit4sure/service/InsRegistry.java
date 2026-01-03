package com.fit4sure.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit4sure.dto.InsDTO;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class InsRegistry {
    private Map<String, InsDTO> codeMap = new HashMap<>(); // Standard Code -> DTO
    private Map<String, String> aliasIndex = new HashMap<>(); // Alias -> Code

    public InsRegistry() {
        loadData();
    }

    private void loadData() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getResourceAsStream("/ins_registry.json")) {
            if (is == null) {
                System.err.println("ins_registry.json not found!");
                return;
            }
            List<InsDTO> list = mapper.readValue(is, new TypeReference<List<InsDTO>>() {
            });

            for (InsDTO dto : list) {
                codeMap.put(dto.getCode(), dto);

                aliasIndex.put(dto.getCode().toLowerCase().replace(" ", ""), dto.getCode());
                aliasIndex.put(dto.getName().toLowerCase(), dto.getCode());

                if (dto.getAliases() != null) {
                    for (String alias : dto.getAliases()) {
                        aliasIndex.put(alias.toLowerCase().trim().replace(" ", ""), dto.getCode());
                    }
                }
            }
            System.out.println("Loaded " + codeMap.size() + " INS codes.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InsDTO findByCodeOrAlias(String input) {
        if (input == null)
            return null;
        String key = input.toLowerCase().trim().replace(" ", ""); // Normalize spaces for INS codes
        String code = aliasIndex.get(key);
        if (code != null) {
            return codeMap.get(code);
        }
        return null;
    }
}
