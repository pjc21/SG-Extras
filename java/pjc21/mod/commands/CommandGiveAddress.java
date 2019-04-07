package pjc21.mod.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import pjc21.mod.util.helpers.GetSaveDir;

public class CommandGiveAddress extends CommandBase
{
	private int ID;
	private boolean cName = false;
	private String CusName;
	
	@Override
	public String getName() 
	{
		return "sgtablet";
	}

	@Override
	public String getUsage(ICommandSender sender) 
	{
		return "sgtablet <player> <item> <id> <customname>";
	}

	@Override
	public int getRequiredPermissionLevel()
    {
        return 2;
    }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		String id;
		CusName = "";
		
		if (args.length < 3)
        {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid use - sgtablet <player> <item> <id> <customname>(conditional)"));
			return;
        }
        else
        {
            EntityPlayer entityplayer = getPlayer(server, sender, args[0]);
            Item item = getItemByText(sender, args[1]);
            ItemStack itemstack = new ItemStack(item, 1, 0);

            GetSaveDir saveDirectory = new GetSaveDir();
    		File saveDir = saveDirectory.getDataDir(true);

            File f = new File(saveDir, "SGGateAddress.json");

    		if(f.exists() && !f.isDirectory()) 
    		{ 
    			id = args[2];
    		}
    		else
    		{
    			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Gate Address File does not exist"));
    			return;
    		}
            
    		if(id != null)
    		{
    			this.ID = Integer.parseInt(id);
    			
    			if(args.length == 4)
    			{
    				cName = true;
    				CusName = args[3];
    			}
    			else 
    			{
    				cName = false;
    			}
    			
    			String s = getSGAddressFromJson(ID);

				try
                {
                    itemstack.setTagCompound(JsonToNBT.getTagFromJson(s));
                }
                catch (NBTException nbtexception)
                {
                    throw new CommandException("commands.give.tagError", new Object[] {nbtexception.getMessage()});
                }
    		}

            boolean flag = entityplayer.inventory.addItemStackToInventory(itemstack);

            if (flag)
            {
                entityplayer.world.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((entityplayer.getRNG().nextFloat() - entityplayer.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityplayer.inventoryContainer.detectAndSendChanges();
            }
        }
	}
	
	public String getSGAddressFromJson(int id) 
	{
		JsonParser parser = new JsonParser();
		
		GetSaveDir saveDirectory = new GetSaveDir();
		File saveDir = saveDirectory.getDataDir(true);
		
		File file = new File(saveDir, "SGGateAddress.json");
		
		try 
		{
			JsonObject jsontree = (JsonObject)parser.parse(new FileReader(file));
			JsonArray addressArray = jsontree.getAsJsonArray("SGAddress");
			
			for (JsonElement pa : addressArray) 
			{
			    JsonObject addressObj = pa.getAsJsonObject();
			    int dimid = addressObj.get("DimID").getAsInt();
			    String gateAddress = addressObj.get("Address").getAsString();
			    
			    if(cName)
			    {
			    	if(addressObj.has("CustomName"))
			    	{
			    		String name = addressObj.get("CustomName").getAsString();
				    	
				    	if(dimid == id && name.equals(CusName))
					    {
				    		String returnAddress = "{\"CustomName\":\""+name+"\",\"Address\":\""+gateAddress+"\",\"DimID\":\""+dimid+"\"}";
					    	return returnAddress;
					    }
			    	}
			    }
			    else 
			    {
			    	if(dimid == id && !addressObj.has("CustomName"))
				    {
			    		String returnAddress = "{\"Address\":\""+gateAddress+"\",\"DimID\":\""+dimid+"\"}";
				    	return returnAddress;
				    }
			    }
			}
		}
		catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
}
