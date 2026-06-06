package com.flopasss.minne.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

public class PartnerData extends SavedData {

    // Bidirectional: both A→B and B→A are stored so every lookup is O(1).
    // On disk we only write one direction per pair and reconstruct both on load.
    private final Map<UUID, UUID> partners = new HashMap<>();

    // Package-private — only SavedDataType and the codec should construct this
    PartnerData() {}

    // -------------------------------------------------------------------------
    // Serialization (Codec-based)
    // -------------------------------------------------------------------------

    private record Partnership(UUID playerA, UUID playerB) {
        static final Codec<Partnership> CODEC = RecordCodecBuilder.create(
            instance ->
                instance
                    .group(
                        UUIDUtil.CODEC.fieldOf("playerA").forGetter(
                            Partnership::playerA
                        ),
                        UUIDUtil.CODEC.fieldOf("playerB").forGetter(
                            Partnership::playerB
                        )
                    )
                    .apply(instance, Partnership::new)
        );
    }

    private static final Codec<PartnerData> CODEC = Partnership.CODEC.listOf()
        .fieldOf("partnerships")
        .codec()
        .xmap(
            // Decode: list of partnerships → PartnerData
            list -> {
                PartnerData data = new PartnerData();
                for (Partnership p : list) {
                    data.partners.put(p.playerA(), p.playerB());
                    data.partners.put(p.playerB(), p.playerA());
                }
                return data;
            },
            // Encode: PartnerData → list of partnerships (deduplicated)
            data -> {
                List<Partnership> list = new ArrayList<>();
                Set<UUID> seen = new HashSet<>();
                for (Map.Entry<UUID, UUID> entry : data.partners.entrySet()) {
                    if (seen.contains(entry.getKey())) continue;
                    seen.add(entry.getKey());
                    seen.add(entry.getValue());
                    list.add(new Partnership(entry.getKey(), entry.getValue()));
                }
                return list;
            }
        );

    public static final SavedDataType<PartnerData> TYPE = new SavedDataType<>(
        "minne_partners",
        PartnerData::new,
        CODEC,
        DataFixTypes.LEVEL
    );

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns the UUID of the given player's partner, or null if they have none.
     */
    public @Nullable UUID getPartner(UUID playerUUID) {
        return partners.get(playerUUID);
    }

    /**
     * Returns true if the given player currently has a partner.
     */
    public boolean hasPartner(UUID playerUUID) {
        return partners.containsKey(playerUUID);
    }

    /**
     * Confirms a partnership between two players.
     * Writes both directions into the map and marks the data dirty so it
     * gets written to disk on the next world save.
     */
    public void setPartner(UUID playerA, UUID playerB) {
        partners.put(playerA, playerB);
        partners.put(playerB, playerA);
        setDirty();
    }

    /**
     * Removes the partnership for the given player.
     * Also removes the reverse entry so their ex-partner is cleaned up too.
     * Safe to call even if the player has no partner (no-op).
     */
    public void removePartner(UUID playerUUID) {
        UUID partner = partners.remove(playerUUID);
        if (partner != null) {
            partners.remove(partner);
        }
        setDirty();
    }

    // -------------------------------------------------------------------------
    // Static accessor
    // -------------------------------------------------------------------------

    /**
     * The single entry point for getting the PartnerData instance.
     *
     * - First call: Minecraft loads from disk (or creates a fresh instance if
     *   the file doesn't exist yet) and caches it on the Overworld.
     * - Subsequent calls: returns the cached instance directly.
     *
     * Always call this with the MinecraftServer reference — never store the
     * result as a static field, since the Overworld is recreated on world reload.
     */
    public static PartnerData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
}
