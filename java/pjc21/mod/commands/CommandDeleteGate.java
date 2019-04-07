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

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import pjc21.mod.util.helpers.GetSaveDir;

public class CommandDeleteGate extends CommandBase
{
	@Override
	public String getName() 
	{
		return "sgdelete";
	}

	@Override
	public String getUsage(ICommandSender sender) 
	{
		return "sgdelete <address>";
	}

	@Override
	public int getRequiredPermissionLevel()
    {
        return 2;
    }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		if (args.length < 1)
        {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid use - sgdelete <address>"));
			return;
        }
        else
        {
        	removeSGAddress(args[0]);
        }
	}
	
	public void removeSGAddress(String address) 
	{
		GetSaveDir saveDirectory = new GetSaveDir();
		File saveDir = saveDirectory.getDataDir(true);

		boolean addressExists;
		
		if (saveDir != null)
		{
			File file = new File(saveDir, "SGGateAddress.json");
			
			int j = -1;
			
			if(file.exists() && !file.isDirectory()) 
			{ 
				JsonParser parser = new JsonParser();
				addressExists = false;
				
				try 
				{
					JsonObject jsontree = (JsonObject)parser.parse(new FileReader(file));
					JsonObject je = jsontree.getAsJsonObject();
					JsonArray ja = je.getAsJsonArray("SGAddress");

					int i = -1;
					
					for (JsonElement pa : ja)
					{
						i++; 
						JsonObject addressObj = pa.getAsJsonObject();
					    String gateAddress = addressObj.get("Address").getAsString();

					    if(address.equals(gateAddress))
					    {
					    	addressExists = true;
					    	j = i;
					    }
					}
	
					if(addressExists)
					{
						ja.remove(j);
						
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
		}
	}
}
