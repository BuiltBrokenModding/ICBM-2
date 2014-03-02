package icbm.sentry.turret.weapon;

import icbm.api.sentry.IAmmunition;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import universalelectricity.api.vector.Vector3;

/**
 * Basic projectile weapon system design more to be used as a prefab. By default it acts like a hand
 * gun with low hit chance and damage.
 * 
 * @author DarkGuardsman, tgame14
 */
public class WeaponProjectile extends WeaponSystem
{
	protected float inaccuracy = 0.1f;
	protected float damage = 5f;
	protected DamageSource damageSource = TurretDamageSource.TurretProjectile;

	public WeaponProjectile(Turret sentry, float damage)
	{
		super(sentry);
		this.damage = damage;
	}

	@Override
	public void fire(Vector3 target)
	{
		super.fire(target.clone().translate(getInaccuracy(), getInaccuracy(), getInaccuracy()));
	}

	private float getInaccuracy()
	{
		return turret.getHost().world().rand.nextFloat() * inaccuracy;
	}

	@Override
	public void onHitEntity(Entity entity)
	{
		if (entity != null)
		{
			entity.attackEntityFrom(damageSource, damage);
		}
	}

	public boolean isAmmo(ItemStack stack)
	{
		return stack != null && stack.getItem() instanceof IAmmunition;
	}

	/**
	 * Used to consume ammo or check if ammo can be consumed
	 * 
	 * @param count - number of items to consume
	 * @param doConsume - true items will be consumed
	 * @return true if all rounds were consumed
	 */
	public boolean consumeAmmo(int count, boolean doConsume)
	{
		if (count > 0 && turret.getHost() instanceof IInventory)
		{
			// TODO add a way to restrict this to a set range of slots
			IInventory inv = ((IInventory) turret.getHost());
			// 0-4 are upgrade slots for the sentry, 5-8 are ammo slots
			int consumeCount = 0;
			for (int slot = 5; slot < inv.getSizeInventory(); slot++)
			{
				ItemStack stack = inv.getStackInSlot(slot);
				if (isAmmo(stack))
				{
					if (stack.stackSize >= count)
					{
						if (doConsume)
							stack.stackSize -= count;
						return true;
					}
					else
					{

					}
				}
			}
		}
		return false;
	}
}
