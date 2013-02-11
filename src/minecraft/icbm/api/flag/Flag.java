package icbm.api.flag;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.vector.Region3;

public class Flag extends FlagBase
{
	/**
	 * The region in which this flag has affect in.
	 */
	public FlagRegion flagRegion;

	public String name;

	public String value;

	public Flag(FlagRegion flagRegion)
	{
		this.flagRegion = flagRegion;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		String flagName = nbt.getString("name");
		String flagValue = nbt.getString("value");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("name", this.name);
		nbt.setString("value", value);

	}
}
