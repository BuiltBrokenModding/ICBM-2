package atomicscience.api;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Loader;

public class AtomicScience
{
	public static final String VERSION = "0.5.0";
	public static final String NAME = "Atomic Science";
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/AtomicScience.cfg"));

}
