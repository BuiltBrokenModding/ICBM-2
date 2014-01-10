package icbm.sentry.render;

import icbm.sentry.turret.TileEntityTurret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public abstract class RenderTurret extends RenderTaggedTile
{
    public ResourceLocation TEXTURE_FILE;
    public ResourceLocation TEXTURE_FILE_FRIENDLY;
    public ResourceLocation TEXTURE_FILE_HOSTILE;

    public void setTextureBaseOnState(TileEntityTurret tileEntity)
    {
        EntityPlayer player = this.getPlayer();

        if (tileEntity.getPlatform() != null)
        {

            if (tileEntity.getPlatform().getOwnerGroup().isMemeber(player.username))
            {
                this.bindTexture(TEXTURE_FILE);
                return;
            }
            else if (tileEntity.getPlatform().getUserAccess(player.username) != null)
            {
                this.bindTexture(TEXTURE_FILE_FRIENDLY);
                return;
            }
        }

        this.bindTexture(TEXTURE_FILE_HOSTILE);
    }
}
