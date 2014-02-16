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

/**
 * Container class for most sentry gun renders of 1^3. Other sentries might need a more custom
 * render approach if they become to complex or large
 * 
 * @author DarkGuardsman
 */
public class RenderTurret extends RenderTaggedTile
{
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f)
	{
		super.renderTileEntityAt(tile, x, y, z, f);

		if (tile instanceof TileTurret)
		{
			SentryRenderer sentryRender = SentryRegistry.getRenderFor(((TileTurret) tile).getSentry());

			if (sentryRender != null)
			{
				GL11.glPushMatrix();
				GL11.glTranslatef((float) x, (float) y, (float) z);
				GL11.glScalef(1f, 1f, 1f);

				RenderUtility.bind(sentryRender.getTexture(getPlayer(), (TileTurret) tile));
				sentryRender.render(((TileTurret) tile).getDirection(), (TileTurret) tile, ((TileTurret) tile).getYawServo().getRotation(), ((TileTurret) tile).getPitchServo().getRotation());

				GL11.glPopMatrix();
			}
		}
	}
}
