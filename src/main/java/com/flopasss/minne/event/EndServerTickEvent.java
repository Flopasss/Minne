package com.flopasss.minne.event;

import com.flopasss.minne.Minne;
import com.flopasss.minne.config.Config;
import com.flopasss.minne.data.PartnerData;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class EndServerTickEvent {

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Config config = Minne.CONFIG;
            if (!config.enabled) return;
            if (server.getTickCount() % config.interval != 0) return;

            PartnerData data = PartnerData.get(server);
            Set<UUID> processed = new HashSet<>();

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                if (processed.contains(uuid)) continue;

                UUID partnerUuid = data.getPartner(uuid);
                if (partnerUuid == null) continue;

                ServerPlayer partner = server
                    .getPlayerList()
                    .getPlayer(partnerUuid);
                if (partner == null) continue;

                if (
                    config.requireVisible &&
                    (player.isInvisible() || partner.isInvisible())
                ) continue;

                if (
                    config.requireSneaking &&
                    (!player.isShiftKeyDown() || !partner.isShiftKeyDown())
                ) continue;

                if (player.level() != partner.level()) continue;

                if (player.distanceTo(partner) > config.distance) continue;

                Vec3 mid = player
                    .getEyePosition()
                    .lerp(partner.getEyePosition(), 0.5);

                player
                    .level()
                    .sendParticles(
                        ParticleTypes.HEART.getType(),
                        false,
                        true,
                        mid.x,
                        mid.y,
                        mid.z,
                        config.count,
                        config.spread,
                        config.spread,
                        config.spread,
                        config.speed
                    );

                processed.add(uuid);
                processed.add(partnerUuid);
            }
        });
    }
}
