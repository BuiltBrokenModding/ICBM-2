package icbm.client.fx;

import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SmokeFX extends EntitySmokeFX
{
	public SmokeFX(World par1World, Vector3 position, float red, float green, float blue, float scale, double distance)
	{
		super(par1World, position.x, position.y, position.z, 0, 0, 0, scale);
		this.renderDistanceWeight = distance;
		this.particleRed = red;
		this.particleBlue = blue;
		this.particleGreen = green;
	}

}
