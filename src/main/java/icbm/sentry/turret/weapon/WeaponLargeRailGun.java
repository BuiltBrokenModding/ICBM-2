package icbm.sentry.turret.weapon;

import icbm.sentry.turret.sentryHandlers.Sentry;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;

public class WeaponLargeRailGun extends WeaponSystemProjectile
{

    public WeaponLargeRailGun(Sentry sentry)
    {
        super(sentry);
    }

    @Override
    public void fire(VectorWorld target)
    {

    }

}
