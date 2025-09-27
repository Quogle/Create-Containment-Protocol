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

        GeoBone head = getAnimationProcessor().getBone("head");
        if (head == null) return;

        var player = entity.level().getNearestPlayer(entity, 16);
        if (player == null) return;

        double dx = player.getX() - entity.getX();
        double dy = player.getEyeY() - entity.getEyeY();
        double dz = player.getZ() - entity.getZ();

        double targetYaw = Math.atan2(dz, dx);
        double entityYaw = Math.toRadians(entity.yBodyRot);

        double yawDiff = targetYaw - entityYaw;
        while (yawDiff > Math.PI) yawDiff -= 2 * Math.PI;
        while (yawDiff < -Math.PI) yawDiff += 2 * Math.PI;

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        double targetPitch = Math.atan2(dy, distanceXZ);

        float pitchDegrees = (float) Math.toDegrees(targetPitch);
        float yawDegrees = (float) Math.toDegrees(yawDiff);

        pitchDegrees = clamp(pitchDegrees, -30f, 30f);
        yawDegrees = clamp(yawDegrees, -45f, 45f);

        head.setRotX(-pitchDegrees);
        head.setRotY(-yawDegrees);
    }

    private static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}