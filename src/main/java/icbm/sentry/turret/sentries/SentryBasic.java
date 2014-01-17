package icbm.sentry.turret.sentries;

import net.minecraft.inventory.IInventory;
import icbm.Reference;
import icbm.sentry.turret.TileTurret;
import icbm.sentry.turret.weapon.WeaponProjectile;

/** Most simple sentry gun using a single cannon
 * 
 * @author DarkGuardsman */
public class SentryBasic extends Sentry
{
    public SentryBasic(TileTurret turret, IInventory inv)
    {
        super(turret);
        this.getWeaponSystems()[0] = new WeaponProjectile(this, inv);
        this.energyPerTick = 20;
    }

    @Override
    public void playFiringSound()
    {
        turret.worldObj.playSoundEffect(turret.xCoord + 0.5, turret.yCoord + 0.5, turret.zCoord + 0.5, Reference.PREFIX + "machinegun", 5F, 1F);
    }
}
