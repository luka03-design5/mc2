package dev.axiom.modules;

import dev.axiom.module.Module;
import dev.axiom.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;

public final class BlockOverlay extends Module {
    public BlockOverlay() {
        super("Block Overlay", "Block Overlay", "Visuals", "Visuals",
            "Anvisierten Block markieren", "Highlight targeted block");
    }

    @Override
    public void onHud(DrawContext g, float td) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.crosshairTarget == null
            || mc.crosshairTarget.getType() != HitResult.Type.BLOCK) return;
        var hit = (BlockHitResult) mc.crosshairTarget;
        if (mc.world.getBlockState(hit.getBlockPos()).isAir()) return;
        int sw = g.getScaledWindowWidth(), sh = g.getScaledWindowHeight();
        RenderUtil.box2d(g, new Box(hit.getBlockPos()).expand(0.002),
            RenderUtil.argb(1f, 0.7f, 0.5f, 1f), sw, sh);
    }
}
