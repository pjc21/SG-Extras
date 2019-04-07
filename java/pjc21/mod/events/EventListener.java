package pjc21.mod.events;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pjc21.mod.objects.loottables.SGLoot;
import pjc21.mod.util.Config;
import pjc21.mod.util.helpers.GetSaveDir;
import pjc21.mod.util.helpers.SGAddress;

public class EventListener 
{
	private static final Map<Integer, List<SGAddress>> GATE_BLOCKS = new HashMap<>();
	private static final List<BlockPos> GATE_POSITIONS = new ArrayList();
	
	@SubscribeEvent
	public void onBlockBreakEvent(BreakEvent event)
	{
		if(!Config.allowBreakGate)
		{
			if(event.getWorld().isRemote) {return;}

			int dimID = event.getPlayer().world.provider.getDimension();
			World world = event.getWorld();
			
			if(event.getState().getBlock() == Block.getBlockFromName("sgcraft:stargatering") || event.getState().getBlock() == Block.getBlockFromName("sgcraft:stargatebase"))
			{
				readGateInfo(dimID);
				
				if(!GATE_POSITIONS.isEmpty())
				{
					for(BlockPos pos : GATE_POSITIONS)
					{
						if(event.getPos().equals(pos))
						{
							if (event.getPlayer().canUseCommand(2, ""))
		        			{
		        				//System.out.println("Player is allowed to edit this gate. Doing nothing.");
		        			}
		        			else
		        			{
		        				//System.out.println("Player is not allowed to edit this gate. Cancelling.");
		        				event.setCanceled(true);
		        			}
						}
					}
				}
			}
			
			if(event.getState().getBlock() == Block.getBlockFromName("sgcraft:stargatecontroller"))
			{
				readGateInfo(dimID);
		        
				TileEntity tileentity = world.getTileEntity(event.getPos());

				if(!GATE_BLOCKS.isEmpty())
				{
					for(SGAddress sgadr : GATE_BLOCKS.get(dimID))
					{
						BlockPos pos = sgadr.getPos();
						
		                if (tileentity == null)
		                {
		                    //throw new CommandException("commands.blockdata.notValid", new Object[0]);
		                }
		                else
		                {
		                    NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());

		                    if(nbttagcompound.getBoolean("isLinkedToStargate"))
	                    	{
	                    		BlockPos linkedPos = new BlockPos(nbttagcompound.getInteger("linkedX"), nbttagcompound.getInteger("linkedY"), nbttagcompound.getInteger("linkedZ"));
	                    		
	                    		if(pos.equals(linkedPos))
	                    		{
	                    			if (event.getPlayer().canUseCommand(2, ""))
	        	        			{
	        	        				//System.out.println("Player is allowed to edit this controller. Doing nothing.");
	        	        			}
	        	        			else
	        	        			{
	        	        				//System.out.println("Player is not allowed to edit this controller. Cancelling.");
	        	        				event.setCanceled(true);
	        	        			}
	                    		}
	                    	}
		                }
					}
				}
			}
			
			if(event.getState().getBlock() != Block.getBlockFromName("sgcraft:stargatering") && event.getState().getBlock() != Block.getBlockFromName("sgcraft:stargatebase") && event.getState().getBlock() != Block.getBlockFromName("sgcraft:stargatecontroller"))
			{
				readGateInfo(dimID);
				
				if(!GATE_POSITIONS.isEmpty())
				{
					for(BlockPos poss : GATE_POSITIONS)
					{
						if(event.getPos().equals(poss))
						{
							if (event.getPlayer().canUseCommand(2, ""))
		        			{
		        				//System.out.println("Player is allowed to edit this blockpos. Doing nothing.");
		        			}
		        			else
		        			{
		        				//System.out.println("Player is not allowed to edit this blockpos. Cancelling.");
		        				event.setCanceled(true);
		        			}
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void handleBlockRightClick(PlayerInteractEvent.RightClickBlock event)
	{
		TileEntity entity = event.getEntityPlayer().getEntityWorld().getTileEntity(event.getPos());
		Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
		int dimID = event.getEntityPlayer().getEntityWorld().provider.getDimension();

		if(!Config.allowAccessBelowGate)
		{
			if(block != Block.getBlockFromName("sgcraft:stargatering") && block != Block.getBlockFromName("sgcraft:stargatebase") && block != Block.getBlockFromName("sgcraft:stargatecontroller"))
			{
				readGateInfo(dimID);

				if(!GATE_POSITIONS.isEmpty())
				{
					for(BlockPos poss : GATE_POSITIONS)
					{
						if(event.getPos().equals(poss))
						{
							if (event.getEntityPlayer().canUseCommand(2, ""))
		        			{
		        				//System.out.println("Player is allowed to interact with this block. Doing nothing.");
		        				event.setUseBlock(Result.ALLOW);
		        			}
		        			else
		        			{
		        				//System.out.println("Player is not allowed to interact with this block. Cancelling.");
		        				event.getEntityPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "Permission Denied!"));
		        				event.setUseBlock(Result.DENY);
		        			}
						}
					}
				}
			}
		}
		
		if(!Config.allowControllerAccess)
		{
			if(block == Block.getBlockFromName("sgcraft:stargatecontroller"))
			{
				int i = (int) Math.round(event.getHitVec().y);

				readGateInfo(dimID);
				
				if(!GATE_BLOCKS.isEmpty())
				{
					if(event.getPos().getY() == i)
					{
						for(SGAddress sgadr : GATE_BLOCKS.get(dimID))
						{
							BlockPos pos = sgadr.getPos();
							System.out.println("Gate Pos: " + pos);
			                if (entity == null)
			                {
			                    //throw new CommandException("commands.blockdata.notValid", new Object[0]);
			                }
			                else
			                {
			                    NBTTagCompound nbttagcompound = entity.writeToNBT(new NBTTagCompound());

			                    if(nbttagcompound.getBoolean("isLinkedToStargate"))
			                	{
			                		BlockPos linkedPos = new BlockPos(nbttagcompound.getInteger("linkedX"), nbttagcompound.getInteger("linkedY"), nbttagcompound.getInteger("linkedZ"));

			                		if(pos.equals(linkedPos))
			                		{
			                			if (event.getEntityPlayer().canUseCommand(2, ""))
			    	        			{
			    	        				//System.out.println("Player is allowed to interact with controller fuel panel. Doing nothing.");
			    	        				event.setUseBlock(Result.ALLOW);
			    	        			}
			    	        			else
			    	        			{
			    	        				//System.out.println("Player is not allowed to interact with controller fuel panel. Cancelling.");
			    	        				event.getEntityPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "Permission Denied!"));
			    	        				event.setUseBlock(Result.DENY);
			    	        			}
			                		}
			                	}
			                }
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockExplodeEvent(ExplosionEvent.Detonate event)
	{
		if(!Config.allowExplodeGate)
		{
			Iterator<BlockPos> iterator = event.getAffectedBlocks().iterator();
			int dimID = event.getWorld().provider.getDimension();
			World world = event.getWorld();

			readGateInfo(dimID);

			while (iterator.hasNext())
			{
				BlockPos pos = iterator.next();

				if (event.getWorld().getBlockState(pos).getBlock() == Block.getBlockFromName("sgcraft:stargatering") || event.getWorld().getBlockState(pos).getBlock() == Block.getBlockFromName("sgcraft:stargatebase"))
				{
					if(!GATE_POSITIONS.isEmpty())
					{
						for(BlockPos poss : GATE_POSITIONS)
						{
							if(poss.equals(pos))
							{
								iterator.remove();
							}
						}
					}
				}
				
				if (event.getWorld().getBlockState(pos).getBlock() == Block.getBlockFromName("sgcraft:stargatecontroller"))
				{
					TileEntity tileentity = world.getTileEntity(pos);

					if(!GATE_BLOCKS.isEmpty())
					{
						for(SGAddress sgadr : GATE_BLOCKS.get(dimID))
						{
							BlockPos pos1 = sgadr.getPos();
							
			                if (tileentity == null)
			                {
			                    //throw new CommandException("commands.blockdata.notValid", new Object[0]);
			                }
			                else
			                {
			                    NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());

			                    if(nbttagcompound.getBoolean("isLinkedToStargate"))
		                    	{
		                    		BlockPos linkedPos = new BlockPos(nbttagcompound.getInteger("linkedX"), nbttagcompound.getInteger("linkedY"), nbttagcompound.getInteger("linkedZ"));

		                    		if(pos1.equals(linkedPos))
		                    		{
		                    			iterator.remove();
		                    		}
		                    	}
			                }
						}
					}
				}

				if(event.getWorld().getBlockState(pos).getBlock() != Block.getBlockFromName("sgcraft:stargatering") && event.getWorld().getBlockState(pos).getBlock() != Block.getBlockFromName("sgcraft:stargatebase") && event.getWorld().getBlockState(pos).getBlock() != Block.getBlockFromName("sgcraft:stargatecontroller"))
				{
					if(!GATE_POSITIONS.isEmpty())
					{
						for(BlockPos poss : GATE_POSITIONS)
						{
							if(poss.equals(pos))
							{
								iterator.remove();
							}
						}
					}
				}
			}
		}
	}
	
	public void readGateInfo(int dimid) 
	{
		GetSaveDir saveDirectory = new GetSaveDir();
		File saveDir = saveDirectory.getDataDir(true);
		SGAddress ga = new SGAddress();
		
		if (saveDir != null)
		{
			File file = new File(saveDir, "SGGateAddress.json");

			if(file.exists() && !file.isDirectory()) 
			{ 
				JsonParser parser = new JsonParser();
				GATE_BLOCKS.clear();
				List<SGAddress> gateBlocks = new ArrayList<>();
				try 
				{
					JsonObject jsontree = (JsonObject)parser.parse(new FileReader(file));
					JsonObject je = jsontree.getAsJsonObject();
					JsonArray ja = je.getAsJsonArray("SGAddress");

					int orientation = -1;
					int facing = -1;
					
					for (JsonElement pa : ja)
					{
						JsonObject addressObj = pa.getAsJsonObject();
						
						int dim = addressObj.get("DimID").getAsInt();
						boolean allowProtection = addressObj.get("AllowProtection").getAsBoolean();
						orientation = addressObj.get("Orientation").getAsInt();
						facing = addressObj.get("Facing").getAsInt();
						
						if(dim == dimid && allowProtection)
						{
							JsonArray bp = addressObj.getAsJsonArray("BlockPos");
							String x = "";
							String y = "";
							String z = "";
							
							for (JsonElement blpos : bp)
							{
								JsonObject cpp = blpos.getAsJsonObject();
								x = cpp.get("X").getAsString();
								y = cpp.get("Y").getAsString();
								z = cpp.get("Z").getAsString();
							}
							
							int x1 = Integer.parseInt(x);
							int y1 = Integer.parseInt(y);
							int z1 = Integer.parseInt(z);
							
							if (gateBlocks == null)
					        {
								GATE_BLOCKS.put(dim, gateBlocks);
					        }

							SGAddress sg = new SGAddress();
							sg.setPos(new BlockPos(x1, y1, z1));
							sg.setOrientation(orientation);
							sg.setFacing(facing);
							
							gateBlocks.add(sg);
							
							GATE_BLOCKS.put(dim, gateBlocks);
						}
					}

					getGatePostions(dimid);
				} 
				catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getGatePostions(int dimID)
	{
		if(GATE_BLOCKS.containsKey(dimID))
        {
			GATE_POSITIONS.clear();

			for(SGAddress sgadr : GATE_BLOCKS.get(dimID))
			{
			    BlockPos pos = sgadr.getPos();
			    int gateFacing = sgadr.getFacing();
			    
		    	if(sgadr.getOrientation() == 1) //Vertical Gate
		    	{
		    		if(gateFacing == 0 || gateFacing == 2) //North or South
		    		{
		    			Iterable<BlockPos> gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX() - 2, pos.getY(), pos.getZ()), new BlockPos(pos.getX() + 2, pos.getY(), pos.getZ()));
			    		for (BlockPos pos1 : gate_bottom)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> gate_left = BlockPos.getAllInBox(new BlockPos(pos.getX() - 2, pos.getY() + 1, pos.getZ()), new BlockPos(pos.getX() - 2, pos.getY() + 3, pos.getZ()));
			    		for (BlockPos pos1 : gate_left)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> gate_right = BlockPos.getAllInBox(new BlockPos(pos.getX() + 2, pos.getY() + 1, pos.getZ()), new BlockPos(pos.getX() + 2, pos.getY() + 3, pos.getZ()));
			    		for (BlockPos pos1 : gate_right)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> gate_top = BlockPos.getAllInBox(new BlockPos(pos.getX() - 2, pos.getY() + 4, pos.getZ()), new BlockPos(pos.getX() + 2, pos.getY() + 4, pos.getZ()));
			    		for (BlockPos pos1 : gate_top)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		if(Config.allowBelowGate)
			    		{
			    			Iterable<BlockPos> below_gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX() - 2, pos.getY() - 1, pos.getZ()), new BlockPos(pos.getX() + 2, pos.getY() - 1, pos.getZ()));
				    		for (BlockPos pos1 : below_gate_bottom)
				    		{
				    			GATE_POSITIONS.add(pos1);
				            }
			    		}
		    		}
		    		
		    		if(gateFacing == 1 || gateFacing == 3) //West or East
		    		{
		    			Iterable<BlockPos> gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 2), new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 2));
			    		for (BlockPos pos1 : gate_bottom)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> gate_left = BlockPos.getAllInBox(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ() - 2), new BlockPos(pos.getX(), pos.getY() + 3, pos.getZ() - 2));
			    		for (BlockPos pos1 : gate_left)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> gate_right = BlockPos.getAllInBox(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ() + 2), new BlockPos(pos.getX(), pos.getY() + 3, pos.getZ() + 2));
			    		for (BlockPos pos1 : gate_right)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> gate_top = BlockPos.getAllInBox(new BlockPos(pos.getX(), pos.getY() + 4, pos.getZ() - 2), new BlockPos(pos.getX(), pos.getY() + 4, pos.getZ() + 2));
			    		for (BlockPos pos1 : gate_top)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		if(Config.allowBelowGate)
			    		{
			    			Iterable<BlockPos> below_gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ() - 2), new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ() + 2));
				    		for (BlockPos pos1 : below_gate_bottom)
				    		{
				    			GATE_POSITIONS.add(pos1);
				            }
			    		}
		    		}
		    	}
		    	
