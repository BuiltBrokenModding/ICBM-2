package icbm.gangshao.render;

import icbm.core.ZhuYaoBase;
import icbm.gangshao.access.AccessLevel;
import icbm.gangshao.muoxing.ModelAATurret;
import icbm.gangshao.turret.TPaoDaiBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import calclavia.lib.render.RenderTaggedTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RFanKong extends RenderTaggedTile
{
	public static final String TEXTURE_FILE = "aa_turret_neutral.png";
	public static final String TEXTURE_FILE_FRIENDLY = "aa_turret_friendly.png";
	public static final String TEXTURE_FILE_HOSTILE = "aa_turret_hostile.png";
	public static final ModelAATurret MODEL = new ModelAATurret();

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		super.renderTileEntityAt(t, x, y, z, f);

		if (t instanceof TPaoDaiBase)
		{
			TPaoDaiBase tileEntity = (TPaoDaiBase) t;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5f, (float) y + 1f, (float) z + 0.5f);
			GL11.glScalef(0.7f, 0.7f, 0.7f);
			this.setTextureBaseOnState(tileEntity);
			render(tileEntity.currentRotationYaw, tileEntity.currentRotationPitch);

			GL11.glPopMatrix();
		}
	}

	public static void render(float renderYaw, float renderPitch)
	{
		GL11.glRotatef(180F, 0F, 0F, 1F);
		GL11.glRotatef(180F, 0F, 1F, 0F);
		// Render base yaw rotation
		GL11.glRotatef(renderYaw, 0F, 1F, 0F);
		MODEL.renderBody(0.0625F);
		MODEL.renderRadar(0.0625F);
		// Render gun pitch rotation
		MODEL.renderCannon(0.0625F, (float) Math.toRadians(renderPitch));
	}

	public void setTextureBaseOnState(TPaoDaiBase tileEntity)
	{
		EntityPlayer player = this.getPlayer();

		if (tileEntity.getPlatform() != null)
		{
			AccessLevel level = tileEntity.getPlatform().getUserAccess(player.username);

			if (level == AccessLevel.ADMIN)
			{
				this.bindTextureByName(ZhuYaoBase.MODEL_PATH + TEXTURE_FILE);
				return;
			}
			else if (level.ordinal() >= AccessLevel.USER.ordinal())
			{
				this.bindTextureByName(ZhuYaoBase.MODEL_PATH + TEXTURE_FILE_FRIENDLY);
				return;
			}
		}

		this.bindTextureByName(ZhuYaoBase.MODEL_PATH + TEXTURE_FILE_HOSTILE);

	}
}