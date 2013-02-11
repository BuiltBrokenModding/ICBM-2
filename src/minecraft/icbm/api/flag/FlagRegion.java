package icbm.api.flag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.vector.Region3;
import net.minecraft.nbt.NBTTagCompound;

/**
 * A defined region.
 * 
 * @author Calclavia
 * 
 */
public class FlagRegion extends FlagBase
{
	/**
	 * The region in which this flag has affect in.
	 */
	public FlagWorld flagWorld;

	public String name;
	public Region3 region;
	public final List<Flag> flags = new ArrayList<Flag>();

	public FlagRegion(FlagWorld worldFlagData)
	{
		this.flagWorld = worldFlagData;
	}

	public FlagRegion(FlagWorld flagWorld, String name, Region3 region)
	{
		this.flagWorld = flagWorld;
		this.name = name;
		this.region = region;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		String flagName = nbt.getString("name");

		Vector3 startVector = Vector3.readFromNBT("min_", nbt);
		Vector3 endVector = Vector3.readFromNBT("max_", nbt);

		this.region = new Region3(startVector, endVector);

		/**
		 * Child Data
		 */
		Iterator childNodes = nbt.getTags().iterator();

		while (childNodes.hasNext())
		{
			NBTTagCompound childNode = (NBTTagCompound) childNodes.next();

			try
			{
				Flag flag = new Flag(this);
				flag.readFromNBT(childNode);
				this.flags.add(flag);
			}
			catch (Exception e)
			{
				System.out.println("Mod Flag: Failed to read flag data: " + childNode.getName());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("name", this.name);

		this.region.min.writeToNBT("min_", nbt);
		this.region.max.writeToNBT("max_", nbt);

		for (Flag flag : this.flags)
		{
			try
			{
				NBTTagCompound flagCompound = new NBTTagCompound();
				flag.writeToNBT(flagCompound);
				nbt.setTag(flag.name, flagCompound);
			}
			catch (Exception e)
			{
				System.out.println("Failed to save world flag data: " + flag.name);
				e.printStackTrace();
			}
		}
	}

}
