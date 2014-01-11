package icbm.sentry.interfaces;

import icbm.api.sentry.IGyroMotor;
import icbm.api.sentry.IWeaponPlatform;
import net.minecraft.tileentity.TileEntity;

/** Applied to all turret TileEntities. */
public interface ISentry extends IWeaponPlatform, IGyroMotor
{
	/** Gets the turret platform. */
	public TileEntity getPlatform();
}
