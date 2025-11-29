package com.quogle.lavarise.menu;

import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.quogle.lavarise.LavaRise;
import java.util.function.Supplier;
import net.minecraft.world.flag.FeatureFlags;// Important!

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.MENU, LavaRise.MOD_ID);

    public static final Supplier<MenuType<DepotMenu>> DEPOT_MENU = MENU_TYPES.register(
            "depot_menu",
            () -> new MenuType<>(DepotMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );
}
