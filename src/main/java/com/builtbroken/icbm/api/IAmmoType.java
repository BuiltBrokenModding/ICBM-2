package com.builtbroken.icbm.api;

/**
 * Created by robert on 12/28/2014.
 */
public interface IAmmoType
{
    /**
     * Primary group the ammo counts as
     * for example "Rocket", "Missile", "Bullet", "Shell"
     * @return valid string value
     */
    String getCategory();

    /**
     * Gets the type of the ammo
     * for example "9mm", "12Gauge", "120mm"
     * @return valid string value
     */
    String getType();

    /** Name to use for translation */
    String getUnlocalizedName();
}
