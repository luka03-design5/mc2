package dev.axiom.modules;

import dev.axiom.module.Module;
import dev.axiom.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public final class KnifeCosmetics extends Module {
    public enum Knife {
        KARAMBIT("Karambit", "karambit"),
        BAYONET("Bayonet", "bayonet"),
        M9("M9 Bayonet", "m9"),
        SKELETON("Skeleton Knife", "skeleton"),
        BUTTERFLY("Butterfly", "butterfly"),
        SWORD_3D("3D Sword", "sword3d");

        public final String label;
        public final String model;
        Knife(String label, String model) { this.label = label; this.model = model; }
    }

    private static int index = 0;
    public static int inspectTicks = 0;

    public KnifeCosmetics() {
        super("Messer", "Knives", "Cosmetics", "Cosmetics",
            "3D CS-Messer + Inspect (Schwert halten)", "3D CS knives + inspect (hold sword)");
    }

    public static int index() { return index; }
    public static void setIndex(int i) {
        Knife[] all = Knife.values();
        index = Math.floorMod(i, all.length);
        ModuleManager.save();
        // hot-swap override files would need resource reload — badge + inspect style change now
    }
    public static void next() { setIndex(index + 1); }
    public static void prev() { setIndex(index - 1); }
    public static Knife current() {
        Knife[] all = Knife.values();
        return all[Math.floorMod(index, all.length)];
    }
    public static Identifier modelId() {
        return Identifier.of("spotifylyrics", "item/" + current().model);
    }
    public static void startInspect() { inspectTicks = 40; }

    @Override public void onTick() { if (inspectTicks > 0) inspectTicks--; }

    @Override
    public void onHud(DrawContext g, float td) {
        if (!enabled()) return;
        var tr = MinecraftClient.getInstance().textRenderer;
        String label = "✦ " + current().label;
        int w = tr.getWidth(label);
        int x = 10, y = g.getScaledWindowHeight() - 22;
        g.fill(x - 5, y - 4, x + w + 8, y + 12, 0xAA0B0D12);
        g.drawText(tr, label, x, y, 0xFFC4B5FD, false);
    }
}
