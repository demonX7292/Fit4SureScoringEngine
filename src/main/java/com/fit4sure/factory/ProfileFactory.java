package com.fit4sure.factory;

import com.fit4sure.strategy.ScoringProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProfileFactory {

    private final Map<String, ScoringProfile> profiles = new HashMap<>();
    private ScoringProfile defaultProfile;

    @Autowired
    public ProfileFactory(List<ScoringProfile> profileList) {
        for (ScoringProfile profile : profileList) {
            // Register by bean name (e.g., "standardProfile")
            profiles.put(profile.getClass().getSimpleName().toLowerCase(), profile);
            // Also register by friendly name if possible, or just rely on simple mapping

            // Heuristic aliases
            if (profile.getClass().getSimpleName().equalsIgnoreCase("StandardProfile")) {
                profiles.put("standard", profile);
                this.defaultProfile = profile;
            } else if (profile.getClass().getSimpleName().equalsIgnoreCase("MuscleBuilderProfile")) {
                profiles.put("muscle", profile);
            } else if (profile.getClass().getSimpleName().equalsIgnoreCase("DietProfile")) {
                profiles.put("diet", profile);
            } else if (profile.getClass().getSimpleName().equalsIgnoreCase("PuristProfile")) {
                profiles.put("purist", profile);
            }
        }
    }

    public ScoringProfile getProfile(String name) {
        if (name == null || name.isEmpty()) {
            return defaultProfile;
        }
        return profiles.getOrDefault(name.toLowerCase(), defaultProfile);
    }
}
