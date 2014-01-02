package icbm.sentry.interfaces;

import net.minecraft.tileentity.TileEntity;
import universalelectricity.api.vector.Vector3;

/** Applied to all turret TileEntities. */
public interface ISentry extends IWeaponPlatform, IGyroMotor
{
    /** Gets the turret platform. */
    public TileEntity getPlatform();
}
