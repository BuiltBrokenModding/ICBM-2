package icbm.api.flag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.vector.Region3;

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
	private final List<Flag> flags = new ArrayList<Flag>();

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
		this.name = nbt.getName();

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
		nbt.setName(this.name);

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

	public boolean containsValue(String flagName, String checkValue, Vector3 position)
	{
		for (Flag flag : this.flags)
		{
			if (flag.name.equalsIgnoreCase(flagName) && flag.value.equalsIgnoreCase(checkValue))
			{
				return true;
			}
		}

		return false;
	}

	public boolean setFlag(String flagName, String value)
	{
		this.removeFlag(flagName);

		if (value != null && value != "")
		{
			return this.flags.add(new Flag(this, flagName, value));
		}

		return false;
	}

	public boolean removeFlag(String flagName)
	{
		for (Flag region : this.flags)
		{
			if (region.name.equalsIgnoreCase(name))
			{
				this.flags.remove(region);
				return true;
			}
		}

		return false;
	}

	public List<Flag> getFlags()
	{
		Iterator<Flag> it = this.flags.iterator();
		while (it.hasNext())
		{
			Flag region = it.next();

			if (region == null)
			{
				it.remove();
				continue;
			}

			if (region.name == null || region.name == "")
			{
				it.remove();
				continue;
			}
		}

		return this.flags;
	}
}
