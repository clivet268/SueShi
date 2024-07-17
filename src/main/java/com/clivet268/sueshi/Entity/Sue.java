package com.clivet268.sueshi.Entity;

import com.clivet268.sueshi.SueShi;
import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;


import javax.annotation.Nullable;
import java.util.UUID;

public class Sue extends PathfinderMob implements NeutralMob, GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(Sue.class, EntityDataSerializers.INT);

    RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("sue.walk");


    @Nullable
    private UUID persistentAngerTarget;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final UniformInt FORGIVING_ANGER_TIME = TimeUtil.rangeOfSeconds(7, 17);

    public Sue(EntityType<? extends Sue> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 240.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 25.0D)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 20.D);
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
        //TOOO, who to not attack?
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Wolf.class, AbstractSchoolingFish.class, IronGolem.class, AbstractVillager.class));
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

    /*
        @Override
        public void tick() {
        }

     */
    @Override
    public boolean doHurtTarget(Entity pEntity) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damagesource = this.damageSources().mobAttack(this);
        if (this.level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), pEntity, damagesource, f);
        }

        boolean flag = pEntity.hurt(damagesource, f);
        if (flag) {
            float f1 = this.getKnockback(pEntity, damagesource);
            if (f1 > 0.0F && pEntity instanceof LivingEntity livingentity) {
                livingentity.knockback(
                        f1 * 0.5F,
                        Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                        -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0))
                );
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (this.level() instanceof ServerLevel serverlevel1) {
                EnchantmentHelper.doPostAttackEffects(serverlevel1, pEntity, damagesource);
            }

            this.playAttackSound();
        }
        this.stopBeingAngry();
        return flag;
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public @NotNull EntityType<? extends Sue> getType() {
        return SueShi.SUE.get();
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::animController)
                //.triggerableAnim(getAnimString(TODO), TODO)
        );
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
        this.setRemainingPersistentAngerTime(FORGIVING_ANGER_TIME.sample(this.random));
    }
}

