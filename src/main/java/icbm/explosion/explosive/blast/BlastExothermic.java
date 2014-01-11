package icbm.explosion.explosive.blast;

import icbm.Reference;
import icbm.explosion.missile.Explosive;
import icbm.explosion.missile.ex.ExExothermic;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;

public class BlastExothermic extends BlastBeam
{
	public BlastExothermic(World world, Entity entity, double x, double y, double z, float size)
	{
		super(world, entity, x, y, z, size);
		this.red = 0.7f;
		this.green = 0.3f;
		this.blue = 0;
	}

	@Override
	public void doExplode()
	{
		super.doExplode();
		this.worldObj.playSoundEffect(position.x, position.y, position.z, Reference.PREFIX + "beamcharging", 4.0F, 0.8F);
	}

	@Override
	public void doPostExplode()
	{
		super.doPostExplode();

		if (!this.worldObj.isRemote)
		{
			this.worldObj.playSoundEffect(position.x, position.y, position.z, Reference.PREFIX + "powerdown", 4.0F, 0.8F);

			if (this.canFocusBeam(this.worldObj, position) && this.thread.isComplete)
			{
				for (Vector3 targetPosition : this.thread.results)
				{
					double distance = Vector3.distance(targetPosition, position);

					double distanceFromCenter = position.distance(targetPosition);

					if (distanceFromCenter > this.getRadius())
						continue;

					/*
					 * Reduce the chance of setting blocks on fire based on distance from center.
					 */
					double chance = this.getRadius() - (Math.random() * distanceFromCenter);

					if (chance > distanceFromCenter * 0.55)
					{
						/*
						 * Check to see if the block is an air block and there is a block below it
						 * to support the fire.
						 */
						int blockID = this.worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

						if (blockID == Block.waterStill.blockID || blockID == Block.waterMoving.blockID || blockID == Block.ice.blockID)
						{
							this.worldObj.setBlockToAir(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
						}

						if ((blockID == 0 || blockID == Block.snow.blockID) && this.worldObj.getBlockMaterial(targetPosition.intX(), targetPosition.intY() - 1, targetPosition.intZ()).isSolid())
						{
							if (this.worldObj.rand.nextFloat() > 0.999)
							{
								this.worldObj.setBlock(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), Block.lavaMoving.blockID, 0, 2);
							}
							else
							{
								this.worldObj.setBlock(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), Block.fire.blockID, 0, 2);

								blockID = this.worldObj.getBlockId(targetPosition.intX(), targetPosition.intY() - 1, targetPosition.intZ());

								if (((ExExothermic) Explosive.exothermic).createNetherrack && (blockID == Block.stone.blockID || blockID == Block.grass.blockID || blockID == Block.dirt.blockID) && this.worldObj.rand.nextFloat() > 0.75)
								{
									this.worldObj.setBlock(targetPosition.intX(), targetPosition.intY() - 1, targetPosition.intZ(), Block.netherrack.blockID, 0, 2);
								}
							}
						}
						else if (blockID == Block.ice.blockID)
						{
							this.worldObj.setBlockToAir(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
						}
					}
				}

				this.worldObj.playSoundEffect(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D, Reference.PREFIX + "explosionfire", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);
			}

			this.worldObj.setWorldTime(18000);
		}
	}

	@Override
	public boolean canFocusBeam(World worldObj, Vector3 position)
	{
		long worldTime = worldObj.getWorldTime();

		while (worldTime > 23999)
		{
			worldTime -= 23999;
		}

		return worldTime < 12000 && !worldObj.isRaining() && super.canFocusBeam(worldObj, position);
	}

}
