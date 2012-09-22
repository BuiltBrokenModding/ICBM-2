package icbm;

import net.minecraft.src.EntitySmokeFX;
import net.minecraft.src.World;
import universalelectricity.prefab.Vector3;

public class SmokeFX extends EntitySmokeFX
{
	public SmokeFX(World par1World, Vector3 position, float red, float green, float blue, float scale, float distance)
	{
		super(par1World, position.x, position.y, position.z, 0, 0, 0, scale);
		this.renderDistanceWeight = distance;
		this.particleRed = red;
		this.particleBlue = blue;
		this.particleGreen = green;
	}

}
