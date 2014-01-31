package icbm.sentry.render;

import net.minecraftforge.common.ForgeDirection;

/** Container class used to call rendering for a sentry
 * 
 * @author DarkGuardsman */
public abstract class RenderSentry
{
    /** Called by the base sentry container's renderer to render the sentry. This may be called from
     * a Tile, Item, or Entity Renderer */
    public abstract void render(ForgeDirection side, float yaw, float pitch);
}
