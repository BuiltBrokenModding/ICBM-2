package icbm.api.flag;

import icbm.explosion.zhapin.ZhaPin.ZhaPinType;

import java.util.HashMap;
import java.util.Iterator;

import universalelectricity.core.vector.Vector2;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ModFlagData
{
	protected NBTTagCompound nbt;

	/**
	 * An array of world flag data. Each representing a world.
	 */
	public HashMap<World, WorldFlagData> worldData = new HashMap<World, WorldFlagData>();

	/**
	 * Initiates a new mod flag data and loads everything from NBT into memory.
	 * 
	 * @param nbt
	 */
	public ModFlagData(NBTTagCompound nbt)
	{
		this.nbt = nbt;

		// A list containing all dimension and data within it.
		Iterator dimensions = this.nbt.getTags().iterator();

		while (dimensions.hasNext())
		{
			try
			{
				NBTTagCompound dimension = (NBTTagCompound) dimensions.next();

			}
			catch (Exception e)
			{
				System.out.println("Mod Flag: Failed to read dimension data.");
				e.printStackTrace();
			}
		}

		getCompoundTag("dim" + world.provider.dimensionId);

		Iterator i = dimensions.getTags().iterator();
		while (i.hasNext())
		{
			try
			{
				NBTTagCompound region = (NBTTagCompound) i.next();

				if (Vector2.distance(position, new Vector2(region.getInteger(FIELD_X), region.getInteger(FIELD_Z))) <= region.getInteger(FIELD_R))
				{
					return (ZhaPinType.get(region.getInteger(FIELD_TYPE)) == ZhaPinType.QUAN_BU || ZhaPinType.get(region.getInteger(FIELD_TYPE)) == type);
				}
			}
			catch (Exception e)
			{
			}
		}
	}

	public void addWorld(WorldFlagData data)
	{

	}

	public WorldFlagData getWorldFlags(World world)
	{
		WorldFlagData worldData = this.worldData.get(world);

		/**
		 * If data is null, create it.
		 */
		if (worldData == null)
		{

		}

		return worldData;
	}
}
