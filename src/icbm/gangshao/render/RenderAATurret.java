package icbm.gangshao.render;

import icbm.core.ZhuYao;
import icbm.gangshao.model.ModelSentryCannon;
import icbm.gangshao.model.ModelWirlWindAA;
import icbm.gangshao.turret.TileEntityTurretBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.library.access.AccessLevel;

@SideOnly(Side.CLIENT)
public class RenderAATurret extends RenderTaggedTile
{
	public static final String TEXTURE_FILE = "";
	public static final String TEXTURE_FILE_FRIENDLY = "";
	public static final String TEXTURE_FILE_HOSTILE = "";
	public static final ModelWirlWindAA MODEL = new ModelWirlWindAA();

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		super.renderTileEntityAt(t, x, y, z, f);

		if (t instanceof TileEntityTurretBase)
		{
			TileEntityTurretBase tileEntity = (TileEntityTurretBase) t;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5f, (float) y + 1.5f, (float) z + 0.5f);

			this.setTextureBaseOnState(tileEntity);
			RenderAATurret.render(tileEntity.rotationYaw, tileEntity.rotationPitch);

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
		MODEL.renderCannon(0.0625F,renderPitch);
	}

	public void setTextureBaseOnState(TileEntityTurretBase tileEntity)
	{
		EntityPlayer player = this.getPlayer();

		if (tileEntity.getPlatform() != null)
		{
			AccessLevel level = tileEntity.getPlatform().getUserAccess(player.username);

			if (level == AccessLevel.ADMIN)
			{
				this.bindTextureByName(ZhuYao.MODEL_PATH + TEXTURE_FILE);
				return;
			}
			else if (level.ordinal() >= AccessLevel.USER.ordinal())
			{
				this.bindTextureByName(ZhuYao.MODEL_PATH + TEXTURE_FILE_FRIENDLY);
				return;
			}
		}

		this.bindTextureByName(ZhuYao.MODEL_PATH + TEXTURE_FILE_HOSTILE);

	}
}