package dev.axiom.modules;

import dev.axiom.module.Module;
import net.minecraft.client.gui.DrawContext;

/** Leichtes Color-Grading: Vignette + kühler Look über HUD-Overlay. */
public final class ColorGrade extends Module {
    public ColorGrade() {
        super("Color Grading", "Color Grading", "Visuals", "Visuals",
            "Film-Look (Vignette + Ton)", "Film look (vignette + tone)");
    }

    @Override
    public void onHud(DrawContext g, float td) {
        int w = g.getScaledWindowWidth();
        int h = g.getScaledWindowHeight();
        // soft teal/purple grade
        g.fill(0, 0, w, h, 0x18A06CFF);
        // vignette edges
        int v = 0x40000000;
        int band = Math.max(24, h / 12);
        for (int i = 0; i < band; i++) {
            int a = (int) (0x40 * (1f - i / (float) band));
            int c = (a << 24);
            g.fill(0, i, w, i + 1, c);
            g.fill(0, h - 1 - i, w, h - i, c);
            g.fill(i, 0, i + 1, h, c);
            g.fill(w - 1 - i, 0, w - i, h, c);
        }
    }
}
