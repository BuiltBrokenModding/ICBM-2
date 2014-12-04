package icbm.explosion.explosive;

import icbm.api.explosion.TriggerCause;
import icbm.explosion.Blast;
import icbm.explosion.blast.BlastSided;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import resonant.lib.world.IWorldChangeAction;

/**
 * Created by robert on 12/3/2014.
 */
public class ExplosiveSided extends Explosive
{
    EnumFacing face;

    public ExplosiveSided(String name, EnumFacing face)
    {
        super(name, null);
        this.face = face;
    }

    @Override
    public IWorldChangeAction createBlastForTrigger(World world, double x, double y, double z, TriggerCause cause, int yieldMultiplier)
    {
        return new BlastSided().setDirection(face).setLocation(world, (int)x, (int)y, (int)z).setCause(cause).setYield(yieldMultiplier * 5);
    }
}
