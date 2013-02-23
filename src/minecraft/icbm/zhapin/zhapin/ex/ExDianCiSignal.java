package icbm.zhapin.zhapin.ex;

import ic2.api.ICustomElectricItem;
import icbm.api.IMissile;
import icbm.api.RadarRegistry;
import icbm.api.explosion.IEMPItem;
import icbm.zhapin.zhapin.EZhaDan;
import icbm.zhapin.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.implement.IItemElectric;
import universalelectricity.core.vector.Vector3;

public class ExDianCiSignal extends ZhaPin
{
	public ExDianCiSignal(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	/**
	 * World worldObj, Vector3 position, int amount, boolean isExplosive
	 */
	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		// Drop all missiles
		List<Entity> entitiesNearby = RadarRegistry.getEntitiesWithinRadius(position.toVector2(), radius);

		for (Entity entity : entitiesNearby)
		{
			if (entity instanceof IMissile)
			{
				if (((IMissile) entity).getTicksInAir() > -1)
				{
					((IMissile) entity).dropMissileAsItem();
				}
			}
		}

		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
		List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);

		for (Entity entity : entities)
		{
			if (entity instanceof EntityPlayer)
			{
				IInventory inventory = ((EntityPlayer) entity).inventory;

				for (int i = 0; i < inventory.getSizeInventory(); i++)
				{
					ItemStack itemStack = inventory.getStackInSlot(i);

					if (itemStack != null)
					{
						if (itemStack.getItem() instanceof IEMPItem)
						{
							((IEMPItem) itemStack.getItem()).onEMP(itemStack, entity, dianCi);
						}
						else if (itemStack.getItem() instanceof IItemElectric)
						{
							((IItemElectric) itemStack.getItem()).setJoules(0, itemStack);
						}
						else if (itemStack.getItem() instanceof ICustomElectricItem)
						{
							((ICustomElectricItem) itemStack.getItem()).discharge(itemStack, ((ICustomElectricItem) itemStack.getItem()).getMaxCharge(), 0, true, false);
						}
					}
				}
			}
			else if (entity instanceof EZhaDan)
			{
				entity.setDead();
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
