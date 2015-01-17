package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;

/**
 * Created by robert on 12/28/2014.
 */
public enum MissileCasings
{
    MICRO(WarheadCasings.EXPLOSIVE_MICRO, MissileMicro.class),
    SMALL(WarheadCasings.EXPLOSIVE_SMALL, MissileSmall.class),
    STANDARD(WarheadCasings.EXPLOSIVE_STANDARD, MissileStandard.class),
    MEDIUM(WarheadCasings.EXPLOSIVE_MEDIUM, MissileMedium.class),
    LARGE(WarheadCasings.EXPLOSIVE_LARGE, MissileLarge.class),
    MICRO_CLASSIC(WarheadCasings.EXPLOSIVE_MICRO, MissileMicroClassic.class),
    SMALL_CLASSIC(WarheadCasings.EXPLOSIVE_SMALL, MissileSmallClassic.class);

    public final WarheadCasings warhead_casing;
    public final Class<? extends Missile> missile_clazz;

    private MissileCasings(WarheadCasings warhead, Class<? extends Missile> missile_clazz)
    {
        this.warhead_casing = warhead;
        this.missile_clazz = missile_clazz;
    }

    public static void register()
    {
        for (MissileCasings size : values())
        {
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, "missile_" + size.name().toLowerCase(), size.missile_clazz);
        }
    }
}
