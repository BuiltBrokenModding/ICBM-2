package icbm.common.zhapin.ex;

import ic2.api.IEnergyStorage;
import icbm.common.TYinXing;
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

		int i = MathHelper.floor_double(position.x);
		int j = MathHelper.floor_double(position.y);
		int k = MathHelper.floor_double(position.z);

		for (int x = -r; x < r; x++)
		{
			for (int y = -r; y < r; y++)
			{
				for (int z = -r; z < r; z++)
				{
					double dist = MathHelper.sqrt_double((x * x + y * y + z * z));
					if (dist > r)
						continue;

					if (Math.round(j + y) == position.intY())
					{
						worldObj.spawnParticle("largesmoke", i + x, j + y, k + z, 0, 0, 0);
					}

					TileEntity tileEntity = worldObj.getBlockTileEntity(i + x, j + y, k + z);
					int blockID = worldObj.getBlockId(i + x, j + y, k + z);

					if (Block.blocksList[blockID] != null)
					{
						if (Block.blocksList[blockID] instanceof IForceFieldBlock)
						{
							((IForceFieldBlock) Block.blocksList[blockID]).weakenForceField(worldObj, i + x, j + y, k + z);
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
								((IForceFieldBlock) tileEntity).weakenForceField(worldObj, i + x, j + y, k + z);
							}

							if (tileEntity instanceof TYinXing)
							{
								((TYinXing) tileEntity).setFangGe(0, 0);
								((TYinXing) tileEntity).setQing(false);
								worldObj.markBlockForRenderUpdate(i + x, j + y, k + z);
							}

							if (tileEntity instanceof IEnergyStorage)
							{
								try
								{
									Field field = ReflectionHelper.findField(Class.forName("ic2.common.TileEntityElectricBlock"), "energy");
									field.set(tileEntity, 0);
								}
								catch (Exception e)
								{
									worldObj.createExplosion(explosionSource, i + x, j + y, k + z, 1.5f, true);
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

}
