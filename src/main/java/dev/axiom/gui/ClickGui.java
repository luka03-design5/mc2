package dev.axiom.gui;

import dev.axiom.client.Ui;
import dev.axiom.module.Module;
import dev.axiom.module.ModuleManager;
import dev.axiom.modules.KnifeCosmetics;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Scaled ClickGUI — fits 1080p and 2K/1440p/4K.
 * Layout is percentage-based and clamped so nothing clips off-screen.
 */
public final class ClickGui extends Screen {
    private static final int BG = 0xCC07080C;
    private static final int PANEL = 0xF0141822;
    private static final int CARD = 0xFF1C2230;
    private static final int CARD_ON = 0xFF2A3142;
    private static final int TEXT = 0xFFF2F4F8;
    private static final int MUTED = 0xFF8B93A5;
    private static final int ACCENT = 0xFFB794FF;
    private static final int ACCENT2 = 0xFF5EEAD4;

    private final Map<String, List<Module>> cats = new LinkedHashMap<>();
    private float uiScale = 1f;

    public ClickGui() {
        super(Text.literal("Axiom"));
        for (Module m : ModuleManager.all()) {
            cats.computeIfAbsent(m.category(), k -> new ArrayList<>()).add(m);
        }
    }

    private void recomputeScale() {
        // scale with width; keep readable on 1080 and compact enough for 2K
        // target: full panel stack fits in ~70% of screen height
        uiScale = Math.min(1.35f, Math.max(0.85f, width / 1600f));
    }

    private int S(int v) { return Math.round(v * uiScale); }

