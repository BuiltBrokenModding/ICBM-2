package icbm.sentry;

import net.minecraft.tileentity.TileEntity;
import universalelectricity.api.vector.Vector3;

/** Applied to all turret TileEntities. */
public interface ISentry extends IWeaponPlatform
{
    /** Set the sentry guns new rotation. This will be updated over time. */
    public void setRotation(float yaw, float pitch);

    /** Gets the turret platform. */
    public TileEntity getPlatform();
}
