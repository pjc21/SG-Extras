package pjc21.mod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class CommandLoadDim extends CommandBase
{
	@Override
	public String getName()
	{
		return "sgloaddim";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "sgloaddim <id> <x> <y> <z>";
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
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid use - sgloaddim <id>"));
			return;
        }
        else
        {
        	String id = args[0];
        	int id2 = Integer.parseInt(id);
        	DimensionType type = DimensionManager.getProviderType(id2);

        	boolean loadSpawn = type.shouldLoadSpawn();

    		if(loadSpawn)
    		{
    			DimensionManager.initDimension(id2);
    		}
    		else
    		{
    			WorldServer world = server.getWorld(id2);
    		}
        }
	}
}