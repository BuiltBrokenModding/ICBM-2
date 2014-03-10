package icbm.sentry.turret.mounted;

import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponRailgun;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.fake.IMultiBlock;

/** Railgun
 * 
 * @author Calclavia */
public class MountedRailgun extends TurretMounted implements IMultiBlock
{
    private int powerUpTicks = -1;

    public MountedRailgun(TileTurret turretProvider)
    {
        super(turretProvider);
        energy = new EnergyStorageHandler(10000000000L);
        riderOffset = new Vector3(0, 0.2, 0);
        maxCooldown = 20 * 10;
        weaponSystem = new WeaponRailgun(this);
    }

    @Override
    public void update()
    {
        super.update();
        if (!world().isRemote && powerUpTicks >= 0)
        {
            powerUpTicks++;
            if (powerUpTicks++ >= 70)
                fire();
        }
    }

    @Override
    public void fire()
    {
        super.fire();
        powerUpTicks = -1;
    }

    @Override
    public void onRedstone()
    {
        powerUpTicks = 0;
    }

    @Override
    public Vector3[] getMultiBlockVectors()
    {
        return new Vector3[] { new Vector3(0, 1, 0) };
    }
}
