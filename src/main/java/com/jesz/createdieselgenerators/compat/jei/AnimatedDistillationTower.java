package com.jesz.createdieselgenerators.compat.jei;

import com.jesz.createdieselgenerators.PartialModels;
import com.jesz.createdieselgenerators.blocks.BlockRegistry;
import com.jesz.createdieselgenerators.blocks.DistillationTankBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

public class AnimatedDistillationTower extends AnimatedKinetics {

    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
        draw(matrixStack, xOffset, yOffset, 3);
    }
    public void draw(PoseStack matrixStack, int xOffset, int yOffset, int height) {
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 201);

        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f));
        int scale = 23;

        blockElement(PartialModels.JEI_DISTILLER_BOTTOM)
                .atLocal(0, 1, 0)
                .rotateBlock(0, 90, 0)
                .scale(scale)
                .render(matrixStack);
        for (int i = 0; i < height-1; i++) {
            blockElement(PartialModels.JEI_DISTILLER_MIDDLE)
                    .atLocal(0, -i, 0)
                    .rotateBlock(0, 90, 0)
                    .scale(scale)
                    .render(matrixStack);
        }
        blockElement(PartialModels.JEI_DISTILLER_TOP)
                .atLocal(0, -height+1, 0)
                .rotateBlock(0, 90, 0)
                .scale(scale)
                .render(matrixStack);
        blockElement(PartialModels.DISTILLATION_GAUGE).atLocal(1, 1, 0.125).rotate(0, -90, 0).scale(scale).render(matrixStack);
        blockElement(PartialModels.DISTILLATION_GAUGE_DIAL).atLocal(0.625, 1 - 0.35, 1.125).scale(scale).rotate(0, -90, getCurrentAngle()/4-90).render(matrixStack);
        blockElement(PartialModels.DISTILLATION_GAUGE).atLocal(1-0.125, 1, 1).rotate(0, 180, 0).scale(scale).render(matrixStack);
        blockElement(PartialModels.DISTILLATION_GAUGE_DIAL).atLocal(-0.125, 1 - 0.35, 0.625).scale(scale).rotate(-getCurrentAngle()/4+90, 180, 0).render(matrixStack);
        matrixStack.popPose();
    }
}