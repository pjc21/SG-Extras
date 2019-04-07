package pjc21.mod.objects.items;

import net.minecraft.item.Item;
import pjc21.mod.Main;
import pjc21.mod.init.ItemInit;
import pjc21.mod.util.interfaces.IHasModel;

public class ItemBase extends Item implements IHasModel
{
	public ItemBase(String name) 
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.PAULSTAB);
		
		ItemInit.ITEMS.add(this);
	}

	@Override
	public void registerModels() 
	{
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
