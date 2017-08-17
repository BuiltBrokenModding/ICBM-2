package com.builtbroken.icbm.client.blast;

import com.builtbroken.icbm.content.blast.power.BlastMicrowave;
import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import net.minecraft.client.Minecraft;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/20/2016.
 */
public class BlastMicrowaveClient extends BlastMicrowave
{
    public BlastMicrowaveClient(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public void doStartDisplay()
    {
        //TODO show wave renderer
    }

    @Override
    public void displayEffectForEdit(IWorldEdit blocks)
    {
        int count = 20;
        if (Minecraft.getMinecraft().gameSettings.particleSetting == 1)
        {
            count = 10;
        }
        else if (Minecraft.getMinecraft().gameSettings.particleSetting == 2)
        {
            count = 3;
        }
        //Spawn particles TODO get steam particles for better effect
        for (int i = 0; i < count; i++)
        {
            world.spawnParticle("smoke", blocks.x() + rn(), blocks.y() + rn(), blocks.z() + rn(), rv(), rv(), rv());
        }
    }

    private final float rn()
    {
        return oldWorld.rand.nextFloat() - oldWorld.rand.nextFloat();
    }

    private final float rv()
    {
        return oldWorld.rand.nextFloat() * .1f;
    }
}
