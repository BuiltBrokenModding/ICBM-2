package atomicscience.api;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Loader;

public class AtomicScience
{
	public static final String MAJOR_VERSION = "@MAJOR@";
	public static final String MINOR_VERSION = "@MINOR@";
	public static final String REVISION_VERSION = "@REVIS@";
	public static final String BUILD_VERSION = "@BUILD@";
	public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION;
	public static final String NAME = "Atomic Science";
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "AtomicScience.cfg"));

}
