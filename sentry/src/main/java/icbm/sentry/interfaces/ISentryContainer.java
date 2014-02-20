package icbm.sentry.interfaces;

import calclavia.lib.prefab.IGyroMotor;
import universalelectricity.api.vector.Vector3;
import net.minecraft.world.World;

/** Used to interact with any container object that can host a sentry
 * 
 * @author Darkguardsman */
public interface ISentryContainer extends IGyroMotor
{
    /** Gets the sentry hosted by this container */
    public ISentry getSentry();

    /** Called to send the fire event to the client for rendering & sound */
    public void sendFireEventToClient(Vector3 target);

    /* 8888888888888888888888888888888888
     * These method should call to the Entity or TileEntity existing data. 
     * For TileEntity try to center the xyz coords
     * 8888888888888888888888888888888888*/

    public World world();

    public double x();

    public double y();

    public double z();

    public float yaw();

    public float pitch();
}
