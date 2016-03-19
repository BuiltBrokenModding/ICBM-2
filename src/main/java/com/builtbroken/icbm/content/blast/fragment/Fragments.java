package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/2/2016.
 */
public enum Fragments
{
    ARROW("ex.icon.fragment.arrow", "fragment.background.arrow", new FragmentGen()
    {
        @Override
        public float scaleDamage(float velocity)
        {
            return 0;
        }

        @Override
        public Blast getBlast(NBTTagCompound tag)
        {
            return new BlastArrows();
        }
    }),
    COBBLESTONE("ex.icon.fragment.cobblestone", "fragment.background.cobblestone", new FragmentGen()
    {
        @Override
        public float scaleDamage(float velocity)
        {
            return Math.max(Math.min(0, velocity * 5), 10);
        }

        @Override
        public Blast getBlast(NBTTagCompound tag)
        {
            return null;
        }
    }),
    WOOD("ex.icon.fragment.wood", "fragment.background.wood", new FragmentGen()
    {
        @Override
        public float scaleDamage(float velocity)
        {
            return Math.max(Math.min(0, velocity * 5), 10);
        }

        @Override
        public Blast getBlast(NBTTagCompound tag)
        {
            return null;
        }
    }),
    BLAZE("ex.icon.fragment.blaze", "fragment.background.blaze", new FragmentGen()
    {
        @Override
        public float scaleDamage(float velocity)
        {
            return 0;
        }

        @Override
        public Blast getBlast(NBTTagCompound tag)
        {
            return null;
        }
    });

    private final FragmentGen gen;
    public final String icon_name;
    public final String corner_icon_name;

    Fragments(String corner_icon_name, String icon_name, FragmentGen gen)
    {
        this.icon_name = icon_name;
        this.corner_icon_name = corner_icon_name;
        this.gen = gen;
    }

    /**
     * Gets a new blast
     *
     * @param tag - save data
     * @return new blast, never null
     */
    public Blast newBlast(NBTTagCompound tag)
    {
        return gen.getBlast(tag);
    }

    /**
     * Modifies the damage based on speed
     *
     * @param velocity
     * @return
     */
    public float scaleDamage(float velocity)
    {
        return gen.scaleDamage(velocity);
    }


    private interface FragmentGen
    {
        float scaleDamage(float velocity);

        Blast getBlast(NBTTagCompound tag);
    }
}
