package com.clivet268.sueshi.client.model;

import com.clivet268.sueshi.Entity.Sue;
import com.clivet268.sueshi.SueShi;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

@OnlyIn(Dist.CLIENT)
public class SueModel extends DefaultedEntityGeoModel<Sue> {
    private static final ResourceLocation SUE_LOCATION = ResourceLocation.fromNamespaceAndPath(SueShi.MODID, "sue");
    public SueModel() {
        super(SUE_LOCATION, false);
    }

    @Override
    public void setCustomAnimations(Sue animatable, long instanceId, AnimationState<Sue> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        GeoBone body = getAnimationProcessor().getBone("body");
        //TODO
    }


}