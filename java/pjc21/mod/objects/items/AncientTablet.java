package pjc21.mod.objects.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pjc21.mod.Main;

public class AncientTablet extends ItemBase
{
	public AncientTablet(String name) 
	{
		super(name);
	}
	
	public Item setMaxStackSize()
    {
        this.maxStackSize = 1;
        return this;
    }

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
		ItemStack itemStackIn = playerIn.getHeldItem(handIn);
		NBTTagCompound nbtTagCompound = itemStackIn.getTagCompound();
		
		if (worldIn.isRemote)
	    {
	        Main.proxy.openMyGui(nbtTagCompound);
	    }
	        
		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }
	
	/*private String[] extraLore;
	
	public void addLine(String line) 
	{
		List<String> list = new ArrayList<String>();
		
		for(String str : extraLore) 
		{
			list.add(str);
		}
		
		if(!list.contains(line))
		{
			list.add(line);
		}
		else
		{
			extraLore = list.toArray(new String[list.size()]);
		}
		
	}*/
	
	
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		if(stack.hasTagCompound()) 
		{
			if(stack.getTagCompound().hasKey("CustomName")) 
			{
				List<String> list = new ArrayList<String>();
				
				list.add(stack.getTagCompound().getString("CustomName"));
			}
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
