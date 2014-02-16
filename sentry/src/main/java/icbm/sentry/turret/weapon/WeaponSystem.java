package icbm.sentry.turret.weapon;

import icbm.sentry.turret.Sentry;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AABBPool;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;

import java.util.List;

/** Modular way of dealing with weapon systems in a way works with different object types
 *
 * @author DarkGuardsman, tgame14 */
public abstract class WeaponSystem
{
    protected Sentry sentry;
    protected double range;
    protected List<Entity> entityList;

    public WeaponSystem (Sentry sentry)
    {
        this.sentry = sentry;
        this.range = 32;
    }

    /** Fire the weapon at a location */
    public abstract void fire (VectorWorld target);

    /** Fire the weapon at an entity. */
    public void fire (Entity entity)
    {
        this.fire((VectorWorld) new Vector3(entity).translate(0, entity.getEyeHeight(), 0));
    }
}
