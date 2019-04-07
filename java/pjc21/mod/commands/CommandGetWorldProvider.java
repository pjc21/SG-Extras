package pjc21.mod.commands;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandGetWorldProvider extends CommandBase
{
	@Override
	public String getName() 
	{
		return "sggetprovider";
	}

	@Override
	public String getUsage(ICommandSender sender) 
	{
		return "sggetprovider <id>";
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
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid use - sggetprovider <id>"));
			return;
        }
        else
        {
        	String id = args[0];
        	int id2 = Integer.parseInt(id);

        	String provider = server.getWorld(id2).provider.getClass().getName();
        	StringSelection stringSelection = new StringSelection(provider);
        	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	clipboard.setContents(stringSelection, null);
        	
        	sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "WorldProvider copied to clipboard"));
        }
	}

}
