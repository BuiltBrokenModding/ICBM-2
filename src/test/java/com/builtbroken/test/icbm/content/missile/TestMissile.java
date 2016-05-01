package com.builtbroken.test.icbm.content.missile;

import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.builtbroken.mc.testing.junit.world.FakeWorld;
import net.minecraft.world.World;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/1/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestMissile extends AbstractTest
{
    @Test
    public void testToString()
    {
        World world = FakeWorld.newWorld("missileToString");
        EntityMissile missile = new EntityMissile(world);
        String s = missile.toString(); //Test for no crash
    }
}
