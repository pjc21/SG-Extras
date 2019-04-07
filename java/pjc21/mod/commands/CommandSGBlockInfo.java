package pjc21.mod.commands;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class CommandSGBlockInfo extends CommandBase
{

	@Override
	public String getName() 
	{
		return "sggetdata";
	}

	@Override
	public String getUsage(ICommandSender sender) 
	{
		return "sggetdata <x> <y> <z> {}";
	}
	
	@Override
	public int getRequiredPermissionLevel()
    {
        return 2;
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		if (args.length < 4)
        {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid use - sggetdata <x> <y> <z> {}"));
			return;
        }
		else
        {
			sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
            BlockPos blockpos = parseBlockPos(sender, args, 0, false);
            World world = sender.getEntityWorld();

            if (!world.isBlockLoaded(blockpos))
            {
            	sender.sendMessage(new TextComponentString(TextFormatting.RED + "BlockPos not loaded"));
            }
            else
            {
                IBlockState iblockstate = world.getBlockState(blockpos);
                TileEntity tileentity = world.getTileEntity(blockpos);

                if (tileentity == null)
                {
                    throw new CommandException("commands.blockdata.notValid", new Object[0]);
                }
                else
                {
                    NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());
                    NBTTagCompound nbttagcompound1 = nbttagcompound.copy();
                    NBTTagCompound nbttagcompound2;

                    try
                    {
                        nbttagcompound2 = JsonToNBT.getTagFromJson(buildString(args, 3));
                    }
                    catch (NBTException nbtexception)
                    {
                        throw new CommandException("commands.blockdata.tagError", new Object[] {nbtexception.getMessage()});
                    }

                    nbttagcompound.merge(nbttagcompound2);
                    nbttagcompound.setInteger("x", blockpos.getX());
                    nbttagcompound.setInteger("y", blockpos.getY());
                    nbttagcompound.setInteger("z", blockpos.getZ());

                    if (nbttagcompound.equals(nbttagcompound1))
                    {
                    	String tag = nbttagcompound.toString();
                    	StringSelection stringSelection = new StringSelection(tag);
                    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    	clipboard.setContents(stringSelection, null);
                    	
                    	sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
                    	sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "NBT Info copied to clipboard"));
                    }
                }
            }
        }
	}
}
