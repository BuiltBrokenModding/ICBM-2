package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.items.weapons.IAmmoType;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;

/**
 * Created by robert on 12/28/2014.
 */
//TODO re-implement as this is a temp class
public class AmmoTypeMissile implements IAmmoType
{
    public static final AmmoTypeMissile MICRO = new AmmoTypeMissile(MissileCasings.MICRO);
    public static final AmmoTypeMissile SMALL = new AmmoTypeMissile(MissileCasings.SMALL);
    public static final AmmoTypeMissile STANDARD = new AmmoTypeMissile(MissileCasings.STANDARD);
    public static final AmmoTypeMissile MEDIUM = new AmmoTypeMissile(MissileCasings.MEDIUM);
    public static final AmmoTypeMissile LARGE = new AmmoTypeMissile(MissileCasings.LARGE);

    public MissileCasings size;

    private AmmoTypeMissile(MissileCasings size)
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