		    	if(sgadr.getOrientation() == 2) //Horizontal Gate)
		    	{
		    		switch (gateFacing)
		    		{
		    		case 0: //North
		    			Iterable<BlockPos> north_gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX() - 2, pos.getY(), pos.getZ()), new BlockPos(pos.getX() + 2, pos.getY(), pos.getZ()));
			    		for (BlockPos pos1 : north_gate_bottom)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> north_gate_left = BlockPos.getAllInBox(new BlockPos(pos.getX() - 2, pos.getY(), pos.getZ() - 1), new BlockPos(pos.getX() - 2, pos.getY(), pos.getZ() - 3));
			    		for (BlockPos pos1 : north_gate_left)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> north_gate_right = BlockPos.getAllInBox(new BlockPos(pos.getX() + 2, pos.getY(), pos.getZ() - 1), new BlockPos(pos.getX() + 2, pos.getY(), pos.getZ() - 3));
			    		for (BlockPos pos1 : north_gate_right)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> north_gate_top = BlockPos.getAllInBox(new BlockPos(pos.getX() - 2, pos.getY(), pos.getZ() - 4), new BlockPos(pos.getX() + 2, pos.getY(), pos.getZ() - 4));
			    		for (BlockPos pos1 : north_gate_top)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		if(Config.allowBelowGate)
			    		{
			    			Iterable<BlockPos> below_north_gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX() - 2, pos.getY()-1, pos.getZ()), new BlockPos(pos.getX() + 2, pos.getY()-1, pos.getZ()));
				    		for (BlockPos pos1 : below_north_gate_bottom)
				    		{
				    			GATE_POSITIONS.add(pos1);
				            }
			    		}
		    			break;
		    		case 2: //South
		    			Iterable<BlockPos> south_gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX() + 2, pos.getY(), pos.getZ()), new BlockPos(pos.getX() - 2, pos.getY(), pos.getZ()));
			    		for (BlockPos pos1 : south_gate_bottom)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> south_gate_left = BlockPos.getAllInBox(new BlockPos(pos.getX() + 2, pos.getY(), pos.getZ() + 1), new BlockPos(pos.getX() + 2, pos.getY(), pos.getZ() + 3));
			    		for (BlockPos pos1 : south_gate_left)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> south_gate_right = BlockPos.getAllInBox(new BlockPos(pos.getX() - 2, pos.getY(), pos.getZ() + 1), new BlockPos(pos.getX() - 2, pos.getY(), pos.getZ() + 3));
			    		for (BlockPos pos1 : south_gate_right)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> south_gate_top = BlockPos.getAllInBox(new BlockPos(pos.getX() + 2, pos.getY(), pos.getZ() + 4), new BlockPos(pos.getX() - 2, pos.getY(), pos.getZ() + 4));
			    		for (BlockPos pos1 : south_gate_top)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		if(Config.allowBelowGate)
			    		{
			    			Iterable<BlockPos> below_south_gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX() + 2, pos.getY()-1, pos.getZ()), new BlockPos(pos.getX() - 2, pos.getY()-1, pos.getZ()));
				    		for (BlockPos pos1 : below_south_gate_bottom)
				    		{
				    			GATE_POSITIONS.add(pos1);
				            }
			    		}
		    			break;
		    		case 3: //East
		    			Iterable<BlockPos> east_gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 2), new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 2));
			    		for (BlockPos pos1 : east_gate_bottom)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> east_gate_left = BlockPos.getAllInBox(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ() - 2), new BlockPos(pos.getX() + 3, pos.getY(), pos.getZ() - 2));
			    		for (BlockPos pos1 : east_gate_left)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> east_gate_right = BlockPos.getAllInBox(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ() + 2), new BlockPos(pos.getX() + 3, pos.getY(), pos.getZ() + 2));
			    		for (BlockPos pos1 : east_gate_right)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos> east_gate_top = BlockPos.getAllInBox(new BlockPos(pos.getX() + 4, pos.getY(), pos.getZ() - 2), new BlockPos(pos.getX() + 4, pos.getY(), pos.getZ() + 2));
			    		for (BlockPos pos1 : east_gate_top)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		if(Config.allowBelowGate)
			    		{
			    			Iterable<BlockPos> below_east_gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX(), pos.getY()-1, pos.getZ() - 2), new BlockPos(pos.getX(), pos.getY()-1, pos.getZ() + 2));
				    		for (BlockPos pos1 : below_east_gate_bottom)
				    		{
				    			GATE_POSITIONS.add(pos1);
				            }
			    		}
		    			break;
		    		case 1: //West
		    			Iterable<BlockPos> west_gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 2), new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 2));
			    		for (BlockPos pos1 : west_gate_bottom)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos>  west_gate_left = BlockPos.getAllInBox(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ() - 2), new BlockPos(pos.getX() - 3, pos.getY(), pos.getZ() - 2));
			    		for (BlockPos pos1 : west_gate_left)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos>  west_gate_right = BlockPos.getAllInBox(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ() + 2), new BlockPos(pos.getX() - 3, pos.getY(), pos.getZ() + 2));
			    		for (BlockPos pos1 : west_gate_right)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		Iterable<BlockPos>  west_gate_top = BlockPos.getAllInBox(new BlockPos(pos.getX() - 4, pos.getY(), pos.getZ() - 2), new BlockPos(pos.getX(), pos.getY() - 4, pos.getZ() + 2));
			    		for (BlockPos pos1 : west_gate_top)
			    		{
			    			GATE_POSITIONS.add(pos1);
			            }
			    		if(Config.allowBelowGate)
			    		{
			    			Iterable<BlockPos> below_west_gate_bottom = BlockPos.getAllInBox(new BlockPos(pos.getX(), pos.getY()-1, pos.getZ() - 2), new BlockPos(pos.getX(), pos.getY()-1, pos.getZ() + 2));
				    		for (BlockPos pos1 : below_west_gate_bottom)
				    		{
				    			GATE_POSITIONS.add(pos1);
				            }
			    		}
		    			break;
		    		}
		    	}
			}
        }
	}

	@SubscribeEvent
	public void Table_Additives(LootTableLoadEvent event)
	{
		if(Config.allowLoot)
		{
			String name = event.getName().toString();
			LootTable loot = new SGLoot().createTable();
			
			for(String s : Config.allowedTables)
			{
				if(name.matches(s))
				{
					if(loot != null)
					{
						event.getTable().addPool(loot.getPool("sg_address"));
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
    void onToolTip(ItemTooltipEvent event)
    {
        List<String> toolTip = event.getToolTip();
        ItemStack stack = event.getItemStack();
        
        if(stack.hasTagCompound()) 
		{
			if(stack.getTagCompound().hasKey("CustomName")) 
			{
				NBTTagCompound tag  = stack.getTagCompound();
				String name = tag.getString("CustomName");
				addTooltips(toolTip, stack, name);
			}
		}
    }

	private void addTooltips(List<String> toolTip, ItemStack stack, String name)
	{
		toolTip.add(TextFormatting.BLUE + name);
	}

}
