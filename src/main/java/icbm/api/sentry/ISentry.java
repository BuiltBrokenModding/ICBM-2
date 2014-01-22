package icbm.api.sentry;

import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Applied to any object that will be loaded into a sentry tile
 * 
 * @author Darkguardsman */
public interface ISentry extends IWeaponPlatform
{
    /** Hp of the sentry, -1 is used for no hp */
    public int getMaxHp();

    /** Energy the sentry consumes per tick */
    public long getEnergyPerTick();

    /** Max energy the sentry's battery can store */
    public long getEnergyCapacity();

    /** Voltage input this tile accepts */
    public long voltage();

    /** Called client side after the gun has fired. Use this to trigger render, and audio events */
    public void onFireClient();

    /** Offset from center of were the barrel ends */
    public Vector3 getAimOffset();

    /** Offset from the block's corrds were the center actualy is */
    public Vector3 getCenterOffset();

    @SideOnly(Side.CLIENT)
    public ISentryRender getRender();

}
