package icbm.api.sentry;

/** Applied to all turret TileEntities that host a sentry instance.
 * 
 * @author Darkguardsman */
public interface ISentryTile extends IGyroMotor
{
    /** Gets the ISentry instance that defines most properties of the sentry gun */
    ISentry getSentry();

    /** Gets the current HP of the turret */
    float getHealth();
}
