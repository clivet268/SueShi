package com.clivet268.sueshi.client.renderer.entity;

import com.clivet268.sueshi.Entity.Sue;
import com.clivet268.sueshi.client.model.SueModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class SushiRenderer extends GeoEntityRenderer<Sue> {
    public SushiRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SueModel());
    }
}
