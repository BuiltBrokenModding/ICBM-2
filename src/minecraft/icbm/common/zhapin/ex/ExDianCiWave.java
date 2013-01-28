package icbm.common.zhapin.ex;

import ic2.api.energy.tile.IEnergyTile;
import icbm.api.IEMPBlock;
import icbm.common.zhapin.ZhaPin;

import java.lang.reflect.Field;

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
import cpw.mods.fml.relauncher.ReflectionHelper;

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

							if (tileEntity instanceof IEnergyTile)
							{
								try
								{
									Field field = ReflectionHelper.findField(Class.forName("ic2.common.TileEntityElectricBlock"), "energy");
									field.set(tileEntity, 0);
								}
								catch (Exception e)
								{
									worldObj.createExplosion(explosionSource, searchPosition.intX(), searchPosition.intY(), searchPosition.intZ(), 1.5f, true);
									System.err.println("Failed to EMP strike an IC2 energy storage.");
									e.printStackTrace();
								}
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
