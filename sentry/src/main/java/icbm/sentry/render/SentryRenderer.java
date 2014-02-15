package icbm.sentry.render;

import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.block.TileSentry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
import calclavia.lib.access.Nodes;

/** Container class used to call rendering for a sentry
 * 
 * @author DarkGuardsman */
public abstract class SentryRenderer
{
    public ResourceLocation TEXTURE_FILE;
    public ResourceLocation TEXTURE_FILE_FRIENDLY;
    public ResourceLocation TEXTURE_FILE_HOSTILE;

    public SentryRenderer(ResourceLocation texture)
    {
        this.TEXTURE_FILE = texture;
        this.TEXTURE_FILE_FRIENDLY = texture;
        this.TEXTURE_FILE_HOSTILE = texture;
    }

    /** Called by the base sentry container's renderer to render the sentry. This may be called from
     * a Tile, or Entity Renderer. Translation will already be applied though fine tuning is
     * needed.
     * 
     * @param side - side of a block the sentry is on
     * @param tile - TODO this will later be replaced with ISentry and is only temporary
     * @param yaw - yaw of the gun/turret
     * @param pitch - pitch of the gun/turret */
    public abstract void render(ForgeDirection side, TileSentry tile, float yaw, float pitch);
    
    public abstract void renderItem();

    public ResourceLocation getTexture(EntityPlayer player, TileSentry tileEntity)
    {
        if (tileEntity != null && player != null)
        {
            //ICBMSentry.LOGGER.info("Getting sentry Texture:");
            if (tileEntity.canUse(Nodes.GROUP_USER_NODE, player))
            {
                //ICBMSentry.LOGGER.info("Returning default Texture:");
                return TEXTURE_FILE;
            }
            else if (tileEntity.canUse(Nodes.GROUP_USER_NODE, player))
            {
                //ICBMSentry.LOGGER.info("Returning Friendly Texture:");
                return TEXTURE_FILE_FRIENDLY;
            }
        }
        //ICBMSentry.LOGGER.info("Returning Hostile Texture:");
        return TEXTURE_FILE_HOSTILE;
    }
}
