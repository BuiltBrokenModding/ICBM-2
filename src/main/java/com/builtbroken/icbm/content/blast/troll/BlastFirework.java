package com.builtbroken.icbm.content.blast.troll;

import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.framework.explosive.blast.Blast;
import com.builtbroken.mc.framework.thread.delay.DelayedActionHandler;
import com.builtbroken.mc.framework.thread.delay.DelayedSpawn;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/31/2017.
 */
public class BlastFirework extends Blast<BlastFirework>
{
    private final ItemStack firework;

    public BlastFirework(IExplosiveHandler handler, ItemStack firework)
    {
        super(handler);
        this.firework = firework;
    }

    @Override
    public int shouldThreadAction()
    {
        return NO_THREAD;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (!beforeBlocksPlaced)
        {
            for (int i = 0; i < 10; i++)
            {
                EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(oldWorld, x(), y(), z(), firework);
                DelayedActionHandler.add(new DelayedSpawn(oldWorld, toPos(), entityfireworkrocket, 5, 1 + MathHelper.rand.nextInt(100)));
            }
        }
    }
}
