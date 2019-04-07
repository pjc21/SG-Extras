package pjc21.mod.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import pjc21.mod.util.helpers.GetSaveDir;
import pjc21.mod.util.helpers.SGAddress;

public class CommandReadAddress extends CommandBase
{
	@Override
	public String getName() 
	{
		return "sgaddress";
	}

	@Override
	public String getUsage(ICommandSender sender) 
	{
		return "sgaddress <customname> <id> <allowasloot> <allowProtection> <x> <y> <z> {}";
	}

	@Override
	public int getRequiredPermissionLevel()
    {
        return 2;
    }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		SGAddress sgaddress = new SGAddress();
		String dimID = null;
		
		if (args.length < 7)
        {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid use - sgaddress <customname>(optional if only 1 gate in dim) <id> <allowasloot>[allow this gate address in loot pool] <allowProtection>[allow this gate to be protected] <x> <y> <z> {}"));
			return;
        }
		else
        {
        	sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
        	World world = sender.getEntityWorld();
        	
        	if(args.length == 7)
        	{
        		dimID = args[0];
        		sgaddress.setPos(parseBlockPos(sender, args, 3, false));
        		sgaddress.setAllowLoot(Boolean.parseBoolean(args[1]));
        		sgaddress.setAllowProtection(Boolean.parseBoolean(args[2]));
        	}
        	if(args.length == 8)
        	{
        		dimID = args[1];
        		sgaddress.setPos(parseBlockPos(sender, args, 4, false));
        		sgaddress.setAllowLoot(Boolean.parseBoolean(args[2]));
        		sgaddress.setAllowProtection(Boolean.parseBoolean(args[3]));
        	}

        	if (!world.isBlockLoaded(sgaddress.getPos()))
            {
            	sender.sendMessage(new TextComponentString(TextFormatting.RED + "BlockPos not loaded"));
    			return;
            }
            else
            {
                IBlockState iblockstate = world.getBlockState(sgaddress.getPos());
                TileEntity tileentity = world.getTileEntity(sgaddress.getPos());

                if (tileentity == null)
                {
                    throw new CommandException("commands.blockdata.notValid", new Object[0]);
                }
                else
                {
                    NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());
                    NBTTagCompound nbttagcompound1 = nbttagcompound.copy();
                    NBTTagCompound nbttagcompound2 = null;

                    if(args.length == 7)
                	{
                    	try
                        {
                            nbttagcompound2 = JsonToNBT.getTagFromJson(buildString(args, 6));
                        }
                        catch (NBTException nbtexception)
                        {
                            throw new CommandException("commands.blockdata.tagError", new Object[] {nbtexception.getMessage()});
                        }
                	}
                    
                    if(args.length == 8)
                	{
                    	try
                        {
                            nbttagcompound2 = JsonToNBT.getTagFromJson(buildString(args, 7));
                        }
                        catch (NBTException nbtexception)
                        {
                            throw new CommandException("commands.blockdata.tagError", new Object[] {nbtexception.getMessage()});
                        }
                	}

                    nbttagcompound.merge(nbttagcompound2);
                    nbttagcompound.setInteger("x", sgaddress.getPos().getX());
                    nbttagcompound.setInteger("y", sgaddress.getPos().getY());
                    nbttagcompound.setInteger("z", sgaddress.getPos().getZ());

                    if (nbttagcompound.equals(nbttagcompound1))
                    {
                    	if(nbttagcompound.hasKey("address", 8)) 
                    	{
                    		sgaddress.setAddress(nbttagcompound.getString("address"));
                    		sgaddress.setOrientation(nbttagcompound.getInteger("gateOrientation"));
                    		sgaddress.setFacing(nbttagcompound.getInteger("facingDirectionOfBase"));
                    	}
                    }
                }
            }
        }
		
		if(dimID != null)
		{
			if (args.length == 8)
			{
				sgaddress.setName(args[0]);
			}
			
			sgaddress.setId(Integer.parseInt(dimID));

			saveSGAddressToJson(sgaddress);
		}
	}

	public void saveSGAddressToJson(SGAddress sgaddress) 
	{
		GetSaveDir saveDirectory = new GetSaveDir();
		File saveDir = saveDirectory.getDataDir(true);
		
		boolean addressExists;
		
		if (saveDir != null)
		{
			File file = new File(saveDir, "SGGateAddress.json");

			if(file.exists() && !file.isDirectory()) 
			{ 
				JsonParser parser = new JsonParser();
				addressExists = false;
				
				try 
				{
					JsonObject jsontree = (JsonObject)parser.parse(new FileReader(file));
					JsonObject je = jsontree.getAsJsonObject();
					JsonArray ja = je.getAsJsonArray("SGAddress");
					
					for (JsonElement pa : ja) 
					{
						JsonObject addressObj = pa.getAsJsonObject();
					    String gateAddress = addressObj.get("Address").getAsString();
					    
					    if(sgaddress.getAddress().equals(gateAddress))
					    {
					    	addressExists = true;
					    }
					}
					
					if(!addressExists)
					{
						JsonObject jo = new JsonObject();
						JsonObject bp = new JsonObject();
						JsonArray ba = new JsonArray();

						if(sgaddress.getName() != "")
						{
							jo.addProperty("CustomName", sgaddress.getName());
						}

						bp.addProperty("X", sgaddress.getPos().getX());
						bp.addProperty("Y", sgaddress.getPos().getY());
						bp.addProperty("Z", sgaddress.getPos().getZ());
												
						ba.add(bp);

						jo.addProperty("Address", sgaddress.getAddress());
						jo.addProperty("DimID", sgaddress.getId());
						jo.addProperty("AllowAsLoot", sgaddress.getAllowLoot());
						jo.addProperty("AllowProtection", sgaddress.getAllowProtection());
						jo.add("BlockPos", ba);
						jo.addProperty("Orientation", sgaddress.getOrientation());
						jo.addProperty("Facing", sgaddress.getFacing());

						ja.add(jo);
						je.add("SGAddress", ja);
						
						GsonBuilder builder = new GsonBuilder();
				        builder.setPrettyPrinting();
				    	Gson gson = builder.create();
						
				    	try 
					    {
				    		Writer writer = new FileWriter(file,false);
				    		gson.toJson(je, writer); 
						    writer.flush();
						    writer.close();
					    	  
					    } 
					    catch (IOException e) 
					    {
							e.printStackTrace();
					    }
					}
				} 
				catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) 
				{
					e.printStackTrace();
				}
			}
			else
			{
				JsonArray ja = new JsonArray();
				JsonObject jo = new JsonObject();
				JsonObject bp = new JsonObject();
				JsonArray ba = new JsonArray();

				if(sgaddress.getName() != "")
				{
					jo.addProperty("CustomName", sgaddress.getName());
				}
				
				bp.addProperty("X", sgaddress.getPos().getX());
				bp.addProperty("Y", sgaddress.getPos().getY());
				bp.addProperty("Z", sgaddress.getPos().getZ());
				
				ba.add(bp);

				jo.addProperty("Address", sgaddress.getAddress());
				jo.addProperty("DimID", sgaddress.getId());
				jo.addProperty("AllowAsLoot", sgaddress.getAllowLoot());
				jo.addProperty("AllowProtection", sgaddress.getAllowProtection());
				jo.add("BlockPos", ba);
				jo.addProperty("Orientation", sgaddress.getOrientation());
				jo.addProperty("Facing", sgaddress.getFacing());

				ja.add(jo);
				
				JsonObject jbj = new JsonObject();
				jbj.add("SGAddress", ja);
				JsonArray json = new JsonArray();
				json.add(jbj);
				
				GsonBuilder builder = new GsonBuilder(); 
		        builder.setPrettyPrinting();
		    	Gson gson = builder.create();
				
		    	try 
			    {
		    		Writer writer = new FileWriter(file,false);
		    		gson.toJson(jbj, writer); 
				    writer.flush();
				    writer.close();
			    } 
			    catch (IOException e) 
			    {
					e.printStackTrace();
			    }
			}
		}
	}
}
