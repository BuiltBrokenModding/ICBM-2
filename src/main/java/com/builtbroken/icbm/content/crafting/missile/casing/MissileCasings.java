package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.ModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Enum of missile sizes
 * Created by robert on 12/28/2014.
 */
public enum MissileCasings
{
    //https://en.wikipedia.org/wiki/RPG-7
    MICRO(WarheadCasings.EXPLOSIVE_MICRO, MissileMicro.class, 1200, 3, 2000, true),
    //http://www.army-technology.com/projects/hellfire-ii-missile/
    SMALL(WarheadCasings.EXPLOSIVE_SMALL, MissileSmall.class, 12000, 10, 96000, true),
    STANDARD(WarheadCasings.EXPLOSIVE_STANDARD, MissileStandard.class, 72000, 100, 800000, true),
    MEDIUM(WarheadCasings.EXPLOSIVE_MEDIUM, MissileMedium.class, 360000, 700, 1500000, false),
    LARGE(WarheadCasings.EXPLOSIVE_LARGE, MissileLarge.class, 1440000, 2000, 4500000, false);

    public final WarheadCasings warhead_casing;
    public final Class<? extends Missile> missile_clazz;
    public final int maxFlightTimeInTicks;
    public boolean enabled = true;

    private final float maxHitPoints;

    /** Mass of the metal casing no parts, engine, warhead, or fuel */
    public final double mass;

    MissileCasings(WarheadCasings warhead, Class<? extends Missile> missile_clazz, int maxFlightTicks, float maxHitPoints, double mass, boolean enabled)
    {
        this.warhead_casing = warhead;
        this.missile_clazz = missile_clazz;
        this.maxFlightTimeInTicks = maxFlightTicks;
        this.mass = mass;
        this.enabled = enabled;
        this.maxHitPoints = maxHitPoints;
    }

    public ItemStack newModuleStack()
    {
        ItemStack stack = new ItemStack(ICBM.itemMissile, 1, ordinal());
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString(ModuleBuilder.SAVE_ID, "icbm.missile_" + name().toLowerCase());
        return stack;
    }

    public Missile newModule()
    {
        return MissileModuleBuilder.INSTANCE.buildMissile(newModuleStack());
    }

    public static void register()
    {
        for (MissileCasings size : values())
        {
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, "missile_" + size.name().toLowerCase(), size.missile_clazz);
        }
    }

    public float getMaxHitPoints()
    {
        return maxHitPoints;
    }

    /**
     * Gets a missile casing from the metadata given.
     * Basicly an array access with upper and lower limit
     *
     * @param itemDamage - meta data
     * @return casing or small if the itemDamage was invalid
     */
    public static MissileCasings fromMeta(int itemDamage)
    {
        if (itemDamage >= 0 && itemDamage < values().length)
        {
            return MissileCasings.values()[itemDamage];
        }
        return MissileCasings.SMALL;
    }
}
