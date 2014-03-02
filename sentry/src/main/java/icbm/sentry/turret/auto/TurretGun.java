package icbm.sentry.turret.auto;

import net.minecraft.item.ItemStack;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import icbm.Reference;
import icbm.api.sentry.IAmmunition;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.items.ItemAmmo.AmmoType;
import icbm.sentry.turret.weapon.WeaponProjectile;

/** @author DarkGuardsman */
public class TurretGun extends TurretAuto
{
	public TurretGun(TileTurret host)
	{
		super(host);
		weaponSystem = new WeaponProjectile(this, 1, 10)
		{
			@Override
			public boolean isAmmo(ItemStack stack)
			{
				return stack.getItem() instanceof IAmmunition && stack.getItemDamage() == AmmoType.BULLET.ordinal();
			}

			@Override
			public void fire(Vector3 target)
			{
				super.fire(target);
				turret.getHost().world().playSoundEffect(turret.getHost().x(), turret.getHost().y(), turret.getHost().z(), Reference.PREFIX + "machinegun", 5F, 1F - (turret.getHost().world().rand.nextFloat() * 0.2f));
			}
		};
		centerOffset.y = 0.27;
		range = 20;
		maxCooldown = 30;
		barrelLength = 0.7f;
	}
}
