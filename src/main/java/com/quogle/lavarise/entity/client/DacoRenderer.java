package com.quogle.lavarise.entity.client;

import com.quogle.lavarise.entity.custom.DacoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DacoRenderer extends GeoEntityRenderer<DacoEntity> {
    public DacoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DacoModel());
        this.shadowRadius = 0.35f;
    }
}

