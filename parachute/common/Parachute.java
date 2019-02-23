/*
 * Parachute.java
 *
 * Copyright (c) 2017 Michael Sheppard
 *
 *  =====GPL=============================================================
 * $program is free software: you can redistribute it and/or modify
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
 *
 */
package com.parachute.common;

import com.parachute.client.HudCompassRenderer;
import com.parachute.client.ModKeyBinding;
import com.parachute.client.ParachuteInputEvent;
import com.parachute.client.RenderParachute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemTier;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;


@Mod(Parachute.MODID)
public class Parachute {
    public static final String MODID = "parachutemod";
    private static final Logger LOGGER = LogManager.getLogger(Parachute.MODID);

    public static final String PARACHUTE_NAME = "parachute";
    public static final String PACK_NAME = "pack";

    public static SoundEvent OPENCHUTE;
    public static SoundEvent LIFTCHUTE;

    public static EntityType<EntityParachute> PARACHUTE;
    public static Item PARACHUTE_ITEM;
    public static Item ITEM_PARACHUTE_PACK;

//    public static final String GUIFACTORY = "com.parachute.client.ParachuteConfigGUIFactory";
//    public static StatBasic parachuteDeployed = new StatBasic("stat.parachuteDeployed", new TextComponentTranslation("stat.parachuteDeployed"));
//    public static StatBasic parachuteDistance = new StatBasic("stat.parachuteDistance",
//            new TextComponentTranslation("stat.parachuteDistance"), StatBase.distanceStatType);


    public Parachute() {
        LOGGER.info("calling Parachute::CTOR");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings("unused")
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("calling Parachute::setup");
//        ConfigHandler.preInit(event);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.spec);
        PacketHandler.register();
        MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerHurtEvent());
    }

    @SuppressWarnings("unused")
    private void initClient(final FMLClientSetupEvent event) {
        LOGGER.info("calling Parachute::initClient");
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new);
        ModKeyBinding.registerKeyBinding();

        MinecraftForge.EVENT_BUS.register(new ParachuteInputEvent());
        MinecraftForge.EVENT_BUS.register(new HudCompassRenderer());
    }

    @SuppressWarnings("unused")
    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("forge", Parachute.MODID, () -> {
            LOGGER.info("Parachute Mod calling forge");
            return Parachute.MODID;
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        LOGGER.info(Parachute.MODID, event.getIMCStream().
                map(m -> m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD event bus
    @SuppressWarnings("unchecked, unused")
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
            LOGGER.info("calling Parachute::RegistryEvents::onEntityRegistry");
            PARACHUTE = EntityType.Builder.create(EntityParachute.class, EntityParachute::new).tracker(80, 5, true).build(MODID);
            PARACHUTE.setRegistryName(Parachute.PARACHUTE_NAME);
            event.getRegistry().register(PARACHUTE);
        }

        @SubscribeEvent
        public  void onItemRegistry(final RegistryEvent<Item> event) {
            LOGGER.info("calling Parachute::RegistryEvents::onItemRegistry");
            Item.Properties props = new Item.Properties();
            props.maxStackSize(4);
            PARACHUTE_ITEM = new ItemParachute(ItemTier.IRON, props, Parachute.PARACHUTE_NAME);
            props.maxStackSize(0);
            ITEM_PARACHUTE_PACK = new ItemParachutePack(ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST, props, Parachute.PACK_NAME);
        }
    }

    public static boolean isFalling(EntityPlayer player) {
        return (player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder());
    }

    @SuppressWarnings("unused")
    public static Logger getLogger() {
        return LOGGER;
    }

    // convienience methods
    public static boolean isClientSide(World w) {
        return w.isRemote;
    }

    public static boolean isServerSide(World w) {
        return !w.isRemote;
    }

}


