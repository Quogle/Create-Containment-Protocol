package com.quogle.lavarise.entity.client;

import com.quogle.lavarise.LavaRise;
import com.quogle.lavarise.entity.custom.DacoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class DacoModel extends GeoModel<DacoEntity> {

    @Override
    public ResourceLocation getModelResource(DacoEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "geo/daco.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DacoEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/entity/daco.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DacoEntity animatable) {
        return DacoAnimations.ANIM_FILE;
    }

    @Override
    public void setCustomAnimations(DacoEntity entity, long instanceId, AnimationState<DacoEntity> state) {
        super.setCustomAnimations(entity, instanceId, state);

        GeoBone leftArm = getAnimationProcessor().getBone("left_arm");
        GeoBone rightArm = getAnimationProcessor().getBone("right_arm");

        if (leftArm != null && rightArm != null) {
            float ageInTicks = (float) state.getAnimationTick();

            float swingSpeedX = 0.085f;      // Controls speed of swinging
            float swingAmplitudeX = 0.15f; // Controls how much the arms move

            float swingSpeedZ = 0.085f;      // Controls speed of swinging
            float swingAmplitudeZ = 0.13f; // Controls how much the arms move

            float phase = ageInTicks * swingSpeedZ;
            // Sine-based swinging
            float leftArmRotX = (float) Math.sin(ageInTicks * swingSpeedX) * swingAmplitudeX;
            float rightArmRotX = (float) Math.sin(ageInTicks * swingSpeedX + Math.PI) * swingAmplitudeX;

            float leftArmRotZ = (float) ((Math.cos(phase) + 1.0) / 2.0) * swingAmplitudeZ;
            float rightArmRotZ = (float) ((Math.cos(phase) + 1.0) / 2.0) * swingAmplitudeZ;

            // Apply rotation to arms (around X axis for up/down swinging)
            leftArm.setRotX(leftArmRotX);
            rightArm.setRotX(rightArmRotX);

            leftArm.setRotZ(leftArmRotZ);
            rightArm.setRotZ(-rightArmRotZ);
        }

        GeoBone head = getAnimationProcessor().getBone("head");
        if (head == null) return;

        float targetPitch = 0f;
        float targetYaw = 0f;

        var player = entity.level().getNearestPlayer(entity, 16);
        if (player != null) {

            //Calculate Distances between daco and closest player
            double dx = player.getX() - entity.getX();
            double dy = player.getEyeY() - entity.getEyeY();
            double dz = player.getZ() - entity.getZ();

            //Get yaw of player and daco
            double targetYawRad = Math.atan2(dz, dx) - Math.PI / 2;
            double entityYaw = Math.toRadians(entity.getYRot());

            //Get difference between daco and player yaw
            double yawDiff = targetYawRad - entityYaw;

            //Make angle difference stay in the range of [-pi, pi]
            while (yawDiff > Math.PI) yawDiff -= 2 * Math.PI;
            while (yawDiff < -Math.PI) yawDiff += 2 * Math.PI;

            //calc horizontal distance between daco and player
            double distanceXZ = Math.sqrt(dx * dx + dz * dz);
            double targetPitchRad = Math.atan2(dy, distanceXZ);

            //convert radians to degrees for clamping
            targetPitch = (float) Math.toDegrees(targetPitchRad);
            targetYaw = (float) Math.toDegrees(yawDiff);
        }

        //Clamp head movement
        targetPitch = clamp(targetPitch, -90f, 90f);
        targetYaw = clamp(targetYaw, -75f, 75f);

        //Interpolation Value
        float lerpFactor = 0.1f;
        entity.prevHeadPitch += (targetPitch - entity.prevHeadPitch) * lerpFactor;
        entity.prevHeadYaw += (targetYaw - entity.prevHeadYaw) * lerpFactor;

        //Apply rotation to bone in radians
        head.setRotX(entity.prevHeadPitch * ((float) Math.PI / 180F));
        head.setRotY(-entity.prevHeadYaw * ((float) Math.PI / 180F));

    }

    private static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}