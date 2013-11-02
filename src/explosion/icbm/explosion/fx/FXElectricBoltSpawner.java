package icbm.explosion.fx;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

/** A spawner used to spawn in multiple electrical bolts for a specific duration. */
public class FXElectricBoltSpawner extends EntityFX
{
	private Vector3 start;
	private Vector3 end;

	public FXElectricBoltSpawner(World world, Vector3 startVec, Vector3 targetVec, long seed, int duration)
	{
		super(world, startVec.x, startVec.y, startVec.z, 0.0D, 0.0D, 0.0D);

		if (seed == 0)
		{
			this.rand = new Random();
		}
		else
		{
			this.rand = new Random(seed);
		}

		this.start = startVec;
		this.end = targetVec;
		this.particleMaxAge = duration;
	}

	@Override
	public void onUpdate()
	{
		Minecraft.getMinecraft().effectRenderer.addEffect(new FXElectricBolt(this.worldObj, this.start, this.end, 0));
		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setDead();
		}
	}

}
