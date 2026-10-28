package dev.axiom.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Screen-space boxes/lines for 1.21.11.
 * Avoids BufferRenderer / Tessellator / VertexFormat — those moved or vanished.
 */
public final class RenderUtil {

    public static Vec3d camPos() {
        return MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
    }

    /** Project world point to screen pixels. null if behind camera. */
    public static float[] worldToScreen(Vec3d world, int sw, int sh) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera cam = mc.gameRenderer.getCamera();
        Vec3d c = cam.getCameraPos();

        double dx = world.x - c.x;
        double dy = world.y - c.y;
        double dz = world.z - c.z;

        // yaw/pitch → camera space (no getRotation in 1.21.11)
        float yaw = (float) Math.toRadians(cam.getYaw());
        float pitch = (float) Math.toRadians(cam.getPitch());
        double cosYaw = Math.cos(yaw), sinYaw = Math.sin(yaw);
        double cosPitch = Math.cos(pitch), sinPitch = Math.sin(pitch);

        // Minecraft: yaw 0 looks south (+Z), rotate then pitch
        double x = dx * cosYaw + dz * sinYaw;
        double z = -dx * sinYaw + dz * cosYaw;
        double y = dy;
        double y2 = y * cosPitch - z * sinPitch;
        double z2 = y * sinPitch + z * cosPitch;

        if (z2 < 0.05) return null;

        float fov = (float) Math.toRadians(mc.options.getFov().getValue());
        float tan = (float) Math.tan(fov * 0.5);
        float aspect = (float) sw / (float) sh;

        float nx = (float) (x / (z2 * tan * aspect));
        float ny = (float) (y2 / (z2 * tan));

        float sx = (nx * 0.5f + 0.5f) * sw;
        float sy = (1f - (ny * 0.5f + 0.5f)) * sh;
        return new float[]{sx, sy};
    }

    /** Draw axis-aligned box outline in screen space (8 corners → edges). */
    public static void box2d(DrawContext g, Box box, int argb, int sw, int sh) {
        Vec3d[] corners = {
            new Vec3d(box.minX, box.minY, box.minZ),
            new Vec3d(box.maxX, box.minY, box.minZ),
            new Vec3d(box.maxX, box.minY, box.maxZ),
            new Vec3d(box.minX, box.minY, box.maxZ),
            new Vec3d(box.minX, box.maxY, box.minZ),
            new Vec3d(box.maxX, box.maxY, box.minZ),
            new Vec3d(box.maxX, box.maxY, box.maxZ),
            new Vec3d(box.minX, box.maxY, box.maxZ),
        };
        float[][] s = new float[8][];
        for (int i = 0; i < 8; i++) s[i] = worldToScreen(corners[i], sw, sh);

        int[][] edges = {
            {0,1},{1,2},{2,3},{3,0},
            {4,5},{5,6},{6,7},{7,4},
            {0,4},{1,5},{2,6},{3,7}
        };
        for (int[] e : edges) {
            float[] a = s[e[0]], b = s[e[1]];
            if (a == null || b == null) continue;
            line(g, a[0], a[1], b[0], b[1], argb);
        }
    }

    public static void line(DrawContext g, float x0, float y0, float x1, float y1, int argb) {
        int steps = Math.max(1, (int) (Math.hypot(x1 - x0, y1 - y0) / 2));
        for (int i = 0; i <= steps; i++) {
            float t = i / (float) steps;
            int x = MathHelper.floor(x0 + (x1 - x0) * t);
            int y = MathHelper.floor(y0 + (y1 - y0) * t);
            g.fill(x, y, x + 1, y + 1, argb);
        }
    }

    public static int argb(float a, float r, float g, float b) {
        int ai = Math.min(255, Math.max(0, (int) (a * 255)));
        int ri = Math.min(255, Math.max(0, (int) (r * 255)));
        int gi = Math.min(255, Math.max(0, (int) (g * 255)));
        int bi = Math.min(255, Math.max(0, (int) (b * 255)));
        return (ai << 24) | (ri << 16) | (gi << 8) | bi;
    }

    private RenderUtil() {}
}
