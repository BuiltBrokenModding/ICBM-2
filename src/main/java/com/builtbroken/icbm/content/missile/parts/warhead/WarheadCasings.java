package com.builtbroken.icbm.content.missile.parts.warhead;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

/**
 * Enum of warhead casings
 * Created by robert on 1/16/2015.
 */
public enum WarheadCasings
{
    EXPLOSIVE_MICRO(WarheadMicro.class, 1000, true),
    EXPLOSIVE_SMALL(WarheadSmall.class, 3500, true),
    EXPLOSIVE_STANDARD(WarheadStandard.class, 8000, true),
    EXPLOSIVE_MEDIUM(WarheadMedium.class, 150000, false),
    EXPLOSIVE_LARGE(WarheadLarge.class, 400000, false);

    public final Class<? extends Warhead> warhead_clazz;
    public final boolean enabled;

    private static boolean reg;

    @SideOnly(Side.CLIENT)
    public IIcon icon;

    /** Mass of the metal casing not including parts. */
    public double mass;

    WarheadCasings(Class<? extends Warhead> warhead_clazz, double mass, boolean enabled)
    {
        this.warhead_clazz = warhead_clazz;
        this.mass = mass;
        this.enabled = enabled;
    }

    public static void register()
    {
        if (!reg)
        {
            reg = true;
            for (WarheadCasings size : values())
            {
                MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, "warhead_" + size.name().toLowerCase(), size.warhead_clazz);
            }
        }
    }

    public static WarheadCasings get(int i)
    {
        if (i >= 0 && i < values().length)
        {
            return values()[i];
        }
        return EXPLOSIVE_MICRO;
    }

    public static WarheadCasings fromMeta(int itemDamage)
    {
        if (itemDamage >= 0 && itemDamage < values().length)
        {
            return values()[itemDamage];
        }
        return EXPLOSIVE_SMALL;
    }
}
