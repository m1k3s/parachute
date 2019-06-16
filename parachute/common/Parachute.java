/*
 * Parachute.java
 *
 *  Copyright (c) 2019 Michael Sheppard
 *
 * =====GPL=============================================================
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 * =====================================================================
 */
package com.parachute.common;

import com.parachute.client.HudCompassRenderer;
import com.parachute.client.ModKeyBinding;
import com.parachute.client.ParachuteInputEvent;
import com.parachute.client.ParachuteRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Parachute.MODID)
public class Parachute {
    public static final String MODID = "parachutemod";
    private static final Logger LOGGER = LogManager.getLogger(Parachute.MODID);

    public static final String PARACHUTE_NAME = "parachute";
    public static final String PACK_NAME = "pack";

    public static boolean aadState = true;


    public Parachute() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.clientSpec);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings("unused")
    private void setup(final FMLCommonSetupEvent event) {
        ConfigHandler.loadConfig();
        PacketHandler.register();
        MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerHurtEvent());
    }

    @SuppressWarnings("unused")
    private void initClient(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ParachuteEntity.class, ParachuteRenderer::new);
        ModKeyBinding.registerKeyBinding();

        MinecraftForge.EVENT_BUS.register(new ParachuteInputEvent());
        MinecraftForge.EVENT_BUS.register(new HudCompassRenderer());
    }

    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(modid = Parachute.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    @ObjectHolder(Parachute.MODID)
    public static class RegistryEvents {
        public final static EntityType<ParachuteEntity> PARACHUTE = null;

        public final static Item PARACHUTE_ITEM = null;
        public static Item PARACHUTEPACK_ITEM = null;

        public static SoundEvent OPENCHUTE = null;
        public static SoundEvent LIFTCHUTE = null;

        @SubscribeEvent
        public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
//            PARACHUTE = EntityType.Builder.<ParachuteEntity>func_220322_a(ParachuteEntity::new, EntityClassification.MISC)
//                    .setTrackingRange(80)
//                    .setUpdateInterval(5)
//                    .setShouldReceiveVelocityUpdates(true)
//                    .func_220321_a(3.25f, (1.0f / 16.0f))
//                    .build(Parachute.MODID);
//
//            PARACHUTE.setRegistryName(Parachute.MODID, Parachute.PARACHUTE_NAME);
//
//            event.getRegistry().register(PARACHUTE);

            event.getRegistry().registerAll(EntityType.Builder.<ParachuteEntity>create(ParachuteEntity::new, EntityClassification.MISC)
                    .setTrackingRange(80)
                    .setUpdateInterval(5)
                    .setShouldReceiveVelocityUpdates(true)
                    .size(3.25f, (1.0f / 16.0f))
                    .build(Parachute.MODID)
                    .setRegistryName(Parachute.MODID, Parachute.PARACHUTE_NAME));
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> event) {
//            PARACHUTE_ITEM = new ParachuteItem(new Item.Properties()
//                    .maxStackSize(4)
//                    .group(ItemGroup.TRANSPORTATION))
//                    .setRegistryName(Parachute.MODID, Parachute.PARACHUTE_NAME);
//
            PARACHUTEPACK_ITEM = new ParachutePackItem(new Item.Properties()
                    .maxStackSize(1))
                    .setRegistryName(Parachute.MODID, Parachute.PACK_NAME);
//
//            event.getRegistry().registerAll(PARACHUTE_ITEM, PARACHUTEPACK_ITEM);
            event.getRegistry().registerAll(new ParachuteItem(new Item.Properties()
                    .maxStackSize(4)
                    .group(ItemGroup.TRANSPORTATION))
                    .setRegistryName(Parachute.MODID, Parachute.PARACHUTE_NAME),

                    PARACHUTEPACK_ITEM);
        }

        @SubscribeEvent
        public static void onSoundRegistry(final RegistryEvent.Register<SoundEvent> event) {
            OPENCHUTE = new SoundEvent(new ResourceLocation(Parachute.MODID + ":chuteopen")).setRegistryName("chuteopen");
            LIFTCHUTE = new SoundEvent(new ResourceLocation(Parachute.MODID + ":lift")).setRegistryName("lift");

            event.getRegistry().registerAll(OPENCHUTE, LIFTCHUTE);
        }
    }

    public static boolean isFalling(PlayerEntity player) {
        return (player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder());
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    // AAD state is tracked here
    public static boolean getAADState() {
        return aadState;
    }

    public static void setAadState(boolean state) {
        aadState = state;
    }

}


