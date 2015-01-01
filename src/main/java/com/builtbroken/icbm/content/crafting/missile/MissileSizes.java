package com.builtbroken.icbm.content.crafting.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.casing.*;
import com.builtbroken.icbm.content.crafting.missile.warhead.*;
import net.minecraft.item.ItemStack;
import resonant.api.explosive.IExplosive;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by robert on 12/28/2014.
 */
public enum MissileSizes
{
    MICRO(WarheadMicro.class, MissileMicro.class),
    SMALL(WarheadSmall.class, MissileSmall.class),
    STANDARD(WarheadStandard.class, MissileStandard.class),
    MEDIUM(WarheadMedium.class, MissileMedium.class),
    LARGE(WarheadLarge.class, MissileLarge.class);

    public final Class< ? extends Warhead> warhead_clazz;
    public final Class< ? extends Missile> missile_clazz;

    private MissileSizes(Class< ? extends Warhead> warhead_clazz, Class< ? extends Missile> missile_clazz)
    {
        this.warhead_clazz = warhead_clazz;
        this.missile_clazz = missile_clazz;
    }

    //================================
    // Missile creation helpers
    //================================
    public ItemStack getMissileStack()
    {
        return new ItemStack(ICBM.itemMissile, 1, ordinal());
    }

    public Missile getMissile()
    {
        return getMissile(getMissileStack());
    }

    public static Missile loadMissile(ItemStack stack)
    {
        if(stack.getItemDamage() < values().length)
        {
            return values()[stack.getItemDamage()].getMissile(stack);
        }
        return null;
    }

    public Missile getMissile(ItemStack stack)
    {
        try
        {
            return missile_clazz.getConstructor(ItemStack.class).newInstance(stack);
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //================================
    // Warhead creation helpers
    //================================
    public ItemStack getWarheadStack()
    {
        return new ItemStack(ICBM.blockExplosive, 1, ordinal());
    }

    public Warhead getWarhead()
    {
        return getWarhead(getWarheadStack());
    }

    public Warhead getWarhead(IExplosive ex)
    {
        return getWarhead(getWarheadStack(), ex);
    }

    public Warhead getWarhead(ItemStack stack, IExplosive ex)
    {
        Warhead warhead = getWarhead(stack);
        warhead.ex = ex;
        return warhead;
    }

    public static Warhead loadWarhead(ItemStack stack)
    {
        if(stack.getItemDamage() < values().length)
        {
            return values()[stack.getItemDamage()].getWarhead(stack);
        }
        return null;
    }

    public Warhead getWarhead(ItemStack stack)
    {
        try
        {
            return warhead_clazz.getConstructor(ItemStack.class).newInstance(stack);
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
