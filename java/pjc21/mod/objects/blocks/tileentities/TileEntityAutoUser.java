package pjc21.mod.objects.blocks.tileentities;

import java.lang.ref.WeakReference;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import pjc21.mod.objects.blocks.BlockActivator;
import pjc21.mod.util.helpers.UtilFakePlayer;

public class TileEntityAutoUser extends TileEntity implements ITickable
{
	private String customName;

	private WeakReference<FakePlayer> fakePlayer;
	private UUID uuid;
	
	private void verifyUuid(World world) 
	{
	    if (uuid == null) 
	    {
	      uuid = UUID.randomUUID();
	      IBlockState state = world.getBlockState(this.pos);
	      world.notifyBlockUpdate(pos, state, state, 3);
	    }
	  }
	
	public void update() 
	{	
		if (world instanceof WorldServer) 
		{
		      verifyUuid(world);
		      if (fakePlayer == null)
		      {
		        fakePlayer = UtilFakePlayer.initFakePlayer((WorldServer) world, this.uuid, "block_activator");
		      }
		      
		      EnumFacing blockFacing = this.world.getBlockState(this.pos).getValue(BlockActivator.FACING);
		      BlockPos pos = getPos().offset(blockFacing);
		      
		      EnumActionResult r = fakePlayer.get().interactionManager.processRightClickBlock(fakePlayer.get(), world, fakePlayer.get().getHeldItemMainhand(), EnumHand.MAIN_HAND, pos, EnumFacing.UP, .5F, .5F, .5F);
		}
	}
	
	public static PlayerInteractEvent.RightClickBlock onRightClickBlock(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing side, Vec3d vec3d) 
	{
		return ForgeHooks.onRightClickBlock(player, hand, pos, side, vec3d);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{
		super.writeToNBT(compound);
		if(this.hasCustomName()) compound.setString("CustomName", this.customName);

		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if(compound.hasKey("CustomName", 8)) this.setCustomName(compound.getString("CustomName"));

	}

	@Override
	public NBTTagCompound getUpdateTag() 
	{
		return writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) 
	{
		super.handleUpdateTag(tag);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) 
	{
		return oldState != newState;
	}
	
	public boolean hasCustomName() 
	{
		return this.customName != null && !this.customName.isEmpty();
	}
	
	public void setCustomName(String customName) 
	{
		this.customName = customName;
	}
	
	@Override
	public ITextComponent getDisplayName() 
	{
		return new TextComponentTranslation("container.block_activator");
	}
}
