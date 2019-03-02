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
import com.parachute.client.RenderParachute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
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
        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerHurtEvent());
    }

    @SuppressWarnings("unused")
    private void initClient(final FMLClientSetupEvent event) {

        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new);
        ModKeyBinding.registerKeyBinding();

        MinecraftForge.EVENT_BUS.register(new ParachuteInputEvent());
        MinecraftForge.EVENT_BUS.register(new HudCompassRenderer());
    }

    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        public static EntityType<EntityParachute> PARACHUTE;

        public static Item PARACHUTE_ITEM;

        public static Item ITEM_PARACHUTE_PACK;
        public static SoundEvent OPENCHUTE;
        public static SoundEvent LIFTCHUTE;

        @SubscribeEvent
        public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
            PARACHUTE = EntityType.Builder.create(EntityParachute.class, EntityParachute::new).tracker(80, 5, true).build(MODID);
            PARACHUTE.setRegistryName(new ResourceLocation(Parachute.MODID, Parachute.PARACHUTE_NAME));
            event.getRegistry().register(PARACHUTE);
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> event) {
            PARACHUTE_ITEM = new ItemParachute(new Item.Properties().maxStackSize(4).group(ItemGroup.TRANSPORTATION))
                    .setRegistryName(new ResourceLocation(Parachute.MODID, Parachute.PARACHUTE_NAME));

            ITEM_PARACHUTE_PACK = new ItemParachutePack(new Item.Properties().maxStackSize(1))
                    .setRegistryName(new ResourceLocation(Parachute.MODID, Parachute.PACK_NAME));

            event.getRegistry().registerAll(PARACHUTE_ITEM, ITEM_PARACHUTE_PACK);
        }

        @SubscribeEvent
        public static void onSoundRegistry(final RegistryEvent.Register<SoundEvent> event) {
            OPENCHUTE = new SoundEvent(new ResourceLocation(Parachute.MODID + ":chuteopen")).setRegistryName("chuteopen");
            LIFTCHUTE = new SoundEvent(new ResourceLocation(Parachute.MODID + ":lift")).setRegistryName("lift");
            event.getRegistry().registerAll(OPENCHUTE, LIFTCHUTE);
        }
    }

    public static boolean isFalling(EntityPlayer player) {
        return (player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder());
    }

    @SuppressWarnings("unused")
    public static Logger getLogger() {
        return LOGGER;
    }

    // AAD state is tracked here
    public static boolean getAADState() { return aadState; }
    public static void setAadState(boolean state) {aadState = state; }

    // convienience methods
    public static boolean isClientSide(World w) {
        return w.isRemote;
    }

    public static boolean isServerSide(World w) {
        return !w.isRemote;
    }

}


