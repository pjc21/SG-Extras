package pjc21.mod.util.handlers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import pjc21.mod.objects.blocks.tileentities.TileEntityAutoUser;
import pjc21.mod.util.Reference;

public class TileEntityHandler 
{
	public static void registerTileEntities()
	{
		GameRegistry.registerTileEntity(TileEntityAutoUser.class, new ResourceLocation(Reference.MODID + ":block_activator"));
	}
}
