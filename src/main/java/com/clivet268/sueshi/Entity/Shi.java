package com.clivet268.sueshi.Entity;

import com.clivet268.sueshi.SueShi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class Shi extends PathfinderMob implements NeutralMob, GeoEntity {
    public int ticksUntilIdleAnim = this.random.nextInt(400, 10000);
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(Shi.class, EntityDataSerializers.INT);

    RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("shi.walk");
    RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlay("shi.idle");

    AnimationController<?> controller = new AnimationController<>(this, "controller", 5, this::animController);


    @Nullable
    private UUID persistentAngerTarget;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final UniformInt VERY_PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(35, 60);

    public Shi(EntityType<? extends Shi> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 240.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 25.0D)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 20.D)
                .add(Attributes.ARMOR, 15);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        //TODO just un fire yourself
        //this.goalSelector.addGoal(1, new PanicGoal(this, 1.05));
        this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 64.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.addPersistentAngerSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readPersistentAngerSaveData(this.level(), pCompound);
    }

    @Override
    public EntityType<? extends Shi> getType() {
        return SueShi.SHI.get();
    }
    @Override
    public int getMaxHeadXRot() {
        return 0;
    }

    public PlayState animController(AnimationState<GeoAnimatable> state) {
        LivingEntity entity = (LivingEntity) state.getAnimatable();
        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        } else {
            return PlayState.STOP;
        }
    }
    //TODO this will be desynched(?) problem?
    @Override
    public void tick() {
        if(this.level().isClientSide) {
            if(ticksUntilIdleAnim <= 0 ) {
                this.navigation.stop();
                this.controller.tryTriggerAnimation("shi.idle");
                ticksUntilIdleAnim = this.random.nextInt(400, 10000);
            }
            else {
                ticksUntilIdleAnim--;
            }
        }
        super.tick();
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller.triggerableAnim("shi.idle", IDLE_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int pTime) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, pTime);
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }
    @Override
    public void setPersistentAngerTarget(@Nullable UUID pTarget) {
        this.persistentAngerTarget = pTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(VERY_PERSISTENT_ANGER_TIME.sample(this.random));
    }
}

