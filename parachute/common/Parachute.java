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
import net.minecraft.client.renderer.model.ModelResourceLocation;
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
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Parachute.MODID)
public class Parachute {

    public static final String PARACHUTE_NAME = "parachute";
    public static final String PACK_NAME = "pack";

    public static SoundEvent OPENCHUTE;
    public static SoundEvent LIFTCHUTE;

    public static EntityType<EntityParachute> PARACHUTE;
    public static Item PARACHUTE_ITEM;
    public static final EntityEquipmentSlot ARMOR_TYPE = EntityEquipmentSlot.CHEST;
//    static final int RENDER_INDEX = 1; // 0 is cloth, 1 is chain, 2 is iron, 3 is diamond and 4 is gold
    public static Item ITEM_PARACHUTE_PACK; // = new ItemParachutePack(ArmorMaterial.LEATHER, ARMOR_TYPE, props, Parachute.PACK_NAME);

    public static final String MODID = "parachutemod";
//    public static final String MODVERSION = "2.0.0";
    public static final String MCVERSION = "1.13.2";
    public static final String NAME = "Parachute Mod NG";
//    public static final String GUIFACTORY = "com.parachute.client.ParachuteConfigGUIFactory";
//    public static StatBasic parachuteDeployed = new StatBasic("stat.parachuteDeployed", new TextComponentTranslation("stat.parachuteDeployed"));
//    public static StatBasic parachuteDistance = new StatBasic("stat.parachuteDistance",
//            new TextComponentTranslation("stat.parachuteDistance"), StatBase.distanceStatType);

//    @SidedProxy(clientSide = "com.parachute.client.ParachuteClientProxy", serverSide = "com.parachute.common.ParachuteServerProxy")
//    public static IProxy clientProxy;

//    @Mod.Instance(MODID)
//    public static Parachute instance;

    @SuppressWarnings("unused")
//    @Mod.EventHandler
    private static final Logger LOGGER = LogManager.getLogger();

    public Parachute() {
        LOGGER.info("calling Parachute::CTOR");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("calling Parachute::setup");
        // some preinit code
//        ConfigHandler.preInit(event);
        int entityID = 1;
//        EntityRegistry.registerModEntity(new ResourceLocation(Parachute.MODID, PARACHUTE_NAME),
//                EntityParachute.class, PARACHUTE_NAME, entityID, Parachute.instance, 80, 5, true);

//        GameRegistry.findRegistry(Item.class).registerAll(ITEM_PARACHUTE_PACK, PARACHUTE_ITEM);
        PacketHandler.init();
        MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerHurtEvent());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("calling Parachute::doClientStuff");
        ModelResourceLocation parachuteResource = new ModelResourceLocation(Parachute.MODID + ":" + Parachute.PARACHUTE_NAME);
        ModelResourceLocation packResource = new ModelResourceLocation(Parachute.MODID + ":" + Parachute.PACK_NAME);
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new);
//        ModelLoader.setCustomModelResourceLocation(Parachute.PARACHUTE_ITEM, 0, parachuteResource);
//        ModelLoader.setCustomModelResourceLocation(Parachute.ITEM_PARACHUTE_PACK, 0, packResource);
        ModKeyBinding.registerKeyBinding();

//        MinecraftForge.EVENT_BUS.register(new ConfigHandler.ConfigEventHandler());
        MinecraftForge.EVENT_BUS.register(new ParachuteInputEvent());
        MinecraftForge.EVENT_BUS.register(new HudCompassRenderer());
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD event bus
    @SuppressWarnings("unchecked, unused")
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
            LOGGER.info("calling Parachute::RegistryEvents::onEntityRegistry");
            PARACHUTE = EntityType.Builder.create(EntityParachute.class, EntityParachute::new).tracker(80, 5, true).build(MODID);
            event.getRegistry().register(PARACHUTE);
        }

        @SubscribeEvent
        public  void onItemRegistry(final RegistryEvent<Item> event) {
            LOGGER.info("calling Parachute::RegistryEvents::onItemRegistry");
            Item.Properties props = new Item.Properties();
            props.maxStackSize(4);
            PARACHUTE_ITEM = new ItemParachute(ItemTier.IRON, props, Parachute.PARACHUTE_NAME);
            props.maxStackSize(0);
            ITEM_PARACHUTE_PACK = new ItemParachutePack(ArmorMaterial.LEATHER, ARMOR_TYPE, props, Parachute.PACK_NAME);
        }
    }
//    public void preInit(FMLPreInitializationEvent event) {
//        logger = event.getModLog();
//        ConfigHandler.preInit(event);
//        int entityID = 1;
//        EntityRegistry.registerModEntity(new ResourceLocation(Parachute.MODID, PARACHUTE_NAME),
//                EntityParachute.class, PARACHUTE_NAME, entityID, Parachute.instance, 80, 5, true);
//
//        GameRegistry.findRegistry(Item.class).registerAll(ITEM_PARACHUTE_PACK, PARACHUTE_ITEM);
//        PacketHandler.init();
//        clientProxy.preInit();
//    }
//
//    @SuppressWarnings("unused")
//    @Mod.EventHandler
//    public void Init(FMLInitializationEvent event) {
//        MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
//        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandler());
//        MinecraftForge.EVENT_BUS.register(new PlayerHurtEvent());
//
//        // add the parachute statistics
//        Parachute.parachuteDeployed.registerStat();
//        Parachute.parachuteDistance.initIndependentStat().registerStat();
//        clientProxy.Init();
//    }

    @SuppressWarnings("unused")
//    @Mod.EventHandler
//    public void postInit(FMLPostInitializationEvent event) {
//        clientProxy.postInit();
//    }

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


