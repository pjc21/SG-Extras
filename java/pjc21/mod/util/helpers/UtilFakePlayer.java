package pjc21.mod.util.helpers;

import java.lang.ref.WeakReference;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import pjc21.mod.util.Reference;

public class UtilFakePlayer 
{
	public static WeakReference<FakePlayer> initFakePlayer(WorldServer ws, UUID uname, String blockName) 
	{
	    GameProfile breakerProfile = new GameProfile(uname, Reference.MODID + ".fake_player." + blockName);
	    WeakReference<FakePlayer> fakePlayer;
	    
	    try 
	    {
	      fakePlayer = new WeakReference<FakePlayer>(FakePlayerFactory.get(ws, breakerProfile));
	    }
	    catch (Exception e) 
	    {
	      fakePlayer = null;
	    }

	    fakePlayer.get().onGround = true;
	    fakePlayer.get().connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(), 
	    		new NetworkManager(EnumPacketDirection.SERVERBOUND), fakePlayer.get()) 
	    {
	      @Override
	      public void sendPacket(Packet packetIn) {}
	    };
	    
	    fakePlayer.get().setSilent(true);
	    return fakePlayer;
	  }
}
