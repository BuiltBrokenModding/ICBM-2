package icbm.sentry.render;

import icbm.sentry.turret.block.TileTurret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
import calclavia.lib.access.Nodes;
import calclavia.lib.render.item.ISimpleItemRenderer;

/** Container class used to call rendering for a sentry
 * 
 * @author DarkGuardsman */
public abstract class TurretRenderer implements ISimpleItemRenderer
{
    public ResourceLocation textureNeutral;
    public ResourceLocation textureFriendly;
    public ResourceLocation textureHostile;

    public TurretRenderer(ResourceLocation texture)
    {
        this.textureNeutral = texture;
        this.textureFriendly = texture;
        this.textureHostile = texture;
    }

    /** Called by the base sentry container's renderer to render the sentry. This may be called from
     * a Tile, or Entity Renderer. Translation will already be applied though fine tuning is needed.
     * 
     * @param side - side of a block the sentry is on
     * @param tile - TODO this will later be replaced with ISentry and is only temporary
     * @param yaw - yaw of the gun/turret
     * @param pitch - pitch of the gun/turret */
    public abstract void render(ForgeDirection side, TileTurret tile, double yaw, double pitch);

    public ResourceLocation getTexture(EntityPlayer player, TileTurret tile)
    {
        if (tile != null && player != null)
        {
            if (tile.canUse(Nodes.PROFILE_OWNER, player) || tile.canUse(Nodes.PROFILE_ADMIN, player))
                return this.textureFriendly;

            if (tile.canAccess(player.username))
            {
                return textureNeutral;
            }
        }
        return textureHostile;
    }
}
