package icbm.sentry.turret.weapon.types;

import icbm.Reference;
import icbm.sentry.ICBMSentry;
import icbm.sentry.interfaces.IEnergyWeapon;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.turret.weapon.WeaponDamage;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import universalelectricity.api.CompatibilityModule;
import universalelectricity.api.vector.IVector3;
import calclavia.lib.prefab.damage.ObjectDamageSource;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** @author DarkGuardsman */
public class WeaponLaser extends WeaponDamage implements IEnergyWeapon
{
    private long energy;

    public WeaponLaser(ITurret sentry, float damage, long energy)
    {
        this(sentry, damage);
        this.soundEffect = Reference.PREFIX + "lasershot";
        this.energy = energy;
    }

    public WeaponLaser(ITurret sentry, float damage)
    {
        super(sentry, ObjectDamageSource.doLaserDamage(sentry), damage);
    }

    @Override
    public void fire(Entity entity)
    {
        this.onHitEntity(entity);
        this.playFiringAudio();
    }

    @Override
    public void onHitEntity(Entity entity)
    {
        if (entity != null)
        {
            super.onHitEntity(entity);
            entity.setFire(5);
        }
    }

    @Override
    public void fire(IVector3 target)
    {
        //TODO add tile damage effect vs light tiles like grass
        this.playFiringAudio();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void fireClient(IVector3 hit)
    {
        ICBMSentry.proxy.renderBeam(world(), getBarrelEnd(), hit, 1F, 1F, 1F, 10);
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        return stack != null && CompatibilityModule.isHandler(stack.getItem());
    }

    @Override
    public boolean consumeAmmo(int sum, boolean yes)
    {
        return true;
    }

    @Override
    public long getEnergyPerShot()
    {
        return this.energy;
    }
}
