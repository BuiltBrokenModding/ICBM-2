package icbm.zhapin.ex;

import ic2.api.IEnergyStorage;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.implement.IDisableable;
import universalelectricity.implement.IElectricityStorage;
import universalelectricity.prefab.Vector3;

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
        
        for(int x = -r; x < r; x++)
		{
			for(int y = -r; y < r; y++)
			{
				for(int z = -r; z < r; z++)
				{
					double dist = MathHelper.sqrt_double((x*x + y*y + z*z));
					if(dist > r) continue;
					
					if(Math.round(j+y) == position.intY())
					{
						worldObj.spawnParticle("largesmoke", i+x, j+y, k+z, 0, 0, 0);
					}
					
					TileEntity tileEntity = worldObj.getBlockTileEntity(i+x, j+y, k+z);
					int blockID = worldObj.getBlockId(i+x, j+y, k+z);
					
					if(tileEntity != null)
					{
						if(tileEntity instanceof IElectricityStorage)
						{
							((IElectricityStorage)tileEntity).setWattHours(0);
						}
						
						if(tileEntity instanceof IEnergyStorage)
						{
							worldObj.createExplosion(explosionSource, i+x, j+y, k+z, 2f);
						}

						if(tileEntity instanceof IDisableable)
						{
							((IDisableable)tileEntity).onDisable(400);
						}
					}
				}
			}
		}
    	
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.emp", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		return false;
	}

}
