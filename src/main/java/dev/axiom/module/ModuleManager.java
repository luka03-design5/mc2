package dev.axiom.module;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.axiom.modules.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {
    private static final Map<String, Module> MAP = new LinkedHashMap<>();
    private static final List<Module> LIST = new ArrayList<>();
    private static boolean german = true;

    public static void init() {
        reg(new Fullbright());
        reg(new ColorGrade());
        reg(new KnifeCosmetics());
        reg(new Hitboxes());
        reg(new BlockOverlay());
        reg(new StorageEsp());
        reg(new Tracers());
        reg(new LyricsToggle());
        load();
    }

    private static void reg(Module m) {
        MAP.put(m.id().toLowerCase(), m);
        LIST.add(m);
    }

    public static List<Module> all() { return LIST; }
    public static Module get(String id) { return MAP.get(id.toLowerCase()); }
    public static boolean german() { return german; }
    public static void setGerman(boolean g) { german = g; save(); }

    public static void tick() {
        for (Module m : LIST) if (m.enabled()) m.onTick();
    }

    public static void hud(DrawContext g, float td) {
        for (Module m : LIST) if (m.enabled()) m.onHud(g, td);
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("axiom-visuals.json");
    }

    public static void load() {
        try {
            Path f = file();
            if (!Files.exists(f)) return;
            JsonObject o = JsonParser.parseString(Files.readString(f)).getAsJsonObject();
            if (o.has("german")) german = o.get("german").getAsBoolean();
            for (Module m : LIST) {
                if (o.has(m.id()) && o.get(m.id()).getAsBoolean()) m.setEnabled(true);
            }
            if (o.has("knife")) KnifeCosmetics.setIndex(o.get("knife").getAsInt());
        } catch (Exception ignored) {}
    }

    public static void save() {
        try {
            JsonObject o = new JsonObject();
            o.addProperty("german", german);
            o.addProperty("knife", KnifeCosmetics.index());
            for (Module m : LIST) o.addProperty(m.id(), m.enabled());
            Files.writeString(file(), o.toString());
        } catch (Exception ignored) {}
    }

    private ModuleManager() {}
}
