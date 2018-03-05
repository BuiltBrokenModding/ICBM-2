package com.builtbroken.icbm.content.missile.parts.warhead;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.function.Function;

/**
 * Enum of warhead casings
 * Created by robert on 1/16/2015.
 */
public enum WarheadCasings
{
    EXPLOSIVE_MICRO(WarheadMicro.class, 1000, i -> new WarheadMicro(i), true),
    EXPLOSIVE_SMALL(WarheadSmall.class, 3500, i -> new WarheadSmall(i), true),
    EXPLOSIVE_STANDARD(WarheadStandard.class, 8000, i -> new WarheadStandard(i), true),
    EXPLOSIVE_MEDIUM(WarheadMedium.class, 150000, i -> new WarheadMedium(i), false),
    EXPLOSIVE_LARGE(WarheadLarge.class, 400000, i -> new WarheadLarge(i), false);

    //TODO create factory methods using lambda expressions to build new warhead objects via WarheadCasings.EXPLOSIVE_MICRO.newWarhead()

    public final Class<? extends Warhead> warhead_clazz;
    public final boolean enabled;

    private static boolean reg;

    @SideOnly(Side.CLIENT)
    public IIcon icon;

    /** Mass of the metal casing not including parts. */
    public double mass;

    public Function<ItemStack, Warhead> warheadFactory;

    WarheadCasings(Class<? extends Warhead> warhead_clazz, double mass, Function<ItemStack, Warhead> warheadFactory, boolean enabled)
    {
        this.warhead_clazz = warhead_clazz;
        this.mass = mass;
        this.warheadFactory = warheadFactory;
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

    public static WarheadCasings get(ItemStack stack)
    {
        return get(stack.getItemDamage());
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

    public Warhead buildModule(ItemStack stack)
    {
        return warheadFactory.apply(stack);
    }
}
