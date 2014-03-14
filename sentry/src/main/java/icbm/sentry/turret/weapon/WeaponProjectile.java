package icbm.sentry.turret.weapon;

import icbm.sentry.interfaces.ITurretUpgrade;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.items.ItemSentryUpgrade.TurretUpgradeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.vector.Vector3;
import calclavia.api.icbm.sentry.IAmmunition;
import calclavia.api.icbm.sentry.ProjectileType;
import calclavia.lib.utility.inventory.InventoryUtility;

/** Basic projectile weapon system design more to be used as a prefab. By default it acts like a hand
 * gun with low hit chance and damage.
 * 
 * @author DarkGuardsman, tgame14, Calclavia */
public class WeaponProjectile extends WeaponDamage
{
    protected float inaccuracy = 0.0005f;
    protected float min_range = 1;
    protected float max_range = 100;
    protected int ammoAmount;

    public WeaponProjectile(Turret sentry, int ammoAmount, float damage)
    {
        super(sentry, TurretDamageSource.turretProjectile, damage);
        this.ammoAmount = ammoAmount;
    }

    @Override
    public void fire(Vector3 target)
    {
        double d = target.distance(this.turret.getAbsoluteCenter());
        super.fire(target.clone().translate(getInaccuracy(d), getInaccuracy(d), getInaccuracy(d)));
        consumeAmmo(ammoAmount, true);
    }

    protected double getInaccuracy(double distance)
    {
        Random rand = turret.getHost().world().rand;
        double offset = distance * (rand.nextFloat() - rand.nextFloat()) * inaccuracy;
        if (distance < min_range || distance > max_range)
        {
            return offset * 2;
        }
        return offset;
    }

    public boolean isAmmo(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof IAmmunition && ((IAmmunition) stack.getItem()).getType(stack) != ProjectileType.UNKNOWN;
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
                        {
                            ItemStack eject = ammo.getShell(itemStack, need);
                            inv.setInventorySlotContents(slot, ammo.consumeAmmo(itemStack, need));
                            if (this.turret.upgrade_count.containsKey(ITurretUpgrade.SHELL_COLLECTOR))
                                eject = InventoryUtility.putStackInInventory(inv, eject, ForgeDirection.UNKNOWN.ordinal(), true);
                            if (eject != null)
                                InventoryUtility.dropItemStack(turret.world(), turret.getPosition(), eject);
                        }
                        return true;
                    }
                    else
                    {
                        int consume = need - ammo.getAmmoCount(itemStack);
                        if (doConsume)
                        {
                            ItemStack eject = ammo.getShell(itemStack, need);
                            inv.setInventorySlotContents(slot, ammo.consumeAmmo(itemStack, consume));
                            if (this.turret.upgrade_count.containsKey(ITurretUpgrade.SHELL_COLLECTOR))
                                eject = InventoryUtility.putStackInInventory(inv, eject, ForgeDirection.UNKNOWN.ordinal(), true);
                            if (eject != null)
                                InventoryUtility.dropItemStack(turret.world(), turret.getPosition(), eject);

                        }
                        need -= consume;
                    }
                    consumeCount += ammo.getAmmoCount(itemStack);
                }
            }
        }

        return consumeCount >= count;
    }
}
