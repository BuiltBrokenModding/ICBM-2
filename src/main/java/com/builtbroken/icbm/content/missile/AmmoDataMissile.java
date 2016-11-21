package com.builtbroken.icbm.content.missile;

import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/21/2016.
 */
public class AmmoDataMissile implements IAmmoData
{
    public static final AmmoDataMissile MICRO = new AmmoDataMissile(AmmoTypeMissile.MICRO);
    public static final AmmoDataMissile SMALL = new AmmoDataMissile(AmmoTypeMissile.SMALL);
    public static final AmmoDataMissile STANDARD = new AmmoDataMissile(AmmoTypeMissile.STANDARD);
    public static final AmmoDataMissile MEDIUM = new AmmoDataMissile(AmmoTypeMissile.MEDIUM);
    public static final AmmoDataMissile LARGE = new AmmoDataMissile(AmmoTypeMissile.LARGE);

    public AmmoTypeMissile ammoType;

    private AmmoDataMissile(AmmoTypeMissile size)
    {
        this.ammoType = size;
    }

    @Override
    public IAmmoType getAmmoType()
    {
        return ammoType;
    }

    @Override
    public float getBaseDamage()
    {
        return 0;
    }

    @Override
    public float getProjectileVelocity()
    {
        return -1;
    }

    @Override
    public void onImpactEntity(Entity shooter, Entity entity)
    {

    }

    @Override
    public void onImpactGround(World world, int x, int y, int z, double hitX, double hitY, double hitZ)
    {

    }

    @Override
    public String getUniqueID()
    {
        return "icbm:ammo.missile." + ammoType.size.name().toLowerCase();
    }

    @Override
    public String getDataType()
    {
        return "ammo";
    }
}
