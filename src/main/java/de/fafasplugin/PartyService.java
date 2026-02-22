package de.fafasplugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PartyService {
    private final Map<UUID, Set<UUID>> parties = new HashMap<>();
    private final Map<UUID, UUID> invites = new HashMap<>();

    public void invite(UUID leader, UUID invited) {
        invites.put(invited, leader);
        parties.computeIfAbsent(leader, ignored -> new HashSet<>()).add(leader);
    }

    public UUID accept(UUID invited) {
        UUID leader = invites.remove(invited);
        if (leader != null) {
            parties.computeIfAbsent(leader, ignored -> new HashSet<>()).add(invited);
        }
        return leader;
    }

    public int size(UUID leader) {
        return parties.getOrDefault(leader, Set.of()).size();
    }
}
