package pjc21.mod.util.helpers;

import net.minecraft.util.math.BlockPos;

public class SGAddress 
{
	private String name;
	private String address;
	private int id;
	private boolean allowLoot;
	private boolean allowProtection;
	private BlockPos pos;
	private int orientation;
	private int facing;
	
	public SGAddress() {};
	
	public SGAddress(String name, String address, int id, boolean allowLoot, boolean allowProtection, BlockPos pos, int orientation, int facing)
	{
		this.name = name;
		this.address = address;
		this.id = id;
		this.allowLoot = allowLoot;
		this.allowProtection = allowProtection;
		this.pos = pos;
		this.orientation = orientation;
		this.facing = facing;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getAddress() 
	{
		return address;
	}

	public void setAddress(String address) 
	{
		this.address = address;
	}
	
	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}
	
	public boolean getAllowLoot() 
	{
		return allowLoot;
	}

	public void setAllowLoot(boolean allowLoot) 
	{
		this.allowLoot = allowLoot;
	}
	
	public boolean getAllowProtection() 
	{
		return allowProtection;
	}

	public void setAllowProtection(boolean allowProtection) 
	{
		this.allowProtection = allowProtection;
	}
	
	public BlockPos getPos() 
	{
		return pos;
	}

	public void setPos(BlockPos pos) 
	{
		this.pos = pos;
	}
	
	public int getOrientation() 
	{
		return orientation;
	}

	public void setOrientation(int orientation) 
	{
		this.orientation = orientation;
	}
	
	public int getFacing() 
	{
		return facing;
	}

	public void setFacing(int facing) 
	{
		this.facing = facing;
	}
}
