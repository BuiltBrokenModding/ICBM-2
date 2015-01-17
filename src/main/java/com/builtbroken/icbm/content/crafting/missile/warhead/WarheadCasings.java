package com.builtbroken.icbm.content.crafting.missile.warhead;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;

/**
 * Created by robert on 1/16/2015.
 */
public enum WarheadCasings
{
    EXPLOSIVE_MICRO(WarheadMicro.class),
    EXPLOSIVE_SMALL(WarheadSmall.class),
    EXPLOSIVE_STANDARD(WarheadStandard.class),
    EXPLOSIVE_MEDIUM(WarheadMedium.class),
    EXPLOSIVE_LARGE(WarheadLarge.class);

    public final Class<? extends Warhead> warhead_clazz;

    private WarheadCasings(Class<? extends Warhead> warhead_clazz)
    {
        this.warhead_clazz = warhead_clazz;
    }

    public static void register()
    {
        for (WarheadCasings size : values())
        {
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, "warhead_" + size.name().toLowerCase(), size.warhead_clazz);
        }
    }

    public static WarheadCasings get(int i)
    {
        if(i >= 0 && i < values().length)
            return values()[i];
        return null;
    }
}
