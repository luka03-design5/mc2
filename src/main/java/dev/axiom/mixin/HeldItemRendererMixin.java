package dev.axiom.mixin;

import dev.axiom.module.ModuleManager;
import dev.axiom.modules.KnifeCosmetics;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void axiom$knifePose(
        AbstractClientPlayerEntity player,
        float tickProgress,
        float pitch,
        Hand hand,
        float swingProgress,
        ItemStack item,
        float equipProgress,
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        int light,
        CallbackInfo ci
    ) {
        if (hand != Hand.MAIN_HAND) return;
        if (!isSword(item)) return;

        ModuleManager.get("Knives");
        var knives = ModuleManager.get("Knives");
        if (knives == null || !knives.enabled()) return;

        // idle pose — slightly more knife-like
        matrices.translate(0.02, 0.04, 0.02);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-8f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(4f));
        matrices.scale(1.08f, 1.08f, 1.08f);

        // inspect animation
        if (KnifeCosmetics.inspectTicks > 0) {
            float t = KnifeCosmetics.inspectTicks - tickProgress;
            float p = 1f - Math.max(0f, Math.min(1f, t / 40f));
            float spin = (float) (Math.sin(p * Math.PI) * 125f);
            float lift = (float) (Math.sin(p * Math.PI) * 0.32f);
            float tilt = (float) (Math.sin(p * Math.PI * 2) * 22f);
            matrices.translate(0.0, lift, 0.08);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spin));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(tilt));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(spin * 0.4f));
        }
    }

    private static boolean isSword(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        var i = stack.getItem();
        return i == Items.WOODEN_SWORD || i == Items.STONE_SWORD || i == Items.IRON_SWORD
            || i == Items.GOLDEN_SWORD || i == Items.DIAMOND_SWORD || i == Items.NETHERITE_SWORD;
    }
}
