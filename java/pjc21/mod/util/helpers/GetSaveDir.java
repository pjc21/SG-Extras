package pjc21.mod.util.helpers;

import java.io.File;

import javax.annotation.Nullable;

import net.minecraftforge.common.DimensionManager;
import pjc21.mod.Main;
import pjc21.mod.util.Reference;

public class GetSaveDir 
{
	@Nullable
    public File getDataDir(boolean createDirs)
    {
		File saveDir = DimensionManager.getCurrentSaveRootDirectory();

        if (saveDir != null)
        {
            saveDir = new File(saveDir, Reference.MODID);

            if (saveDir.exists() == false && (createDirs == false || saveDir.mkdirs() == false))
            {
                if (createDirs)
                {
                    Main.logger.warn("Failed to create a directory for storing SG Address file '{}'", saveDir.getPath());
                }

                return null;
            }

            return saveDir;
        }

        return null;
    }
}
