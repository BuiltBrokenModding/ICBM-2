package com.builtbroken.icbm.content.blast.explosive;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.jlib.lang.StringHelpers;
import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.init.Blocks;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/13/2015.
 */
public class BlastPathTester extends BlastSimplePath<BlastPathTester>
{

    public BlastPathTester(IExplosiveHandler handler)
    {
        super(handler);
        this.recursive = false;
    }

    @Override
    public BlockEdit changeBlock(BlockPos location)
    {
        return new BlockEdit(oldWorld, location).set(Blocks.air, 0, false, true);
    }

    @Override
    public void getEffectedBlocks(List<IWorldEdit> list)
    {
        int blocks = (int) ((4 / 3) * Math.PI * size * size * size);
        ICBM.INSTANCE.logger().info(this + " GetEffectedBlocks(l)");
        ICBM.INSTANCE.logger().info("\tMax Blocks: " + blocks);
        long time = System.nanoTime();
        lastUpdate = time;
        super.getEffectedBlocks(list);
        time = System.nanoTime() - time;
        ICBM.INSTANCE.logger().info("\tEntries: " + list.size());
        ICBM.INSTANCE.logger().info("\tTime: " + StringHelpers.formatNanoTime(time));
    }

    @Override
    public String toString()
    {
        return "BlastPathTester@" + hashCode() + "[ Size:" + size + "  Center: " + blockCenter + "]";
    }
}
