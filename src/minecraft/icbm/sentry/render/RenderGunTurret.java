package icbm.sentry.render;

import icbm.sentry.ICBMSentry;
import icbm.sentry.model.ModelGunTurret;
import icbm.sentry.terminal.AccessLevel;
import icbm.sentry.turret.TileEntityBaseTurret;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGunTurret extends RenderTaggedTile
{
	public static final String TEXTURE_FILE = "gun_turret_t1.png";
	public static final ModelGunTurret MODEL = new ModelGunTurret();

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		super.renderTileEntityAt(t, x, y, z, f);

		if (t instanceof TileEntityBaseTurret)
		{
			TileEntityBaseTurret tileEntity = (TileEntityBaseTurret) t;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5f, (float) y + 1f, (float) z + 0.5f);
			GL11.glScalef(0.7f, 0.7f, 0.7f);
			// Bind texture TODO change this to render different textures based on state and player.
			this.setTextureBaseOnState(tileEntity);
			this.render(tileEntity.renderRotationYaw, tileEntity.renderRotationPitch);

			GL11.glPopMatrix();
		}
	}

	public static void render(float renderPitch, float renderYaw)
	{
		GL11.glRotatef(180F, 0F, 0F, 1F);
		GL11.glRotatef(180F, 0F, 1F, 0F);
		MODEL.render(0.0625F);
		// Render base rotation
		GL11.glRotatef(renderYaw, 0F, 1F, 0F);
		MODEL.renderBody(0.0625F);
		// Render gun rotation
		GL11.glRotatef(renderPitch, 1F, 0F, 0F);
		MODEL.renderCannon(0.0625F);
	}

	public void setTextureBaseOnState(TileEntityBaseTurret tileEntity)
	{
		EntityPlayer player = this.getPlayer();
		if (player != null && player.getDistance(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) <= RenderPlayer.NAME_TAG_RANGE && tileEntity.getPlatform() != null)
		{
			AccessLevel level = tileEntity.getPlatform().getPlayerAccess(player);

			if (level == AccessLevel.OPERATOR)
			{
				this.bindTextureByName(ICBMSentry.TEXTURE_PATH + TEXTURE_FILE);
			}
			else if (level.ordinal() >= AccessLevel.USER.ordinal())
			{
				// TODO add friendly texture
				this.bindTextureByName(ICBMSentry.TEXTURE_PATH + TEXTURE_FILE);
			}
			else
			{
				// TODO add hostile texture
				this.bindTextureByName(ICBMSentry.TEXTURE_PATH + TEXTURE_FILE);
			}
		}
		else
		{
			// TODO add neutral texture
			this.bindTextureByName(ICBMSentry.TEXTURE_PATH + TEXTURE_FILE);
		}
	}
}