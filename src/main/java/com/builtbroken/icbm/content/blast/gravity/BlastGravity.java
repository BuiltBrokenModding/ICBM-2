package com.builtbroken.icbm.content.blast.gravity;

import com.builtbroken.icbm.api.blast.IBlastTileMissile;
import com.builtbroken.icbm.api.missile.ITileMissile;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public class BlastGravity extends Blast<BlastGravity> implements IBlastTileMissile
{
    private int ticks = 0;
    private int duration = 20 * 15;
    private AxisAlignedBB bounds;

    public BlastGravity(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public BlastGravity setYield(double size)
    {
        this.size = size;
        updateBounds();
        return this;
    }

    public BlastGravity setLocation(final World world, double x, double y, double z)
    {
        super.setLocation(world, x, y, z);
        updateBounds();
        return this;
    }

    private void updateBounds()
    {
        bounds = new Cube(x - size, y - size, z - size, x + size, y + size, z + size).toAABB();
    }

    @Override
    public void tickBlast(ITileMissile tile, IMissile missile)
    {
        //TODO add render effect to emulate a tractor beam
        ticks++;

        if (!world.isRemote)
        {
            List<Entity> list = world.getEntitiesWithinAABB(Entity.class, bounds);
            for (Entity entity : list)
            {
                entity.addVelocity(0, 0.1, 0);
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
