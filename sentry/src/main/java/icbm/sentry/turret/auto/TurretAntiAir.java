package icbm.sentry.turret.auto;

import icbm.api.sentry.IAATarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.item.ItemStack;
import universalelectricity.api.vector.Vector3;
import icbm.Reference;
import icbm.api.sentry.IAmmunition;
import icbm.api.sentry.ProjectileType;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.items.ItemAmmo.AmmoType;
import icbm.sentry.turret.weapon.WeaponProjectile;

/**
 * AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman
 */
public class TurretAntiAir extends TurretAuto
{
    private static Class<? extends EntityLiving>[] vanillaFlight = new Class[] { EntityWither.class, EntityGhast.class, EntityBlaze.class, EntityDragon.class };

	public TurretAntiAir(TileTurret host)
	{
		super(host);
		this.weaponSystem = new WeaponProjectile(this, 1, 10)
		{
			@Override
			public boolean isAmmo(ItemStack stack)
			{
				return super.isAmmo(stack) && ((IAmmunition) stack.getItem()).getType(stack) == ProjectileType.CONVENTIONAL;
			}

			@Override
			public void fire(Vector3 target)
			{
				super.fire(target);
				turret.getHost().world().playSoundEffect(turret.getHost().x(), turret.getHost().y(), turret.getHost().z(), Reference.PREFIX + "aagun", 5F, 1F - (turret.getHost().world().rand.nextFloat() * 0.2f));
			}
		};
		this.centerOffset.y = 0.75;
	}

    @Override
    public boolean canFire ()
    {
        if (this.target instanceof IAATarget || isVanillaFlier(this.target))
            return super.canFire();
        return false;
    }

    private static boolean isVanillaFlier (Entity target)
    {
        for (Class<? extends EntityLiving> entityClass : vanillaFlight)
        {
            if (entityClass.isInstance(target))
                return true;
        }
        return false;
    }
}
