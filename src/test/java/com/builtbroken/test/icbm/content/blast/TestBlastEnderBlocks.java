package com.builtbroken.test.icbm.content.blast;

import com.builtbroken.icbm.content.blast.effect.BlastEnderBlocks;
import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.builtbroken.mc.testing.junit.world.FakeWorld;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/17/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestBlastEnderBlocks extends AbstractTest
{
    @Test
    public void testRequiredFunctionality()
    {
        //Test internal function calls for functionality
        //As these calls not working will break other methods

        //Test distance checks
        Pos pos = new Pos(.5, .5, .5);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            assertEquals(1.0, pos.distance(new BlockEdit(null, dir.offsetX + 0.5, dir.offsetY + 0.5, dir.offsetZ + 0.5)));
        }
        //Test contains
        List<BlockEdit> pList = new ArrayList();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            BlockEdit d = new BlockEdit(null, dir.offsetX, dir.offsetY, dir.offsetZ);
            assertFalse(pList.contains(d));
            pList.add(d);
        }
    }

    @Test
    public void testBlast()
    {
        World world = FakeWorld.newWorld("testBlastEnderBlocks");
        for (int x = -10; x <= 10; x++)
        {
            for (int z = -10; z <= 10; z++)
            {
                for (int y = 0; y <= 30; y++)
                {
                    world.setBlock(x, y, z, Blocks.dirt);
                }
            }
        }
        //Test Size 1
        checkBlast(world, 0, 30, 0, 1, 6, 6);
        checkBlast(world, 0, 10, 0, 1, 7, 7);
    }

    private final List<IWorldEdit> checkBlast(World world, int x, int y, int z, int size, int dirt, int air)
    {
        List<IWorldEdit> edits = popBlast(world, x, y, z, size);
        checkValues(edits, air, dirt);
        return edits;
    }

    private final List<IWorldEdit> popBlast(World world, int x, int y, int z, int size)
    {
        BlastEnderBlocks blast = new BlastEnderBlocks();
        blast.setLocation(world, x, y, z);
        blast.setYield(size);
        List<IWorldEdit> edits = new ArrayList();
        blast.getEffectedBlocks(edits);
        return edits;
    }

    private final void checkValues(List<IWorldEdit> edits, int expectedAir, int expectedDirt)
    {
        assertEquals(expectedAir + expectedDirt, edits.size());
        int air = 0;
        int dirt = 0;
        for (IWorldEdit edit : edits)
        {
            if (edit.getNewBlock() == Blocks.air)
            {
                air++;
            }
            if (edit.getNewBlock() == Blocks.dirt)
            {
                dirt++;
            }
        }
        assertSame(expectedAir, air);
        assertSame(expectedDirt, dirt);
    }
}
