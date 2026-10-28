package dev.axiom.module;

import net.minecraft.client.gui.DrawContext;

public abstract class Module {
    private final String nameDe;
    private final String nameEn;
    private final String catDe;
    private final String catEn;
    private final String descDe;
    private final String descEn;
    private boolean enabled;

    protected Module(String nameDe, String nameEn, String catDe, String catEn, String descDe, String descEn) {
        this.nameDe = nameDe; this.nameEn = nameEn;
        this.catDe = catDe; this.catEn = catEn;
        this.descDe = descDe; this.descEn = descEn;
    }

    public String name() { return ModuleManager.german() ? nameDe : nameEn; }
    public String category() { return ModuleManager.german() ? catDe : catEn; }
    public String desc() { return ModuleManager.german() ? descDe : descEn; }
    public String id() { return nameEn; }
    public boolean enabled() { return enabled; }

    public void setEnabled(boolean on) {
        if (enabled == on) return;
        enabled = on;
        if (on) onEnable(); else onDisable();
        ModuleManager.save();
    }

    public void toggle() { setEnabled(!enabled); }
    protected void onEnable() {}
    protected void onDisable() {}
    public void onHud(DrawContext g, float td) {}
    public void onTick() {}
}
