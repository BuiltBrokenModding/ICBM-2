package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.IAmmoType;

/**
 * Created by robert on 12/28/2014.
 */
//TODO re-implement as this is a temp class
public class AmmoTypeMissile implements IAmmoType
{
    public static final AmmoTypeMissile INSTANCE = new AmmoTypeMissile();

    private AmmoTypeMissile() {}

    @Override
    public String getCategory()
    {
        return "Missile";
    }

    @Override
    public String getType()
    {
        return "Small";
    }

    @Override
    public String getUnlocalizedName()
    {
        return ICBM.PREFIX + "missile.small";
    }
}
