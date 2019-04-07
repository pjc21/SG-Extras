package pjc21.mod.util.handlers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pjc21.mod.commands.CommandDeleteGate;
import pjc21.mod.commands.CommandGetWorldProvider;
import pjc21.mod.commands.CommandGiveAddress;
import pjc21.mod.commands.CommandLoadDim;
import pjc21.mod.commands.CommandReadAddress;
import pjc21.mod.commands.CommandSGBlockInfo;
import pjc21.mod.commands.CommandSetRandomSpawn;
import pjc21.mod.commands.CommandUnloadDim;
import pjc21.mod.events.EventListener;
import pjc21.mod.init.BlockInit;
import pjc21.mod.init.ItemInit;
import pjc21.mod.util.interfaces.IHasModel;


@EventBusSubscriber
public class RegistryHandler 
{
	
	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(ItemInit.ITEMS.toArray(new Item[0]));
	}
	
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(BlockInit.BLOCKS.toArray(new Block[0]));
	}
	
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event)
	{
		for(Item item : ItemInit.ITEMS)
		{
			if(item instanceof IHasModel)
			{
				((IHasModel)item).registerModels();
			}
		}
		
		for(Block block : BlockInit.BLOCKS)
		{
			if(block instanceof IHasModel)
			{
				((IHasModel)block).registerModels();
			}
		}
	}

	public static void preInitRegistries() {}
	
	public static void initRegistries()
	{
		MinecraftForge.EVENT_BUS.register(new EventListener());
		TileEntityHandler.registerTileEntities();
	}
	
	public static void postInitRegistries()	{}
	
	public static void serverRegistries(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandReadAddress());
		event.registerServerCommand(new CommandGiveAddress());
		event.registerServerCommand(new CommandUnloadDim());
		event.registerServerCommand(new CommandLoadDim());
		event.registerServerCommand(new CommandSetRandomSpawn());
		event.registerServerCommand(new CommandDeleteGate());
		event.registerServerCommand(new CommandGetWorldProvider());
		event.registerServerCommand(new CommandSGBlockInfo());
	}
}
