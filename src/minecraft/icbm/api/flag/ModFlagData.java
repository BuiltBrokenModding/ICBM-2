package icbm.api.flag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import universalelectricity.core.vector.Vector3;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ModFlagData extends FlagBase
{
	/**
	 * An array of world flag data. Each representing a world.
	 */
	private final List<FlagWorld> flagWorlds = new ArrayList<FlagWorld>();

	/**
	 * Initiates a new mod flag data and loads everything from NBT into memory. Only exists server
	 * side.
	 * 
	 * @param nbt
	 */
	public ModFlagData(NBTTagCompound nbt)
	{
		this.readFromNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		// A list containing all dimension ID and data within it.
		Iterator dimensions = nbt.getTags().iterator();

		while (dimensions.hasNext())
		{
			NBTTagCompound dimension = (NBTTagCompound) dimensions.next();

			try
			{
				int dimensionID = Integer.parseInt(dimension.getName());
				World world = DimensionManager.getWorld(dimensionID);
				FlagWorld readData = new FlagWorld(world, nbt);
				this.flagWorlds.add(readData);
			}
			catch (Exception e)
			{
				System.out.println("Mod Flag: Failed to read dimension data: " + dimension.getName());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		for (FlagWorld worldData : this.flagWorlds)
		{
			try
			{
				nbt.setTag(worldData.world.provider.dimensionId + "", worldData.getNBT());
			}
			catch (Exception e)
			{
				System.out.println("Failed to save world flag data: " + worldData.world);
				e.printStackTrace();
			}
		}
	}

	public FlagWorld getFlagWorld(World world)
	{
		FlagWorld worldData = null;

		for (FlagWorld data : this.flagWorlds)
		{
			if (data.world.provider.dimensionId == world.provider.dimensionId)
			{
				worldData = data;
				break;
			}
		}

		// If data is null, create it.
		if (worldData == null)
		{
			worldData = new FlagWorld(world);
			this.flagWorlds.add(worldData);
		}

		return worldData;
	}

	public boolean containsValue(World world, String flagName, String checkValue, Vector3 position)
	{
		return this.getFlagWorld(world).containsValue(flagName, checkValue, position);
	}
}
