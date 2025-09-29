package com.quogle.lavarise.entity.custom;

import net.minecraft.world.entity.*;
import software.bernie.geckolib.animation.AnimationState;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class DacoEntity extends Mob implements GeoEntity {

    public float prevHeadPitch = 0f;
    public float prevHeadYaw = 0f;

    private final AnimatableManager<DacoEntity> animatableManager = new AnimatableManager<>(this);

    public DacoEntity(EntityType<DacoEntity> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20d)
                .add(Attributes.MOVEMENT_SPEED, 0.25d)
                .add(Attributes.FOLLOW_RANGE, 16.0D); // non-zero speed
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<DacoEntity> state) {
        state.setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private final AnimatableInstanceCache cache = new AnimatableInstanceCache(this) {
        @Override
        public <T extends GeoAnimatable> AnimatableManager<T> getManagerForId(long uniqueId) {
            @SuppressWarnings("unchecked")
            AnimatableManager<T> manager = (AnimatableManager<T>) animatableManager;
            return manager;
        }
    };

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}
