package icbm.explosion.zhapin.ex;

import ic2.api.IEnergyStorage;
import icbm.api.explosion.IEMPBlock;
import icbm.explosion.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import universalelectricity.core.implement.IDisableable;
import universalelectricity.core.implement.IJouleStorage;
import universalelectricity.core.vector.Vector3;
import chb.mods.mffs.api.IForceEnergyStorageBlock;
import chb.mods.mffs.api.IForceFieldBlock;

public class ExDianCiWave extends ZhaPin
{
	public ExDianCiWave(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	/**
	 * World worldObj, Vector3 position, int amount, boolean isExplosive
	 */
	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		int r = radius;

		for (int x = -r; x < r; x++)
		{
			for (int y = -r; y < r; y++)
			{
				for (int z = -r; z < r; z++)
				{
					double dist = MathHelper.sqrt_double((x * x + y * y + z * z));

					Vector3 searchPosition = Vector3.add(position, new Vector3(x, y, z));
					if (dist > r)
						continue;

					if (Math.round(position.x + y) == position.intY())
					{
						worldObj.spawnParticle("largesmoke", searchPosition.x, searchPosition.y, searchPosition.z, 0, 0, 0);
					}

					int blockID = searchPosition.getBlockID(worldObj);
					Block block = Block.blocksList[blockID];
					TileEntity tileEntity = searchPosition.getTileEntity(worldObj);

					if (block != null)
					{
						if (block instanceof IForceFieldBlock)
						{
							((IForceFieldBlock) Block.blocksList[blockID]).weakenForceField(worldObj, searchPosition.intX(), searchPosition.intY(), searchPosition.intZ());
						}
						else if (block instanceof IEMPBlock)
						{
							((IEMPBlock) block).onEMP(worldObj, searchPosition, dianCi);
						}
						else if (tileEntity != null)
						{
							if (tileEntity instanceof IJouleStorage)
							{
								((IJouleStorage) tileEntity).setJoules(0);
							}

							if (tileEntity instanceof IDisableable)
							{
								((IDisableable) tileEntity).onDisable(400);
							}

							if (tileEntity instanceof IForceEnergyStorageBlock)
							{
								((IForceEnergyStorageBlock) tileEntity).consumePowerfromStorage((int) worldObj.rand.nextFloat() * ((IForceEnergyStorageBlock) tileEntity).getStorageMaxPower(), false);
							}

							if (tileEntity instanceof IForceFieldBlock)
							{
								((IForceFieldBlock) tileEntity).weakenForceField(worldObj, searchPosition.intX(), searchPosition.intY(), searchPosition.intZ());
							}

							if (tileEntity instanceof IEnergyStorage)
							{
								((IEnergyStorage) tileEntity).setStored(0);
							}
						}
					}
				}
			}
		}

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.emp", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		return false;
	}

	@Override
	public float getRadius()
	{
		return 50;
	}

	@Override
	public double getEnergy()
	{
		return 0;
	}
}
