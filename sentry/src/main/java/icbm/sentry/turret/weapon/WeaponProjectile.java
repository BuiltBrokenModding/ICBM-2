package icbm.sentry.turret.weapon;

import icbm.api.sentry.IAmmunition;
import icbm.api.sentry.ProjectileType;
import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.items.ItemAmmo.AmmoType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.utility.inventory.InventoryUtility;

/** Basic projectile weapon system design more to be used as a prefab. By default it acts like a hand
 * gun with low hit chance and damage.
 * 
 * @author DarkGuardsman, tgame14 */
public class WeaponProjectile extends WeaponDamage
{
    protected float inaccuracy = 0.1f;
    private int ammoAmount;

    public WeaponProjectile(Turret sentry, int ammoAmount, float damage)
    {
        super(sentry, TurretDamageSource.turretProjectile, damage);
        this.ammoAmount = ammoAmount;
    }

    @Override
    public void fire(Vector3 target)
    {
        super.fire(target.clone().translate(getInaccuracy(), getInaccuracy(), getInaccuracy()));
        consumeAmmo(ammoAmount, true);
        InventoryUtility.dropItemStack(turret.world(), turret.getPosition(), new ItemStack(ICBMSentry.itemAmmo, 1, AmmoType.SHELL.ordinal()));
    }

    private float getInaccuracy()
    {
        return turret.getHost().world().rand.nextFloat() * inaccuracy;
    }

    public boolean isAmmo(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof IAmmunition && ((IAmmunition) stack.getItem()).getType(stack) == ProjectileType.CONVENTIONAL;
    }

    @Override
    public boolean canFire()
    {
        return consumeAmmo(ammoAmount, false);
    }

    /** Used to consume ammo or check if ammo can be consumed
     * 
     * @param count - number of items to consume
     * @param doConsume - true items will be consumed
     * @return true if all rounds were consumed */
    public boolean consumeAmmo(int count, boolean doConsume)
    {
        int consumeCount = 0;
        int need = count;
        IInventory inv = turret.getHost().getInventory();

        if (count > 0 && inv != null)
        {
            for (int slot = 0; slot < inv.getSizeInventory(); slot++)
            {
                ItemStack itemStack = inv.getStackInSlot(slot);

                if (isAmmo(itemStack))
                {
                    IAmmunition ammo = (IAmmunition) itemStack.getItem();
                    if (ammo.getAmmoCount(itemStack) >= need)
                    {
                        if (doConsume)
                            ammo.consumeAmmo(itemStack, need);
                        return true;
                    }
                    else
                    {
                        int consume = need - ammo.getAmmoCount(itemStack);
                        if (doConsume)
                            ammo.consumeAmmo(itemStack, consume);
                        need -= consume;
                    }
                    consumeCount += ammo.getAmmoCount(itemStack);
                }
            }
        }

        return consumeCount >= count;
    }
}
