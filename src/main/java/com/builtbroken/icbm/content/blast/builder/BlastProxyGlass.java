package com.builtbroken.icbm.content.blast.builder;

import com.builtbroken.mc.api.edit.IWorldChangeLayeredAction;
import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.framework.explosive.blast.Blast;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import net.minecraft.init.Blocks;

import java.util.List;

/**
 * Requested by ProxyNeko on MMD discord
 * <p>
 * * -----------
 * Request
 *
 * @DarkCow Idea for you but I like it so plz do lol Biosphere nuke that keeps everything in a 11x11 chunk area
 * (filled with a glass sphere that's hollow with whatever's left inside intact) and everything outside the
 * sphere for 2 chunks on all sides is fucked(edited)
 * * -----------
 * <p>
 * Actual version will generate a sphere of glass around a fixed point at 1/10 the size of the warhead.
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2018.
 */
public class BlastProxyGlass extends Blast<BlastProxyGlass> implements IWorldChangeLayeredAction
{
    public BlastProxyGlass(IExplosiveHandler handler)
    {
        super(handler);
    }

    private int layers;
    private int sphereSize;
    private int sizeInt;

    @Override
    public BlastProxyGlass setYield(double size)
    {
        double prev = this.size;
        super.setYield(Math.floor(size));
        if (prev != size)
        {
            calculateLayers();
        }
        return this;
    }

    public void calculateLayers()
    {
        sizeInt = (int) Math.floor(getYield());
        sphereSize = sizeInt / 2;
        layers = sizeInt * 2 + 1; //Number of y layers
    }

    @Override
    public int getLayers()
    {
        return layers;
    }

    @Override
    public boolean shouldContinueAction(int layer)
    {
        return !killExplosion || layer > getLayers();
    }

    @Override
    public int shouldThreadAction()
    {
        return INSTANT_THREAD;
    }

    @Override
    public void getEffectedBlocks(List<IWorldEdit> list, int layer)
    {
        final Pos center = toPos();
        int ty = layer - sizeInt;
        for (int tx = -sizeInt; tx < sizeInt + 1; tx++)
        {
            for (int tz = -sizeInt; tz < sizeInt + 1; tz++)
            {
                double distance = Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2));
                if (distance <= sphereSize)
                {
                    //Make glass edge
                    if (distance >= sphereSize - 2)
                    {
                        BlockEdit edit = new BlockEdit(oldWorld, center.add(tx, ty, tz));
                        edit.set(Blocks.glass);
                        list.add(edit);
                    }
                    else
                    {
                        //Leave everything inside range alone
                    }
                }
                //Destroy everything outside of range
                else if(distance < sizeInt + 1)
                {
                    list.add(new BlockEdit(oldWorld, center.add(tx, ty, tz)).setAir());
                }
            }
        }
    }
}
