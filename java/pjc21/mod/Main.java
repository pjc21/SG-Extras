package pjc21.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import pjc21.mod.proxy.CommonProxy;
import pjc21.mod.util.Config;
import pjc21.mod.util.Reference;
import pjc21.mod.util.handlers.RegistryHandler;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)

public class Main 
{
	@Instance
	public static Main instance;
	
	@SidedProxy(clientSide = Reference.CLIENT, serverSide = Reference.COMMON)
	public static CommonProxy proxy;

	public static final CreativeTabs PAULSTAB = new Pjc21Tab("paulstab");
	public static final Logger logger = LogManager.getLogger(Reference.MODID);
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
		Config.load(event.getSuggestedConfigurationFile());
		RegistryHandler.preInitRegistries();
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event) 
	{
		RegistryHandler.initRegistries();
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) 
	{
		RegistryHandler.postInitRegistries();
	}
	
	@EventHandler
	public static void serverInit(FMLServerStartingEvent event) 
	{
		RegistryHandler.serverRegistries(event);
	}
}
