package com.parachute.common;

//import net.minecraft.item.EnumDyeColor;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemCloth;
//import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ParachuteItemCraftedEvent {

    @SubscribeEvent
    public void event(PlayerEvent.ItemCraftedEvent craftedEvent) {
        if (craftedEvent.crafting.getItem() instanceof ItemParachute) {
            craftedEvent.player.addStat(Parachute.buildParachute, 1);
//            EnumDyeColor[] canopy = new EnumDyeColor[3];
//            for (int k = 0; k < 3; k++) { // scan the top three slots for same colored wool blocks
//                ItemStack stack = craftedEvent.craftMatrix.getStackInSlot(k);
//                if (stack != null) {
//                    Item item = stack.getItem();
//                    if (item instanceof ItemCloth) {
//                        canopy[k] = EnumDyeColor.byMetadata(item.getMetadata(stack));
//                        if (craftedEvent.player.worldObj.isRemote) {
//                            if (canopy[0].equals(canopy[1]) && canopy[0].equals(canopy[2])) {
//                                ConfigHandler.setChuteColor(canopy[0].toString());
//                            } else {
//                                ConfigHandler.setChuteColor("white");
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}
