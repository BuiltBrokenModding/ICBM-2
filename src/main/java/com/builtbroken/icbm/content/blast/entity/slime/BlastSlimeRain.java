package com.builtbroken.icbm.content.blast.entity.slime;

import com.builtbroken.icbm.api.blast.IBlastTileMissile;
import com.builtbroken.icbm.api.missile.ITileMissile;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public class BlastSlimeRain extends Blast<BlastSlimeRain> implements IBlastTileMissile
{
    private int ticks = 0;
    private int duration = 1000;

    public BlastSlimeRain(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public void tickBlast(ITileMissile tile, IMissile missile)
    {
        //TODO set duration to size value
        //TODO render laser into sky while active
        //TODO make sky glow rainbow colors
        if (ticks == 0)
        {
            duration = (int) size * duration;
        }
        if (ticks++ % 5 == 0)
        {
            //TODO add spawn limit to prevent lag
            if (!world.isRemote)
            {
                EulerAngle angle = new EulerAngle(world.rand.nextFloat() * 360, 0 + 90 * world.rand.nextFloat());
                Location pos = new Location(world, angle.toPos().multiply(10).add(x, y + 70, z));
                if (pos.isChunkLoaded())
                {
                    Chunk chunk = pos.getChunk();
                    int count = 0;
                    for (List list : chunk.entityLists)
                    {
                        if (list != null)
                        {
                            count += list.size();
                        }
                    }
                    if (count < 30)
                    {
                        //TODO pick random location
                        EntitySlimeRain entity = new EntitySlimeRain(world);
                        entity.setSlimeSize(1);
                        entity.setPosition(pos.x(), pos.y(), pos.z());
                        world.spawnEntityInWorld(entity);
                    }
                    else
                    {
                        ticks -= 5;
                    }
                }
                else
                {
                    ticks -= 5;
                }
            }
        }
        if (ticks >= duration)
        {
            killAction(false);
        }
    }

    @Override
    public boolean isCompleted()
    {
        return super.isCompleted() || ticks >= duration;
    }
}
