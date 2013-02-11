package icbm.api.flag;

import universalelectricity.prefab.vector.Region3;

public class Flag
{
	/**
	 * The region in which this flag has affect in.
	 */
	public Region3 region;

	public String name;

	public String value;

	public Flag(Region3 region, String name, String value)
	{
		this.region = region;
		this.name = name;
		this.value = value;
	}
}
