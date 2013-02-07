package icbm.api;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Loader;

public class ICBM
{
	/**
	 * Name of the channel and mod ID.
	 */
	public static final String NAME = "ICBM";

	/**
	 * The version of ICBM.
	 */
	public static final String VERSION = "1.0.4";

	/**
	 * Configuration file for ICBM.
	 */
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/ICBM.cfg"));

	/**
	 * Some texture file directory references.
	 */
	public static final String TEXTURE_FILE_PATH = "/icbm/textures/";
}
