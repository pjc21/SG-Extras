package pjc21.mod.objects.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import pjc21.mod.util.Reference;

public class AncientTabletGUI extends GuiScreen
{
	private static final ResourceLocation GUI_BASE = new ResourceLocation(Reference.MODID, Reference.GUI_BASE);
	NBTTagCompound nbtTagCompound;
	
	private final int textureHeight = 150;
    private final int textureWidth = 250;
    public int guiCentreX;
	public int guiCentreY;
	public String dimID;
	public String address;
    
    public AncientTabletGUI(NBTTagCompound nbtTagCompound)
    {
    	this.nbtTagCompound = nbtTagCompound;
    	this.dimID = null;
    	this.address = null;
    }

    public void onGuiClosed()
    {
    	this.dimID = null;
    	this.address = null;
    }
    
    @Override
    public void drawDefaultBackground()
    {
    	GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    	this.mc.getTextureManager().bindTexture(GUI_BASE);
    	int guiCentreX = (width - textureWidth ) / 2;
    	int guiCentreY = (height - textureHeight) / 2;
    	drawTexturedModalRect(guiCentreX, guiCentreY, 0, 0, textureWidth, textureHeight);
    }
    
    @Override
    public void drawScreen(int parWidth, int parHeight, float p_73863_3_)
    {
    	drawDefaultBackground();
    	
    	int guiCentreX = (width - textureWidth ) / 2;
    	int guiCentreY = (height - textureHeight) / 2;

    	if(this.nbtTagCompound != null)
        {
        	dimID = nbtTagCompound.getString("DimID");
 	        address = nbtTagCompound.getString("Address");

			int x = (guiCentreX + (textureWidth / 2)) - mc.fontRenderer.getStringWidth(address);
			int y = guiCentreY + (textureHeight / 2);

			if(this.nbtTagCompound.hasKey("CustomName"))
			{
				String name = nbtTagCompound.getString("CustomName");
				
				GL11.glPushMatrix();
				{
					GL11.glScalef(1.5f, 1.8f, 1.0f);
					mc.fontRenderer.drawString("Name: " + name, ((x - mc.fontRenderer.getStringWidth(name)) + mc.fontRenderer.getStringWidth(name) / 2) / 1.5f, (y - 30) / 1.8f, 0xbfbfbf, false);
		    		mc.fontRenderer.drawString("Address: " + address, ((x + 10) - mc.fontRenderer.getStringWidth(address)) / 1.5f, (y - 7) / 1.8f, 0xbfbfbf, false);
		    		mc.fontRenderer.drawString("DimID: " + dimID, (x - mc.fontRenderer.getStringWidth(dimID)) / 1.5f, (y + 17) / 1.8f, 0xbfbfbf, false);
				}
				GL11.glPopMatrix();
			}
			else
			{
				GL11.glPushMatrix();
				{
					GL11.glScalef(1.5f, 1.8f, 1.0f);
		    		mc.fontRenderer.drawString("Address: " + address, ((x + 4) - mc.fontRenderer.getStringWidth(address)) / 1.5f, (y - 7) / 1.8f, 0xbfbfbf, false);
		    		mc.fontRenderer.drawString("DimID: " + dimID, (x - mc.fontRenderer.getStringWidth(dimID)) / 1.5f, (y + 17) / 1.8f, 0xbfbfbf, false);
				}
				GL11.glPopMatrix();
			}
        }

        super.drawScreen(parWidth, parHeight, p_73863_3_);
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
    protected void keyTyped(char c, int keyCode) throws IOException 
    {
        super.keyTyped(c, keyCode);
        if (keyCode == Keyboard.KEY_E)
        {
        	this.mc.displayGuiScreen((GuiScreen)null);
        }
    }
}
