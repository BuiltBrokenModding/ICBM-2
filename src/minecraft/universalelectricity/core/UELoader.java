package universalelectricity.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Unload;
import universalelectricity.core.electricity.Electricity;
import universalelectricity.core.electricity.ElectricityConnections;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

/**
 * A class used to load Universal Electricity and make it work.
 * 
 * @author Calclavia
 * 
 */
public class UELoader
{
	public static final UELoader INSTANCE = new UELoader();

	public static boolean isInitialized = false;

	public void initiate()
	{
		if (!isInitialized)
		{
			Electricity.instance = new Electricity();
			MinecraftForge.EVENT_BUS.register(this);

			UniversalElectricity.isVoltageSensitive = UniversalElectricity.CONFIGURATION.get("Compatiblity", "Is Voltage Sensitive", UniversalElectricity.isVoltageSensitive).getBoolean(UniversalElectricity.isVoltageSensitive);

			UniversalElectricity.IC2_RATIO = UniversalElectricity.CONFIGURATION.get("Compatiblity", "IndustrialCraft Conversion Ratio", UniversalElectricity.IC2_RATIO).getDouble(UniversalElectricity.IC2_RATIO);
			UniversalElectricity.BC3_RATIO = UniversalElectricity.CONFIGURATION.get("Compatiblity", "BuildCraft Conversion Ratio", UniversalElectricity.BC3_RATIO).getDouble(UniversalElectricity.BC3_RATIO);
			UniversalElectricity.TO_IC2_RATIO = 1 / UniversalElectricity.IC2_RATIO;
			UniversalElectricity.TO_BC_RATIO = 1 / UniversalElectricity.BC3_RATIO;

			if (UniversalElectricity.BC3_RATIO <= 0 || !Loader.isModLoaded("BuildCraft|Core"))
			{
				System.out.println("Disabled Buildcraft electricity conversion!");
			}
			else
			{
				System.out.println("Buildcraft conversion ratio: " + UniversalElectricity.BC3_RATIO);
			}

			if (UniversalElectricity.IC2_RATIO <= 0 || !Loader.isModLoaded("IC2"))
			{
				System.out.println("Disabled Industrialcraft electricity conversion!");
			}
			else
			{
				System.out.println("IC2 conversion ratio: " + UniversalElectricity.IC2_RATIO);
			}

			FMLLog.finest("Universal Electricity v" + UniversalElectricity.VERSION + " successfully loaded!");

			isInitialized = true;
		}
	}

	@ForgeSubscribe
	public void onWorldUnload(Unload event)
	{
		Electricity.instance = new Electricity();
		ElectricityConnections.clearAll();
	}
}
