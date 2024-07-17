package com.clivet268.sueshi.client.model;

import com.clivet268.sueshi.Entity.Shi;
import com.clivet268.sueshi.Entity.Sue;
import com.clivet268.sueshi.SueShi;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

@OnlyIn(Dist.CLIENT)
public class ShiModel extends DefaultedEntityGeoModel<Shi> {
    private static final ResourceLocation SHI_LOCATION = ResourceLocation.fromNamespaceAndPath(SueShi.MODID, "shi");
    public ShiModel() {
        super(SHI_LOCATION, false);
    }

    @Override
    public void setCustomAnimations(Shi animatable, long instanceId, AnimationState<Shi> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        //GeoBone body = getAnimationProcessor().getBone("body");
        //TODO
    }

}