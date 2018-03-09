package com.builtbroken.icbm.content.blast.builder;

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
 *
 * Actual version will generate a sphere of glass around a fixed point at 1/10 the size of the warhead.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2018.
 */
public class BlastProxyGlass extends Blast<BlastProxyGlass>
{
    public BlastProxyGlass(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public void getEffectedBlocks(final List<IWorldEdit> list)
    {
        final Pos center = toPos();
        double size = getYield();
        int sphereSize = (int) Math.floor(size / 10.0);

        //Generate glass
        //Credit https://www.reddit.com/r/VoxelGameDev/comments/2cttnt/how_to_create_a_sphere_out_of_voxels/
        for (int tx = -sphereSize; tx < sphereSize + 1; tx++)
        {
            for (int ty = -sphereSize; ty < sphereSize + 1; ty++)
            {
                for (int tz = -sphereSize; tz < sphereSize + 1; tz++)
                {
                    if (Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2)) <= sphereSize - 2)
                    {
                        BlockEdit edit = new BlockEdit(oldWorld, center.add(tx, ty, tz));
                        edit.set(Blocks.glass);
                        list.add(edit);
                    }
                    else
                    {
                        //TODO destroy everything
                    }
                }
            }
        }

    }
}
