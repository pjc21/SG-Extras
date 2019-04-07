package pjc21.mod.util.helpers;

import java.io.File;

import javax.annotation.Nullable;

import net.minecraftforge.common.DimensionManager;
import pjc21.mod.Main;
import pjc21.mod.util.Reference;

public class GetLootDir 
{
	@Nullable
    public File getDataDir(boolean createDirs)
    {
		File defaultDir =  DimensionManager.getCurrentSaveRootDirectory();

		if(defaultDir != null)
		{
			String sd = defaultDir.toString();
			String PATH = "\\data\\loot_tables\\";
		    String directoryName = sd.concat(PATH);

			File lootDir = new File(directoryName, Reference.MODID);

			if (lootDir.exists() == false && (createDirs == false || lootDir.mkdirs() == false))
		    {
				System.out.println("LootDir doesnot exist");
		        Main.logger.warn("Failed to create a directory for storing SG Address file '{}'", lootDir.getPath());

		        return null;
		    }
			
			return lootDir;
		}
		
		System.out.println("Save Dir Null");
		
		return null;
    }
}
