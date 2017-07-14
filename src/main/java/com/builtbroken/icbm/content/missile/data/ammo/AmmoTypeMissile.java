package com.builtbroken.icbm.content.missile.data.ammo;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.mc.api.data.EnumProjectileTypes;
import com.builtbroken.mc.api.data.weapon.IAmmoType;

/**
 * Created by robert on 12/28/2014.
 */
//TODO re-implement as this is a temp class
public class AmmoTypeMissile implements IAmmoType
{
    public static final AmmoTypeMissile MICRO = new AmmoTypeMissile(MissileSize.MICRO);
    public static final AmmoTypeMissile SMALL = new AmmoTypeMissile(MissileSize.SMALL);
    public static final AmmoTypeMissile STANDARD = new AmmoTypeMissile(MissileSize.STANDARD);
    public static final AmmoTypeMissile MEDIUM = new AmmoTypeMissile(MissileSize.MEDIUM);
    public static final AmmoTypeMissile LARGE = new AmmoTypeMissile(MissileSize.LARGE);

    public MissileSize size;

    private AmmoTypeMissile(MissileSize size)
    {
        this.size = size;
    }

    @Override
    public String getAmmoCategory()
    {
        return "Missile";
    }

    @Override
    public String getAmmoType()
    {
        return size.name().toLowerCase();
    }

    @Override
    public String getUnlocalizedName()
    {
        return ICBM.PREFIX + getAmmoCategory() + "." + getAmmoType();
    }

    @Override
    public EnumProjectileTypes getProjectileType()
    {
        return EnumProjectileTypes.ROCKET;
    }

    @Override
    public String getUniqueID()
    {
        return "icbm:ammoType.missile." + size.name().toLowerCase();
    }

    @Override
    public String getDataType()
    {
        return "ammoType";
    }
}
