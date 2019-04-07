package pjc21.mod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import pjc21.mod.init.ItemInit;

public class Pjc21Tab extends CreativeTabs
{
	public Pjc21Tab(String label) { super("sgextras");
	this.setBackgroundImageName("pjc21.png");}
	public ItemStack getTabIconItem() { return new ItemStack(ItemInit.ANCIENT_TABLET);}
}
