package com.clivet268.sueshi.client.renderer.entity;

import com.clivet268.sueshi.Entity.Shi;
import com.clivet268.sueshi.Entity.Sue;
import com.clivet268.sueshi.client.model.ShiModel;
import com.clivet268.sueshi.client.model.SueModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class ShiRenderer extends GeoEntityRenderer<Shi> {
    public ShiRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShiModel());
    }
}