    @Override
    public void render(DrawContext g, int mouseX, int mouseY, float delta) {
        recomputeScale();
        g.fill(0, 0, width, height, BG);

        int colW = S(180);
        int gap = S(14);
        int cols = Math.max(1, cats.size());
        int totalW = cols * colW + (cols - 1) * gap;
        // clamp so margins remain
        if (totalW > width - S(40)) {
            colW = (width - S(40) - (cols - 1) * gap) / cols;
            totalW = cols * colW + (cols - 1) * gap;
        }
        int startX = (width - totalW) / 2;
        int top = S(56);

        // header
        Ui.textBold(g, textRenderer, "AXIOM", startX, S(14), TEXT);
        String sub = ModuleManager.german()
            ? "Cosmetics + Visuals  ·  Right Shift  ·  Inspect: I  ·  Lyrics: K"
            : "Cosmetics + Visuals  ·  Right Shift  ·  Inspect: I  ·  Lyrics: K";
        Ui.text(g, textRenderer, sub, startX, S(32), MUTED);

        // language chip
        String lang = ModuleManager.german() ? "DE  |  EN" : "EN  |  DE";
        int lw = Ui.width(textRenderer, lang) + S(24);
        int lx = width - lw - S(16);
        Ui.rrect(g, lx, S(12), lw, S(26), S(8), CARD);
        Ui.text(g, textRenderer, lang, lx + S(12), S(19), MUTED);

        int maxPanelH = height - top - S(100);
        int ci = 0;
        for (var e : cats.entrySet()) {
            int x = startX + ci * (colW + gap);
            int y = top;
            int rows = e.getValue().size();
            int rowH = S(40);
            int h = Math.min(maxPanelH, S(40) + rows * rowH + S(16));
            Ui.rrect(g, x, y, colW, h, S(14), PANEL);
            // accent dot
            Ui.rrect(g, x + S(12), y + S(14), S(7), S(7), S(3), ACCENT);
            Ui.textBold(g, textRenderer, e.getKey(), x + S(26), y + S(14), TEXT);

            int my = y + S(36);
            for (Module m : e.getValue()) {
                if (my + rowH > y + h - S(8)) break;
                boolean on = m.enabled();
                boolean hover = mouseX >= x + S(8) && mouseX < x + colW - S(8)
                    && mouseY >= my && mouseY < my + rowH - S(4);
                int bg = on ? CARD_ON : CARD;
                if (hover) bg = on ? 0xFF343C50 : 0xFF252B3A;
                Ui.rrect(g, x + S(8), my, colW - S(16), rowH - S(6), S(10), bg);
                if (on) Ui.rrect(g, x + S(8), my, S(3), rowH - S(6), S(2), ACCENT);

                Ui.text(g, textRenderer, m.name(), x + S(18), my + S(6), on ? TEXT : MUTED);
                Ui.text(g, textRenderer, m.desc(), x + S(18), my + S(18), 0xFF5C6578);

                // pill
                int px = x + colW - S(44);
                int py = my + S(10);
                Ui.rrect(g, px, py, S(28), S(14), S(7), on ? ACCENT : 0xFF3A4150);
                Ui.rrect(g, on ? px + S(15) : px + S(3), py + S(2), S(10), S(10), S(5), 0xFFFFFFFF);
                my += rowH;
            }
            ci++;
        }

        // knife bar — always fully on screen
        int kx = startX;
        int ky = height - S(88);
        int kw = Math.min(totalW, width - S(40));
        Ui.rrect(g, kx, ky, kw, S(64), S(14), PANEL);
        Ui.rrect(g, kx + S(12), ky + S(14), S(6), S(6), S(3), ACCENT2);
        String kTitle = ModuleManager.german() ? "3D Messer" : "3D Knives";
        Ui.textBold(g, textRenderer, kTitle, kx + S(26), ky + S(12), TEXT);
        Ui.text(g, textRenderer, KnifeCosmetics.current().label, kx + S(26), ky + S(32), ACCENT2);
        String hint = ModuleManager.german()
            ? "Schwert halten · I = Inspect · < > wechseln"
            : "Hold a sword · I = Inspect · < > cycle";
        Ui.text(g, textRenderer, hint, kx + S(26), ky + S(46), MUTED);

        Ui.rrect(g, kx + kw - S(108), ky + S(16), S(40), S(32), S(10), CARD);
        Ui.textBold(g, textRenderer, "<", kx + kw - S(94), ky + S(24), TEXT);
        Ui.rrect(g, kx + kw - S(58), ky + S(16), S(40), S(32), S(10), CARD);
        Ui.textBold(g, textRenderer, ">", kx + kw - S(44), ky + S(24), TEXT);

        super.render(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        if (click.button() != 0) return super.mouseClicked(click, doubled);
        recomputeScale();

        String lang = ModuleManager.german() ? "DE  |  EN" : "EN  |  DE";
        int lw = Ui.width(textRenderer, lang) + S(24);
        int lx = width - lw - S(16);
        if (mx >= lx && mx <= lx + lw && my >= S(12) && my <= S(38)) {
            ModuleManager.setGerman(!ModuleManager.german());
            if (client != null) client.setScreen(new ClickGui());
            return true;
        }

        int colW = S(180);
        int gap = S(14);
        int cols = Math.max(1, cats.size());
        int totalW = cols * colW + (cols - 1) * gap;
        if (totalW > width - S(40)) {
            colW = (width - S(40) - (cols - 1) * gap) / cols;
            totalW = cols * colW + (cols - 1) * gap;
        }
        int startX = (width - totalW) / 2;
        int top = S(56);
        int rowH = S(40);
        int maxPanelH = height - top - S(100);

        int ci = 0;
        for (var e : cats.entrySet()) {
            int x = startX + ci * (colW + gap);
            int y = top;
            int h = Math.min(maxPanelH, S(40) + e.getValue().size() * rowH + S(16));
            int row = y + S(36);
            for (Module m : e.getValue()) {
                if (row + rowH > y + h - S(8)) break;
                if (mx >= x + S(8) && mx < x + colW - S(8) && my >= row && my < row + rowH - S(6)) {
                    m.toggle();
                    return true;
                }
                row += rowH;
            }
            ci++;
        }

        int kx = startX;
        int ky = height - S(88);
        int kw = Math.min(totalW, width - S(40));
        if (my >= ky + S(16) && my <= ky + S(48)) {
            if (mx >= kx + kw - S(108) && mx <= kx + kw - S(68)) { KnifeCosmetics.prev(); return true; }
            if (mx >= kx + kw - S(58) && mx <= kx + kw - S(18)) { KnifeCosmetics.next(); return true; }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override public boolean shouldPause() { return false; }
}
