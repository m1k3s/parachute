//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
//

package parachute.common;

import java.util.*;

import org.lwjgl.input.Keyboard;

import parachute.common.EntityParachute;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.Mod.Instance;

import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.SidedProxy;

@Mod (
	modid = Parachute.ID,
	name = Parachute.name,
	version = Parachute.VER
)

@NetworkMod (
	clientSideRequired = true,
	serverSideRequired = true,
	packetHandler = ParachutePacketHandler.class,
	connectionHandler = ParachutePacketHandler.class,
	channels = {Parachute.CHANNEL}
)

public class Parachute {
	
	static EnumToolMaterial EnumParachuteMaterial = EnumHelper.addToolMaterial("nylon", 0, 59, 2.0F, 0, 15);
	
	public static final String ID = "ParachuteMod";
	public static final String VER = "1.4.7";
	public static final String CHANNEL = "Parachute";
	public static final String name = "Parachute Mod";
	
	private int heightLimit;
	private int chuteColor;
	private boolean thermals;
	private boolean autoDeploy;
	private int fallDistance;
	private static int itemID;
	private int entityID = EntityRegistry.findGlobalUniqueEntityId();

	@SidedProxy (
		clientSide = "parachute.client.ClientProxyParachute",
		serverSide = "parachute.common.CommonProxyParachute"
	)
	public static CommonProxyParachute proxy;

	public static Item parachuteItem;
	
	@Instance
	public static Parachute instance;

	public Parachute() {
		
	}
	
	@PreInit
    public void preLoad(FMLPreInitializationEvent event) {
		String generalComments = Parachute.name + " Config\nMichael Sheppard (crackedEgg)";
		String itemComment = "itemID - customize the ItemID (2500)";
		String heightComment = "heightLimit  - 0 (zero) disables altitude limiting (225)";
		String thermalComment = "allowThermals - true|false enable/disable thermals (true)";
		String deployComment = "autoDeploy - true|false enable/disable auto parachute deployment (false)";
		String fallComment = "fallDistance - maximum falling distance before auto deploy (2 - 20) (5)";
		String colorComment = "Color index numbers:\nrandom     - -1\n"
							+ "black      -  0\nred        -  1\n"
							+ "green      -  2\nbrown      -  3\n"
							+ "blue       -  4\npurple     -  5\n"
							+ "cyan       -  6\nlight grey -  7\n"
							+ "dark grey  -  8\nmagenta    -  9\n"
							+ "lime       - 10\nyellow     - 11\n"
							+ "light blue - 12\npink       - 13\n"
							+ "orange     - 14\nwhite      - 15";
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		heightLimit = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 225, heightComment).getInt();
		chuteColor = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", -1, colorComment).getInt();
		thermals = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment).getBoolean(true);
		autoDeploy = config.get(Configuration.CATEGORY_GENERAL, "autoDeploy", false, deployComment).getBoolean(false);
		fallDistance = config.get(Configuration.CATEGORY_GENERAL, "fallDistance", 5, fallComment).getInt();
		itemID = config.get(Configuration.CATEGORY_GENERAL, "itemID", 2500, itemComment).getInt();
		
		// fix fallDistance  (2 < fallDistance < 20)
		fallDistance = (fallDistance < 2) ? 2 : (fallDistance > 20) ? 20 : fallDistance;
		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);
		
		config.save();
		
		proxy.registerRenderTextures();
		proxy.registerRenderer();
		proxy.registerKeyHandler(); // for keyboard control of parachute
		
		// only register tick handler if autoDeploy is enabled
		if (autoDeploy) {
			proxy.registerServerTickHandler(); // for auto deployment feature
		}
    }

	@Init
	public void load(FMLInitializationEvent event) {
		parachuteItem = (new ItemParachute(itemID, EnumParachuteMaterial));
		parachuteItem.setIconIndex(0);
		parachuteItem.setItemName("Parachute");
		EntityRegistry.registerModEntity(EntityParachute.class, "Parachute", entityID, this, 64, 10, true);

		GameRegistry.addRecipe(new ItemStack(parachuteItem, 1), new Object[] {
			"###", "X X", " L ", '#', Block.cloth, 'X', Item.silk, 'L', Item.leather
		});

		LanguageRegistry.addName(parachuteItem, "Parachute");
		NetworkRegistry.instance().registerConnectionHandler(new ParachutePacketHandler());
		instance = this;
	}
	
	public String getVersion() {
		return Parachute.VER;
	}
	
	public double getMaxAltitude() {
		return heightLimit;
	}
	
	public boolean getAllowThermals() {
		return thermals;
	}
	
	public boolean getAutoDeploy() {
		return autoDeploy;
	}
	
	public int getChuteColor() {
		return chuteColor;
	}
	
	public int getFallDistance() {
		return fallDistance;
	}

	public static int getItemID() {
		return itemID;
	}
	
}
