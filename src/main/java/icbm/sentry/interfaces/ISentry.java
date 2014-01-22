package icbm.sentry.interfaces;

import universalelectricity.api.vector.Vector3;
import icbm.api.sentry.IGyroMotor;
import icbm.api.sentry.IWeaponPlatform;
import net.minecraft.tileentity.TileEntity;

/** Applied to all turret TileEntities. */
public interface ISentry extends IWeaponPlatform, IGyroMotor
{
    /** Gets the turret platform. */
    public TileEntity getPlatform();

    /** Gets the distance from the center of the sentry to were the barrel ends as a xyz vector */
    public Vector3 getAimOffset();
}
