package pjc21.mod.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import pjc21.mod.objects.blocks.BlockActivator;

public class BlockInit 
{
	public static final List<Block> BLOCKS = new ArrayList<Block>();

	public static final Block BLOCK_ACTIVATOR = new BlockActivator("block_activator");

}
