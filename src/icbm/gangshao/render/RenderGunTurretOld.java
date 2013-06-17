package icbm.gangshao.render;

import icbm.core.ZhuYaoBase;
import icbm.gangshao.model.ModelGunTurret;
import icbm.gangshao.turret.TileEntityTurretBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import calclavia.lib.render.RenderTaggedTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.library.access.AccessLevel;

@SideOnly(Side.CLIENT)
public class RenderGunTurretOld extends RenderTaggedTile
{
	public static final String TEXTURE_FILE = "gun_turret_neutral.png";
	public static final String TEXTURE_FILE_FRIENDLY = "gun_turret_friendly.png";
	public static final String TEXTURE_FILE_HOSTILE = "gun_turret_hostile.png";
	public static final ModelGunTurret MODEL = new ModelGunTurret();

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		super.renderTileEntityAt(t, x, y, z, f);

		if (t instanceof TileEntityTurretBase)
		{
			TileEntityTurretBase tileEntity = (TileEntityTurretBase) t;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5f, (float) y + 1f, (float) z + 0.5f);
			GL11.glScalef(0.7f, 0.7f, 0.7f);

			this.setTextureBaseOnState(tileEntity);
			RenderGunTurretOld.render(tileEntity.currentRotationYaw, tileEntity.currentRotationPitch);

			GL11.glPopMatrix();
		}
	}

	public static void render(float renderYaw, float renderPitch)
	{
		GL11.glRotatef(180F, 0F, 0F, 1F);
		GL11.glRotatef(180F, 0F, 1F, 0F);
		MODEL.render(0.0625F);
		// Render base yaw rotation
		GL11.glRotatef(renderYaw, 0F, 1F, 0F);
		MODEL.renderBody(0.0625F);
		// Render gun pitch rotation
		GL11.glRotatef(renderPitch + 10, 1F, 0F, 0F);
		MODEL.renderCannon(0.0625F);
	}

	public void setTextureBaseOnState(TileEntityTurretBase tileEntity)
	{
		EntityPlayer player = this.getPlayer();

		if (tileEntity.getPlatform() != null)
		{
			AccessLevel level = tileEntity.getPlatform().getUserAccess(player.username);

			if (level == AccessLevel.ADMIN)
			{
				this.bindTextureByName(ZhuYaoBase.TEXTURE_PATH + TEXTURE_FILE);
				return;
			}
			else if (level.ordinal() >= AccessLevel.USER.ordinal())
			{
				this.bindTextureByName(ZhuYaoBase.TEXTURE_PATH + TEXTURE_FILE_FRIENDLY);
				return;
			}
		}

		this.bindTextureByName(ZhuYaoBase.TEXTURE_PATH + TEXTURE_FILE_HOSTILE);

	}
}