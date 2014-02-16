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
public class RenderSentry extends RenderTaggedTile
{
	// TODO later on we will want to pass most rendering/positioning to the model as the gun will
	// not render correctly without complex translation.
	// The only thing this class should end up doing is basic translation and rotation.
	private static SentryRenderer backup_render = new SentryRenderGunTurret();

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f)
	{
		// Seems like TESR is not even spawning for tile entity.
		System.out.println("TESR Rendering");
		super.renderTileEntityAt(tile, x, y, z, f);
		
		if (tile instanceof TileSentry)
		{
			SentryRenderer sentryRender = SentryRegistry.getRenderFor(((TileSentry) tile).getSentry());
			if (sentryRender == null)
			{
				sentryRender = backup_render;
			}
			System.out.println("Sentry: " + ((TileSentry) tile).getSentry() + " Renderer: " + sentryRender.getClass().getName());
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x, (float) y, (float) z);
			GL11.glScalef(1f, 1f, 1f);

			RenderUtility.bind(sentryRender.getTexture(this.getPlayer(), (TileSentry) tile));
			sentryRender.render(((TileSentry) tile).getDirection(), (TileSentry) tile, ((TileSentry) tile).getYawServo().getRotation(), ((TileSentry) tile).getPitchServo().getRotation());

			GL11.glPopMatrix();
		}
	}
}
