package parachute.common;

//
// Copyright 2011 Michael Sheppard (crackedEgg)
//

import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import parachute.common.EntityParachute;
import net.minecraft.src.*;
import net.minecraftforge.common.ForgeVersion;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.Mod.Instance;

import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.SidedProxy;

// mod info, version
interface ModInfo {
	public final static String version = "1.3.2";
	public final static String channel = "Parachute";
	public final static String FMLVersion = "4.1.4.298";
}

@Mod (
	modid = "ParachuteMod",
	name = "Parachute Mod",
	version = ModInfo.version
)

@NetworkMod (
	clientSideRequired = true,
	serverSideRequired = true,
	packetHandler = ParachutePacketHandler.class,
	connectionHandler = ParachutePacketHandler.class,
	channels = {ModInfo.channel}
)

public class Parachute
{
	private int heightLimit;
	private int chuteColor;
	private boolean thermals;
	private static int itemID;
	private int entityID = EntityRegistry.findGlobalUniqueEntityId();
	public final Properties props = new Properties();

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
//		checkFMLVersion();
		File configFile = event.getSuggestedConfigurationFile();
		try {
			init(configFile);
		} catch (IOException e) {
			System.err.println("Doh! Parachute.init() crashed.");
		}
		proxy.registerRenderTextures();
		proxy.registerRenderer();
		proxy.registerKeyHandler();
    }

	@Init
	public void load(FMLInitializationEvent event) {
		parachuteItem = (new ItemParachute(itemID, EnumToolMaterial.WOOD)).setIconIndex(0).setItemName("Parachute");
		EntityRegistry.registerModEntity(EntityParachute.class, "Parachute", entityID, this, 64, 10, true);

		GameRegistry.addRecipe(new ItemStack(parachuteItem, 1), new Object[] {
			"###", "X X", " L ", '#', Block.cloth, 'X', Item.silk, 'L', Item.leather
		});

		LanguageRegistry.addName(parachuteItem, "Parachute");
		NetworkRegistry.instance().registerConnectionHandler(new ParachutePacketHandler());
	}
	
	public String getVersion() {
		return ModInfo.version;
	}
	
//	public void checkFMLVersion() {
//		String runningVersion = ForgeVersion.getVersion();
//		if (!builtWithFMLVersion()) {
//			System.err.println("WARNING: The parachute mod was built with ForgeModLoader version " + ModInfo.FMLVersion +
//					" and may not work correctly with ForgeModLoader version " + runningVersion);
//		} else {
//			System.err.println("INFO: FMLVersion requirements for the parachute mod have been satisfied.");
//		}
//	}
//	
//	public boolean builtWithFMLVersion() {
//		return (ForgeVersion.getMajorVersion() == 4 && ForgeVersion.getMinorVersion() == 1 &&
//				 ForgeVersion.getRevisionVersion() == 4 && ForgeVersion.getBuildVersion() == 298); 
//	}

	public void loadConfig(File cfgFile) throws IOException {
		try {
			if (!cfgFile.exists() && !cfgFile.createNewFile()) {
				return;
			}

			if (cfgFile.canRead()) {
				FileInputStream fileinputstream = new FileInputStream(cfgFile);
				props.load(fileinputstream);
				fileinputstream.close();
			}
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}

	public void saveConfig(File cfgFile) throws IOException {
		String comments = " Parachute Mod Config\n"
				+ " Michael Sheppard (crackedEgg)\n\n"
				+ " itemID=2011 is the default\n"
				+ " heightLimit=0 disables altitude limiting\n"
				+ " thermals - enable|disable space key thermals\n\n"
				+ " Color index numbers:\n" + " random     - -1\n"
				+ " black      -  0\n" + " red        -  1\n"
				+ " green      -  2\n" + " brown      -  3\n"
				+ " blue       -  4\n" + " purple     -  5\n"
				+ " cyan       -  6\n" + " light grey -  7\n"
				+ " dark grey  -  8\n" + " magenta    -  9\n"
				+ " lime       - 10\n" + " yellow     - 11\n"
				+ " light blue - 12\n" + " pink       - 13\n"
				+ " orange     - 14\n" + " white      - 15\n";

		try {
			if (!cfgFile.exists() && !cfgFile.createNewFile()) {
				return;
			}
			if (cfgFile.canWrite()) {
				FileOutputStream fileoutputstream = new FileOutputStream(cfgFile);
				props.store(fileoutputstream, comments);
				fileoutputstream.close();
			}
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}

	private void init(File cfgFile) throws IOException {
		// initialize to default values
		initDefaults();

		try {
			loadConfig(cfgFile);

			if (props.containsKey("heightLimit")) {
				heightLimit = Integer.parseInt(props.getProperty("heightLimit"));
			}

			if (props.containsKey("chuteColor")) {
				chuteColor = Integer.parseInt(props.getProperty("chuteColor"));
			}

			if (props.containsKey("allowThermals")) {
				thermals = Boolean.parseBoolean(props.getProperty("allowThermals"));
			}

			if (props.containsKey("itemID")) {
				itemID = Integer.parseInt(props.getProperty("itemID"));
			}
			
			props.setProperty("heightLimit", Integer.toString(heightLimit));

			props.setProperty("allowThermals", Boolean.toString(thermals));

			props.setProperty("chuteColor", Integer.toString(chuteColor));

			props.setProperty("itemID", Integer.toString(itemID));

		} catch (IOException e) {
			throw new IOException(e.getMessage());
		} finally {
			saveConfig(cfgFile);
		}
	}
	
	public double getMaxAltitude() {
		return heightLimit;
	}
	
	public boolean getAllowThermals() {
		return thermals;
	}
	
	public int getChuteColor() {
		return chuteColor;
	}

	protected void initDefaults() {
		heightLimit = 225;
		chuteColor = -1;
		itemID = 2011;
		thermals = true;
	}

	public static int getItemID() {
		return itemID;
	}
}
