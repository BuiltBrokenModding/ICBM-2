package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/2/2016.
 */
public enum Fragments
{
    ARROW(new FragmentGen()
    {
        @Override
        public double scaleData(double velocity)
        {
            return 0;
        }

        @Override
        public Blast getBlast(NBTTagCompound tag)
        {
            return new BlastArrows();
        }
    }),
    COBBLESTONE(new FragmentGen()
    {
        @Override
        public double scaleData(double velocity)
        {
            return Math.max(Math.min(0, velocity * 5), 10);
        }

        @Override
        public Blast getBlast(NBTTagCompound tag)
        {
            return null;
        }
    }),
    WOOD(new FragmentGen()
    {
        @Override
        public double scaleData(double velocity)
        {
            return Math.max(Math.min(0, velocity * 5), 10);
        }

        @Override
        public Blast getBlast(NBTTagCompound tag)
        {
            return null;
        }
    }),
    BLAZE(new FragmentGen()
    {
        @Override
        public double scaleData(double velocity)
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

    Fragments(FragmentGen gen)
    {
        this.gen = gen;
    }

    public Blast newBlast(NBTTagCompound tag)
    {
        return gen.getBlast(tag);
    }


    private interface FragmentGen
    {
        double scaleData(double velocity);

        Blast getBlast(NBTTagCompound tag);
    }
}
