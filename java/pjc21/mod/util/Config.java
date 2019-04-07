package pjc21.mod.util;

import java.io.File;
import java.util.HashSet;

import net.minecraftforge.common.config.Configuration;

public class Config 
{
	public static Configuration config;
	public static String CATEGORY_BLOCKS = "BLOCKS";
	public static String CATEGORY_LOOT = "LOOT";
	
	public static boolean allowBreakGate = false;
	public static boolean allowExplodeGate = false;
	public static boolean allowBelowGate = true;
	
	public static boolean allowAccessBelowGate = false;
	public static boolean allowControllerAccess = false;
	
	public static boolean allowLoot = true;
	public static String[] lootTables;
	public static HashSet<String> allowedTables = new HashSet<>();
	public static double randomChance = 0.5;
	
	public static void load(File configFile) 
	{
		config = new Configuration(configFile);
		config.load();

		allowBreakGate = config.get(CATEGORY_BLOCKS, "Allow breaking Stargates", false, "Can players break Stargates, if false only players with OP can break - this only applies to stargates added to the SGGateAddress file").getBoolean();
		allowExplodeGate = config.get(CATEGORY_BLOCKS, "Allow explosions to destroy Stargates & DHD's", false, "Can Explosions destroy Stargates and linked DHD's - this only applies to stargates added to the SGGateAddress file").getBoolean();
		allowBelowGate = config.get(CATEGORY_BLOCKS, "Allow 1 block below Stargate to be protected", true, "If true only players with OP can break blocks/TE 1 block below Stargates & can not be destroyed by explosions - this only applies to stargates added to the SGGateAddress file").getBoolean();
		allowAccessBelowGate = config.get(CATEGORY_BLOCKS, "Allow access below gate", false, "Allow right-click access to blocks/TE 1 block below gate, if false only players with OP can access - this only applies to stargates added to the SGGateAddress file").getBoolean();
		allowControllerAccess = config.get(CATEGORY_BLOCKS, "Allow access to DHD", false, "Allow access to linked DHD fuel panel, if false only players with OP can access - this only applies to stargates added to the SGGateAddress file").getBoolean();

		allowLoot = config.get(CATEGORY_LOOT, "Allow as Loot", true, "Allow Ancient Tablets as Loot").getBoolean();
		lootTables = config.getStringList("LootTables", CATEGORY_LOOT, new String[] { "minecraft:chests/desert_pyramid", "minecraft:chests/jungle_temple" }, "Gets the list of loot tables to add Ancient Tablets to");
		randomChance = config.get(CATEGORY_LOOT, "Loot random chance", 0.5, "The random chance for loot to spawn").getDouble();

		for (String s : lootTables) 
		{
			allowedTables.add(s);
	    }

		config.save();
	}
}
