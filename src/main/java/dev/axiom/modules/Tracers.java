package dev.axiom.modules;

import dev.axiom.module.Module;
import dev.axiom.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

public final class Tracers extends Module {
    public Tracers() {
        super("Tracers", "Tracers", "Visuals", "Visuals",
            "Linien zu Spielern", "Lines to players");
    }

    @Override
    public void onHud(DrawContext g, float td) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        int sw = g.getScaledWindowWidth(), sh = g.getScaledWindowHeight();
        float cx = sw / 2f, cy = sh / 2f;
        int col = RenderUtil.argb(0.85f, 1f, 0.3f, 0.4f);
        for (PlayerEntity e : mc.world.getPlayers()) {
            if (e == mc.player || e.squaredDistanceTo(mc.player) > 96 * 96) continue;
            float[] s = RenderUtil.worldToScreen(e.getBoundingBox().getCenter(), sw, sh);
            if (s != null) RenderUtil.line(g, cx, cy, s[0], s[1], col);
        }
    }
}
