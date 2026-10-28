package dev.axiom.modules;

import dev.axiom.module.Module;
import dev.axiom.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashSet;
import java.util.Set;

/** Range 24, scan every 40 ticks — FPS-freundlich. */
public final class StorageEsp extends Module {
    private static final int RANGE = 24;
    private final Set<BlockPos> cache = new HashSet<>();
    private int tick;

    public StorageEsp() {
        super("Storage ESP", "Storage ESP", "Visuals", "Visuals",
            "Truhen / Shulker / Enderchest", "Chests / shulkers / ender");
    }

    private static boolean isStorage(Block b) {
        if (b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST
            || b == Blocks.ENDER_CHEST || b == Blocks.BARREL
            || b == Blocks.SHULKER_BOX) return true;
        return Registries.BLOCK.getId(b).getPath().contains("shulker_box");
    }

    @Override
    public void onTick() {
        if (++tick % 40 != 0) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        cache.clear();
        BlockPos o = mc.player.getBlockPos();
        for (int x = -RANGE; x <= RANGE; x++)
            for (int y = -RANGE; y <= RANGE; y++)
                for (int z = -RANGE; z <= RANGE; z++) {
                    BlockPos p = o.add(x, y, z);
                    if (isStorage(mc.world.getBlockState(p).getBlock()))
                        cache.add(p.toImmutable());
                }
    }

    @Override
    public void onHud(DrawContext g, float td) {
        if (cache.isEmpty()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        int sw = g.getScaledWindowWidth(), sh = g.getScaledWindowHeight();
        for (BlockPos p : cache) {
            if (p.getSquaredDistance(mc.player.getBlockPos()) > RANGE * RANGE) continue;
            Block b = mc.world.getBlockState(p).getBlock();
            int c;
            if (b == Blocks.ENDER_CHEST) c = RenderUtil.argb(0.9f, 0.55f, 0.25f, 1f);
            else if (Registries.BLOCK.getId(b).getPath().contains("shulker"))
                c = RenderUtil.argb(0.9f, 0.9f, 0.3f, 0.9f);
            else c = RenderUtil.argb(0.9f, 1f, 0.75f, 0.2f);
            RenderUtil.box2d(g, new Box(p).expand(0.01), c, sw, sh);
        }
    }
}
