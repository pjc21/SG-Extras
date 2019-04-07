package pjc21.mod.commands;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;

public class CommandSetRandomSpawn extends CommandBase
{
	public final Random rand = new Random();
	public WorldServer worldserver;
	public World world;
	public WorldInfo worldinfo;

	public String type;
	public boolean liquid;
	
	private int seaLevel = 63;

	@Override
	public String getName() 
	{
		return "sgsetrandomspawn";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "sgsetrandomspawn <id> <type> <containsliquid>";
	}

	@Override
	public int getRequiredPermissionLevel()
    {
        return 2;
    }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		if (args.length < 3)
        {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid use - sgsetrandomspawn <dimId> <type>[OVERWORLD, NETHER] <containsliquid>(can spawn contain liquid [true, false])"));
			return;
        }
        else
        {
        	String id = args[0];
        	int id2 = Integer.parseInt(id);
        	this.world = DimensionManager.getWorld(id2);
        	
        	if(!world.isRemote)
        	{
        		this.worldserver = server.getWorld(id2);
        	}

    		BlockPos pos = setRandomSpawnLocation(id2, server, sender, args);
    		
    		int k = 0;
    		
    		while(pos == null)
    		{
    			pos = setRandomSpawnLocation(id2, server, sender, args);
    			++k;
    			if (k == 500)
	            {
    				System.out.println("Something went wrong, no new pos found!");
            		break;
	            }
    		}

    		sender.getEntityWorld().setSpawnPoint(pos);
    		server.getPlayerList().sendPacketToAllPlayers(new SPacketSpawnPosition(pos));
        }
	}
	
	public int getSeaLevel()
    {
        return this.seaLevel;
    }
	
	public BlockPos setRandomSpawnLocation(int id2, MinecraftServer server, ICommandSender sender, String[] args)
    {
		if(!world.isRemote)
		{
			BlockPos blockpos = null;
			
			Entity entity = sender.getCommandSenderEntity();
			
			int y = this.worldserver.getSpawnPoint().getY();
			
			if (y <= 0)
	        {
				y = this.getSeaLevel() + 1;
	        }

			int min = -10000;
	        int max = 10000;

	        int i = rand.nextInt((max - min) + 1) + min;
	        int j = rand.nextInt((max - min) + 1) + min;
	        int k = 0;

	        if(args[1] != null)
            {
            	type = args[1];
            }
            if(args[2] != null)
            {
            	liquid = Boolean.parseBoolean(args[2]);
            }	

            BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(i, 0, j));
            
            while (world.getBlockState(pos).getMaterial() == Material.AIR)
            {
            	
	            i += this.rand.nextInt(8) - this.rand.nextInt(8);
	            j += this.rand.nextInt(8) - this.rand.nextInt(8);
	            ++k;

	            switch (type)
	            {
		            case "OVERWORLD":
		            	default:
		            		blockpos = world.getTopSolidOrLiquidBlock(new BlockPos(i, y, j));
		            	break;
		            case "NETHER":
		            	int h;
	
		            	for (h = world.getActualHeight() - 20; h >= 0; --h)
	                    {
	                        if (this.world.isAirBlock(new BlockPos(i, h, j)))
	                        {
	                            while (h > 0 && this.world.isAirBlock(new BlockPos(i, h, j)))
	                            {
	                                --h;
	                            }
	                            
	                            y = h;
	                        	break;
	                        }
	                    }
		            	
		            	if(h != 0)
		            	{
		            		blockpos = new BlockPos(i, y, j);
		            	}
		            	
		            	break;
		            case "END":
		            	blockpos = world.getTopSolidOrLiquidBlock(new BlockPos(i, y, j));
		            	break;
	            }

	            AxisAlignedBB spawn_area =  new AxisAlignedBB(blockpos.getX() - 3, blockpos.getY(), blockpos.getZ() - 3, blockpos.getX() + 3, blockpos.getY() - 5, blockpos.getZ() + 3);

	            if(liquid)
	            {
	            	if(!world.isAirBlock(blockpos.down()) && blockpos.getY() != 0 && world.getBlockState(blockpos.down()).getMaterial() != Material.LEAVES 
	            			&& world.getBlockState(blockpos.down()).getMaterial() != Material.WOOD)
		            {
		            	break;
		            }
	            }
	            else
	            {
	            	if(!world.containsAnyLiquid(spawn_area) && world.getBlockState(blockpos.down()).getMaterial().isSolid() && !world.isAirBlock(blockpos.down()) 
	            			&& blockpos.getY() != 0 && world.getBlockState(blockpos.down()).getMaterial() != Material.LEAVES && world.getBlockState(blockpos.down()).getMaterial() != Material.WOOD)
		            {
		            	break;
		            }
	            }

            	if (k == 10000)
	            {
            		break;
	            }
	        }
			return blockpos;
		}
		return null;	
    }
}
