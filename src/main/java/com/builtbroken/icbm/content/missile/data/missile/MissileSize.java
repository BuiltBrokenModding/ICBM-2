package com.builtbroken.icbm.content.missile.data.missile;

import com.builtbroken.icbm.content.missile.data.casing.MissileCasingData;
import com.builtbroken.icbm.content.missile.parts.warhead.WarheadCasings;

import java.util.HashMap;

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

    /** Size of warhead that can fit into the missile */
    public final WarheadCasings warhead_casing;
    /** How long the missile can stay in air before being collected as trash */
    public final int maxFlightTimeInTicks;

    /** Map of casing data for the missile size */
    public final HashMap<String, MissileCasingData> casingDataMap = new HashMap();

    /** Default missile casing size */
    public MissileCasingData defaultMissileCasing;

    /**
     * @param warhead        - warhead size to use
     * @param maxFlightTicks - time in air before delted
     */
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

    /**
     * Tries to find the missile data for the ID
     *
     * @param missileDataID - ID of the missile, should be lower cased
     * @return missile data or default missile data to prevent NPE
     */
    public MissileCasingData getMissileData(String missileDataID)
    {
        if (casingDataMap.containsKey(missileDataID))
        {
            return casingDataMap.get(missileDataID);
        }
        if (defaultMissileCasing == null)
        {
            defaultMissileCasing = casingDataMap.get("icbm:" + name().toLowerCase());
        }
        return defaultMissileCasing;
    }
}
