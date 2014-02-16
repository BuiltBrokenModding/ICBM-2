package icbm.sentry.interfaces;

import net.minecraft.world.World;

/** Used to interact with any container object that can host a sentry
 * 
 * @author Darkguardsman */
public interface ISentryContainer
{
    public ISentry getSentry();
    
    public World world();
    
    public double x();
    
    public double y();
    
    public double z();
    
    public float yaw();
    
    public float pitch();
}
