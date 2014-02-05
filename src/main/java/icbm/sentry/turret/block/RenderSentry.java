package icbm.sentry.turret.block;

import calclavia.lib.render.RenderTaggedTile;
import icbm.sentry.ICBMSentry;
import icbm.sentry.render.SentryRenderer;
import icbm.sentry.turret.SentryRegistry;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Container class for most sentry gun renders of 1^3. Other sentries might need a more custom
 * render approach if they become to complex or large
 *
 * @author DarkGuardsman */
public class RenderSentry extends RenderTaggedTile
{
    //TODO later on we will want to pass most rendering/positioning to the model as the gun will not render correctly without complex translation.
    //The only thing this class should end up doing is basic translation and rotation.
    @Override
    public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
    {
        super.renderTileEntityAt(t, x, y, z, f);
        if (t instanceof TileSentry)
        {
            SentryRenderer sentryRender = SentryRegistry.getRenderFor(((TileSentry) t).getSentry());
            if (sentryRender != null)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float) x + 0.5F, (float) y + 2.2F, (float) z + 0.5F);
                GL11.glScalef(1.5f, 1.5f, 1.5f);

                this.bindTexture(sentryRender.getTexture(this.getPlayer(), (TileSentry) t));
                sentryRender.render(((TileSentry) t).getDirection(), (TileSentry) t, ((TileSentry) t).getYawServo().getRotation(), ((TileSentry) t).getPitchServo().getRotation());

                GL11.glPopMatrix();
            }
        }
    }

    @Override
    protected void bindTexture (ResourceLocation par1ResourceLocation)
    {
        super.bindTexture(par1ResourceLocation);
    }
}
