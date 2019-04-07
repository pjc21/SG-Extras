package pjc21.mod.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import pjc21.mod.objects.items.AncientTablet;

public class ItemInit 
{
	public static final List<Item> ITEMS = new ArrayList<Item>();
	
	public static final Item ANCIENT_TABLET = new AncientTablet("ancient_tablet");
}
