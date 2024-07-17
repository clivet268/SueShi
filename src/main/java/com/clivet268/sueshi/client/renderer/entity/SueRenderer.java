package com.clivet268.sueshi.client.renderer.entity;

import com.clivet268.sueshi.Entity.Sue;
import com.clivet268.sueshi.client.model.SueModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class SueRenderer extends GeoEntityRenderer<Sue> {
    public SueRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SueModel());
    }
}

