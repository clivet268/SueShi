package com.clivet268.sueshi;

import com.clivet268.sueshi.Entity.Shi;
import com.clivet268.sueshi.Entity.Sue;
import com.clivet268.sueshi.client.model.ShiModel;
import com.clivet268.sueshi.client.renderer.entity.ShiRenderer;
import com.clivet268.sueshi.client.renderer.entity.SueRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// Thanks to seymourimadeit for https://github.com/seymourimadeit/guardvillagers/
// which showed me good base for starting in neo forge
// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SueShi.MODID)
public class SueShi
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "sueshi";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<Sue>> SUE = ENTITIES.register("sue", () -> EntityType.Builder.of(Sue::new, MobCategory.CREATURE)
            .sized(2.0F, 0.8F).setShouldReceiveVelocityUpdates(true).build(MODID + "sue"));
    public static final DeferredHolder<EntityType<?>, EntityType<Shi>> SHI = ENTITIES.register("shi", () -> EntityType.Builder.of(Shi::new, MobCategory.CREATURE)
            .sized(1.4F, 0.6F).setShouldReceiveVelocityUpdates(true).build(MODID + "shi"));

    public SueShi(IEventBus modEventBus, ModContainer modContainer)
    {
        ENTITIES.register(modEventBus);

        modEventBus.addListener(this::addAttributes);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    private void addAttributes(final EntityAttributeCreationEvent event) {
        event.put(SUE.get(), Sue.createAttributes().build());
        event.put(SHI.get(), Shi.createAttributes().build());
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        //TODO is this okay as is? does it need more functionality in the future???
        @SubscribeEvent
        public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SUE.get(), SueRenderer::new);
            event.registerEntityRenderer(SHI.get(), ShiRenderer::new);
        }
    }
}

