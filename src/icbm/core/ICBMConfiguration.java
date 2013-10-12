package icbm.core;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Loader;

/** Settings class for various configuration settings.
 * 
 * @author Calclavia */
public class ICBMConfiguration
{
    /** Configuration file for ICBM. */
    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "ICBM.cfg"));

    /** Should ICBM use external fuel? **/
    public static boolean USE_FUEL = true;
    public static boolean ZAI_KUAI = true;
    public static int DAO_DAN_ZUI_YUAN = 10000;
    public static int ANTIMATTER_SIZE = 55;
    public static boolean DESTROY_BEDROCK = true;

    public static void initiate()
    {
        CONFIGURATION.load();
        USE_FUEL = CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Use Fuel", ICBMConfiguration.USE_FUEL).getBoolean(ICBMConfiguration.USE_FUEL);
        ZAI_KUAI = ICBMConfiguration.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Allow Chunk Loading", ZAI_KUAI).getBoolean(ZAI_KUAI);
        DAO_DAN_ZUI_YUAN = ICBMConfiguration.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Max Missile Distance", ICBMConfiguration.DAO_DAN_ZUI_YUAN).getInt(ICBMConfiguration.DAO_DAN_ZUI_YUAN);
        ANTIMATTER_SIZE = ICBMConfiguration.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Antimatter Explosion Size", ANTIMATTER_SIZE).getInt(ANTIMATTER_SIZE);
        DESTROY_BEDROCK = ICBMConfiguration.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Antimatter Destroy Bedrock", DESTROY_BEDROCK).getBoolean(DESTROY_BEDROCK);
        CONFIGURATION.save();
    }

}
