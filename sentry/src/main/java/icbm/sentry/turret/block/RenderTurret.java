package icbm.sentry.turret.block;

import calclavia.lib.render.RenderTaggedTile;
import calclavia.lib.render.RenderUtility;
import icbm.sentry.ICBMSentry;
import icbm.sentry.render.SentryRenderGunTurret;
import icbm.sentry.render.SentryRenderer;
import icbm.sentry.turret.SentryRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/** Container class for most sentry gun renders of 1^3. Other sentries might need a more custom
 * render approach if they become to complex or large
 * 
 * @author DarkGuardsman */
public class RenderTurret extends RenderTaggedTile
{
    private static final SentryRenderer backup_render = new SentryRenderGunTurret();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f)
    {
        super.renderTileEntityAt(tile, x, y, z, f);

        if (tile instanceof TileTurret)
        {
            TileTurret tileTurret = (TileTurret) tile;
            SentryRenderer sentryRender = SentryRegistry.getRenderFor(tileTurret.getSentry());
            if (sentryRender == null)
            {
                sentryRender = backup_render;
            }
            if (sentryRender != null)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float) x, (float) y, (float) z);
                GL11.glScalef(1f, 1f, 1f);

                RenderUtility.bind(sentryRender.getTexture(getPlayer(), tileTurret));
                sentryRender.render(tileTurret.getDirection(), tileTurret, tileTurret.yaw(), tileTurret.pitch());

                GL11.glPopMatrix();
            }
        }
    }
}
