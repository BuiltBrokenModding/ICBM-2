package icbm.sentry.interfaces;

import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.IVectorWorld;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

/** Implement this on any object that hosts a turret.
 * 
 * @author Darkguardsman, Calclavia */
public interface ITurretProvider extends IVectorWorld
{
    /** Gets the sentry hosted by this container */
    public ITurret getTurret();

    public IInventory getInventory();    

    public void sendFireEventToClient(IVector3 target);
}
