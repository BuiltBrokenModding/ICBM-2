package icbm;

/** @author Calclavia */
public class Reference
{

    /** Name of the channel and mod ID. */
    public static final String NAME = "ICBM";
    /** The version of ICBM. */
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION;

    public static final String CHANNEL = NAME;
    public static final String DOMAIN = "icbm";
    public static final String PREFIX = DOMAIN + ":";
    public static final String ASSETS_PATH = "/assets/icbm/";
    public static final String TEXTURE_PATH = "textures/";
    public static final String GUI_PATH = TEXTURE_PATH + "gui/";
    public static final String MODEL_PREFIX = "models/";
    public static final String MODEL_DIRECTORY = ASSETS_PATH + MODEL_PREFIX;

    public static final String MODEL_TEXTURE_PATH = TEXTURE_PATH + MODEL_PREFIX;
    public static final String BLOCK_PATH = TEXTURE_PATH + "blocks/";
    public static final String ITEM_PATH = TEXTURE_PATH + "items/";
    public static final String LANGUAGE_PATH = ASSETS_PATH + "languages/";
    public static final String[] LANGUAGES = new String[] { "en_US", "zh_CN", "es_ES", "de_DE", "ru_RU"};

}
