package pjc21.mod.objects.loottables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetNBT;
import pjc21.mod.init.ItemInit;
import pjc21.mod.util.Config;
import pjc21.mod.util.helpers.GetSaveDir;

public class SGLoot
{
	public LootTable createTable()
	{
		JsonParser parser = new JsonParser();
		
		GetSaveDir saveDirectory = new GetSaveDir();
		File saveDir = saveDirectory.getDataDir(true);
		
		if(saveDir != null)
        {
			File file = new File(saveDir, "SGGateAddress.json");
			
			if(file.exists() && !file.isDirectory()) 
			{ 
				List<String> tags = new ArrayList<>();
				
				LootTable SGLOOT_TABLE = new LootTable(new LootPool[0]);

				NBTTagCompound tagIn = null;

				try 
				{
					JsonObject jsontree = (JsonObject)parser.parse(new FileReader(file));
					JsonArray addressArray = jsontree.getAsJsonArray("SGAddress");
					
					for (JsonElement pa : addressArray) 
					{
					    JsonObject addressObj = pa.getAsJsonObject();
					    
					    if(addressObj.get("AllowAsLoot").getAsBoolean())
					    {
					    	int dimid = addressObj.get("DimID").getAsInt();
						    String gateAddress = addressObj.get("Address").getAsString();

					    	if(addressObj.has("CustomName"))
					    	{
					    		String name = addressObj.get("CustomName").getAsString();

					    		tags.add("{\"CustomName\":\""+name+"\",\"Address\":\""+gateAddress+"\",\"DimID\":\""+dimid+"\"}");
					    	}
					    	else
					    	{
					    		tags.add("{\"Address\":\""+gateAddress+"\",\"DimID\":\""+dimid+"\"}");
					    	}
					    }
					}
				}
				catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) 
				{
					e.printStackTrace();
				}
				
				ArrayList<LootEntryItem> entries = new ArrayList<LootEntryItem>();

				for (String st : tags) 
				{
					try 
					{
						tagIn = JsonToNBT.getTagFromJson(st);
					} 
					catch (NBTException e) 
					{
						e.printStackTrace();
					}
					
					entries.add(new LootEntryItem(ItemInit.ANCIENT_TABLET, 1, 0, new LootFunction[] { new SetNBT(new LootCondition[] {}, tagIn)  }, null, "sgextras:ancient_tablet"));
				}

				LootEntry [] entriesArray = entries.toArray(new LootEntry[entries.size()]);
				LootPool pool = new LootPool(entriesArray, new LootCondition[] { new RandomChance((float) Config.randomChance) }, new RandomValueRange(1), new RandomValueRange(0, 1), "sg_address");
				SGLOOT_TABLE.addPool(pool);
				
				return SGLOOT_TABLE;
			}
        }
		return null;
	}
	
	/*public File saveSGTabletInfoToJson()
	{
		GetSaveDir saveDirectory = new GetSaveDir();
		File saveDir = saveDirectory.getDataDir(true);
		
		boolean addressExists;
		
		if (saveDir != null)
		{
			File file = new File(saveDir, "SGGateAddress.json");

			if(file.exists() && !file.isDirectory()) 
			{ 
				System.out.println("Found SG Address");
				System.out.println(file);
				
				GetLootDir lootDirectory = new GetLootDir();
				File lootDir = lootDirectory.getDataDir(true);
				
				JsonParser parser = new JsonParser();
				if(lootDir != null)
				{
					File lootFile = new File(lootDir, "tabletaddress.json");

					if(lootFile.exists() && !lootFile.isDirectory()) 
					{ 
						System.out.println("Loot File Exists");
						
						try 
						{
							JsonObject jsontree = (JsonObject)parser.parse(new FileReader(file));
							JsonObject je = jsontree.getAsJsonObject();
							JsonArray ja = je.getAsJsonArray("SGAddress");
							
							String returnAddress;
							
							JsonArray ja1 = new JsonArray();
							JsonObject jo = new JsonObject();
							JsonArray ba = new JsonArray();
							JsonArray co = new JsonArray();
							JsonObject ct = new JsonObject();
							
							for (JsonElement pa : ja) 
							{
								JsonObject addressObj = pa.getAsJsonObject();
							    String gateAddress = addressObj.get("Address").getAsString();
							    String dimID = addressObj.get("DimID").getAsString();
							    
							    JsonObject cp = new JsonObject();
							    JsonArray cpp = new JsonArray();
							    JsonObject bp = new JsonObject();

							    if(addressObj.has("CustomName"))
						    	{
						    		String name = addressObj.get("CustomName").getAsString();
						    		returnAddress = "{CustomName:"+'"'+name+'"'+",Address:"+'"'+gateAddress+'"'+",DimID:"+'"'+dimID+'"'+"}";
						    	}
								else
								{
									returnAddress = "{Address:"+'"'+gateAddress+'"'+",DimID:"+'"'+dimID+'"'+"}";
								}

								cp.addProperty("function", "minecraft:set_nbt");
								cp.addProperty("tag", returnAddress);
								cpp.add(cp);

								bp.addProperty("type", "item");
								bp.addProperty("name", "sgextras:ancient_tablet");
								bp.addProperty("weight", 1);
								bp.add("functions", cpp);
								
								ba.add(bp);

								returnAddress = "";
							}

							ct.addProperty("condition", "random_chance");
							ct.addProperty("chance", 0.5);
							co.add(ct);
							
							jo.add("conditions", co);
							jo.addProperty("name", "sg_address");
							jo.addProperty("rolls", 1);
							jo.add("entries", ba);

							ja1.add(jo);
							
							JsonObject jbj = new JsonObject();
							jbj.add("pools", ja1);
							JsonArray json = new JsonArray();
							json.add(jbj);
							
							GsonBuilder builder = new GsonBuilder(); 
					        builder.setPrettyPrinting();
					    	Gson gson = builder.create();
							
					    	try 
						    {
					    		Writer writer = new FileWriter(lootFile,false);
					    		gson.toJson(jbj, writer); 
							    writer.flush();
							    writer.close();
						    } 
						    catch (IOException e) 
						    {
								e.printStackTrace();
						    }
						} 
						catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) 
						{
							e.printStackTrace();
						}
					}
					else
					{
						try 
						{
							JsonObject jsontree = (JsonObject)parser.parse(new FileReader(file));
							JsonObject je = jsontree.getAsJsonObject();
							JsonArray ja = je.getAsJsonArray("SGAddress");
							
							String returnAddress;
							
							JsonArray ja1 = new JsonArray();
							JsonObject jo = new JsonObject();
							JsonArray ba = new JsonArray();
							JsonArray co = new JsonArray();
							JsonObject ct = new JsonObject();
							
							for (JsonElement pa : ja) 
							{
								JsonObject addressObj = pa.getAsJsonObject();
							    String gateAddress = addressObj.get("Address").getAsString();
							    String dimID = addressObj.get("DimID").getAsString();
							    
							    JsonObject cp = new JsonObject();
							    JsonArray cpp = new JsonArray();
							    JsonObject bp = new JsonObject();

							    if(addressObj.has("CustomName"))
						    	{
						    		String name = addressObj.get("CustomName").getAsString();
						    		returnAddress = "{CustomName:"+'"'+name+'"'+",Address:"+'"'+gateAddress+'"'+",DimID:"+'"'+dimID+'"'+"}";
						    	}
								else
								{
									returnAddress = "{Address:"+'"'+gateAddress+'"'+",DimID:"+'"'+dimID+'"'+"}";
								}

								cp.addProperty("function", "minecraft:set_nbt");
								cp.addProperty("tag", returnAddress);
								cpp.add(cp);
								
								bp.addProperty("type", "item");
								bp.addProperty("name", "sgextras:ancient_tablet");
								bp.addProperty("weight", 1);
								bp.add("functions", cpp);
								
								ba.add(bp);

								returnAddress = "";
							}

							ct.addProperty("condition", "random_chance");
							ct.addProperty("chance", 0.5);
							co.add(ct);
							
							jo.add("conditions", co);
							jo.addProperty("name", "sg_address");
							jo.addProperty("rolls", 1);
							jo.add("entries", ba);

							ja1.add(jo);
							
							JsonObject jbj = new JsonObject();
							jbj.add("pools", ja1);
							JsonArray json = new JsonArray();
							json.add(jbj);
							
							GsonBuilder builder = new GsonBuilder(); 
					        builder.setPrettyPrinting();
					    	Gson gson = builder.create();
							
					    	try 
						    {
					    		Writer writer = new FileWriter(lootFile,false);
					    		gson.toJson(jbj, writer); 
							    writer.flush();
							    writer.close();
						    } 
						    catch (IOException e) 
						    {
								e.printStackTrace();
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
		return null;
	}*/
}
