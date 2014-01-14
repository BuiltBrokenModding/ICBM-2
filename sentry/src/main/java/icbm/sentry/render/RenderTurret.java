package icbm.sentry.render;

import icbm.sentry.turret.TileTurret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import calclavia.lib.access.Nodes;

public abstract class RenderTurret extends RenderTaggedTile
{
    public ResourceLocation TEXTURE_FILE;
    public ResourceLocation TEXTURE_FILE_FRIENDLY;
    public ResourceLocation TEXTURE_FILE_HOSTILE;

    public void setTextureBaseOnState(TileTurret tileEntity)
    {
        EntityPlayer player = this.getPlayer();

        if (tileEntity.getPlatform() != null)
        {

            if (tileEntity.getPlatform().canUse(Nodes.GROUP_USER_NODE, player))
            {
                this.bindTexture(TEXTURE_FILE);
                return;
            }
            else if (tileEntity.getPlatform().canUse(Nodes.GROUP_USER_NODE, player))
            {
                this.bindTexture(TEXTURE_FILE_FRIENDLY);
                return;
            }
        }

        this.bindTexture(TEXTURE_FILE_HOSTILE);
    }
}
