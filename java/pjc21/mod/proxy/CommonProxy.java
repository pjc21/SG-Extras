package pjc21.mod.proxy;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public class CommonProxy 
{
	public void registerItemRenderer(Item item, int meta, String id) {}
	public void registerVariantRenderer(Item item, int meta, String filename, String id) {}
	public void openMyGui(NBTTagCompound nbtTagCompound) {}
}
