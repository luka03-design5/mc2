package dev.axiom.modules;

import dev.axiom.lyrics.LyricsState;
import dev.axiom.module.Module;

public final class LyricsToggle extends Module {
    public LyricsToggle() {
        super("Lyrics", "Lyrics", "HUD", "HUD",
            "Spotify-Lyrics anzeigen", "Show Spotify lyrics");
    }

    @Override protected void onEnable() { LyricsState.enabled = true; }
    @Override protected void onDisable() { LyricsState.enabled = false; }
}
