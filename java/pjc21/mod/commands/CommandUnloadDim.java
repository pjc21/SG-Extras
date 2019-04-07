package pjc21.mod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class CommandUnloadDim extends CommandBase
{

	@Override
	public String getName() 
	{
		return "sgunloaddim";
	}

	@Override
	public String getUsage(ICommandSender sender) 
	{
		return "sgunloaddim <id>";
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
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid use - sgunloaddim <id>"));
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
        		type.setLoadSpawn(false);
        	}
        	
			DimensionManager.unloadWorld(id2);
        }
	}
}