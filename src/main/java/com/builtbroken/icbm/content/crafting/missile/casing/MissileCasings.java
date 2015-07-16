package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;

/**
 * Created by robert on 12/28/2014.
 */
public enum MissileCasings
{
    MICRO(WarheadCasings.EXPLOSIVE_MICRO, MissileMicro.class, 1200, true),
    SMALL(WarheadCasings.EXPLOSIVE_SMALL, MissileSmall.class, 12000, true),
    STANDARD(WarheadCasings.EXPLOSIVE_STANDARD, MissileStandard.class, 72000, false),
    MEDIUM(WarheadCasings.EXPLOSIVE_MEDIUM, MissileMedium.class, 360000, false),
    LARGE(WarheadCasings.EXPLOSIVE_LARGE, MissileLarge.class, 1440000, false);

    public final WarheadCasings warhead_casing;
    public final Class<? extends Missile> missile_clazz;
    public final int maxFlightTimeInTicks;
    public boolean enabled = true;

    MissileCasings(WarheadCasings warhead, Class<? extends Missile> missile_clazz, int maxFlightTicks, boolean enabled)
    {
        this.warhead_casing = warhead;
        this.missile_clazz = missile_clazz;
        this.maxFlightTimeInTicks = maxFlightTicks;
        this.enabled = enabled;
    }

    public static void register()
    {
        for (MissileCasings size : values())
        {
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, "missile_" + size.name().toLowerCase(), size.missile_clazz);
        }
    }
}
