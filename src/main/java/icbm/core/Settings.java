package icbm.core;

import calclavia.lib.content.IDManager;
import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.Configuration;

import java.io.File;

/** Settings class for various configuration settings.
 * 
 * @author Calclavia */
public class Settings
{
    /** Auto-incrementing configuration IDs. Use this to make sure no config ID is the same. */
    public static final IDManager idManager = new IDManager(3880, 19220);

    public static int getNextBlockID()
    {
        return idManager.getNextBlockID();
    }

    public static int getNextItemID()
    {
        return idManager.getNextItemID();
    }

    /** Configuration file for ICBM. */
    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "ICBM.cfg"));

    /** Should ICBM use external fuel? **/
    public static boolean USE_FUEL = true;
    public static boolean ZAI_KUAI = true;
    public static int DAO_DAN_ZUI_YUAN = 10000;
    public static int ANTIMATTER_SIZE = 55;
    public static boolean DESTROY_BEDROCK = true;
    public static int MAX_ROCKET_LAUCNHER_TIER = 2;

    public static void initiate()
    {
        CONFIGURATION.load();
        USE_FUEL = CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Use Fuel", Settings.USE_FUEL).getBoolean(Settings.USE_FUEL);
        ZAI_KUAI = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Allow Chunk Loading", ZAI_KUAI).getBoolean(ZAI_KUAI);
        DAO_DAN_ZUI_YUAN = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Max Missile Distance", Settings.DAO_DAN_ZUI_YUAN).getInt(Settings.DAO_DAN_ZUI_YUAN);
        ANTIMATTER_SIZE = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Antimatter Explosion Size", ANTIMATTER_SIZE).getInt(ANTIMATTER_SIZE);
        DESTROY_BEDROCK = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Antimatter Destroy Bedrock", DESTROY_BEDROCK).getBoolean(DESTROY_BEDROCK);
        MAX_ROCKET_LAUCNHER_TIER = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Limits the max missile tier for rocket launcher item", MAX_ROCKET_LAUCNHER_TIER).getInt(MAX_ROCKET_LAUCNHER_TIER);
        CONFIGURATION.save();
    }

}
