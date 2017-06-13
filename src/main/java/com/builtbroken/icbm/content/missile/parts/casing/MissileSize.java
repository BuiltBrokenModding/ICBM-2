package com.builtbroken.icbm.content.missile.parts.casing;

import com.builtbroken.icbm.content.missile.data.casing.MissileCasingData;
import com.builtbroken.icbm.content.missile.parts.warhead.WarheadCasings;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum of missile sizes
 * Created by robert on 12/28/2014.
 */
public enum MissileSize
{
    //https://en.wikipedia.org/wiki/RPG-7
    MICRO(WarheadCasings.EXPLOSIVE_MICRO, 1200), // 1m and bellow
    //http://www.army-technology.com/projects/hellfire-ii-missile/
    SMALL(WarheadCasings.EXPLOSIVE_SMALL, 12000), //3m and bellow
    STANDARD(WarheadCasings.EXPLOSIVE_STANDARD, 72000), //7m and bellow
    MEDIUM(WarheadCasings.EXPLOSIVE_MEDIUM, 360000), //16m
    LARGE(WarheadCasings.EXPLOSIVE_LARGE, 1440000); //32m

    public final WarheadCasings warhead_casing;
    public final int maxFlightTimeInTicks;

    public final List<MissileCasingData> casingDataList = new ArrayList();
    public MissileCasingData defaultMissileCasing;

    MissileSize(WarheadCasings warhead, int maxFlightTicks)
    {
        this.warhead_casing = warhead;
        this.maxFlightTimeInTicks = maxFlightTicks;
    }

    /**
     * Gets a missile casing from the metadata given.
     * Basicly an array access with upper and lower limit
     *
     * @param itemDamage - meta data
     * @return casing or small if the itemDamage was invalid
     */
    public static MissileSize fromMeta(int itemDamage)
    {
        if (itemDamage >= 0 && itemDamage < values().length)
        {
            return MissileSize.values()[itemDamage];
        }
        return MissileSize.SMALL;
    }

    /**
     * Gets the size of the missile
     *
     * @param missileSize
     * @return
     */
    public static MissileSize get(int missileSize)
    {
        return fromMeta(missileSize);
    }
}
