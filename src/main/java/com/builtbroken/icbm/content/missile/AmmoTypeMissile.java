package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.IAmmoType;
import com.builtbroken.icbm.content.crafting.missile.MissileSizes;

/**
 * Created by robert on 12/28/2014.
 */
//TODO re-implement as this is a temp class
public class AmmoTypeMissile implements IAmmoType
{
    public static final AmmoTypeMissile MICRO = new AmmoTypeMissile(MissileSizes.MICRO);
    public static final AmmoTypeMissile SMALL = new AmmoTypeMissile(MissileSizes.SMALL);
    public static final AmmoTypeMissile STANDARD = new AmmoTypeMissile(MissileSizes.STANDARD);
    public static final AmmoTypeMissile MEDIUM = new AmmoTypeMissile(MissileSizes.MEDIUM);
    public static final AmmoTypeMissile LARGE = new AmmoTypeMissile(MissileSizes.LARGE);

    protected MissileSizes size;

    private AmmoTypeMissile(MissileSizes size)
    {
        this.size = size;
    }

    @Override
    public String getCategory()
    {
        return "Missile";
    }

    @Override
    public String getType()
    {
        return size.name().toLowerCase();
    }

    @Override
    public String getUnlocalizedName()
    {
        return ICBM.PREFIX + getCategory() +"." + getType();
    }
}
