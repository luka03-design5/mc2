package dev.axiom.client;

import dev.axiom.gui.ClickGui;
import dev.axiom.lyrics.LyricsClient;
import dev.axiom.module.ModuleManager;
import dev.axiom.modules.KnifeCosmetics;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class AxiomClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new LyricsClient().onInitializeClient();
        ModuleManager.init();

        KeyBinding.Category cat = KeyBinding.Category.create(Identifier.of("spotifylyrics", "axiom"));
        KeyBinding guiKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.axiom.gui", GLFW.GLFW_KEY_RIGHT_SHIFT, cat));
        KeyBinding inspectKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.axiom.inspect", GLFW.GLFW_KEY_I, cat));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ModuleManager.tick();
            while (guiKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new ClickGui());
            }
            while (inspectKey.wasPressed()) {
                if (client.currentScreen == null) KnifeCosmetics.startInspect();
            }
        });

        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.of("spotifylyrics", "axiom-hud"),
            (g, tc) -> ModuleManager.hud(g, tick(tc)));
    }

    private static float tick(RenderTickCounter tc) {
        try { return tc.getTickProgress(true); } catch (Throwable t) { return 0f; }
    }
}
