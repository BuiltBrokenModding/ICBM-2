package icbm.gangshao.render;

import icbm.core.ZhuYaoICBM;
import icbm.gangshao.access.AccessLevel;
import icbm.gangshao.muoxing.MLeiShe;
import icbm.gangshao.turret.TPaoDaiBase;
import icbm.gangshao.turret.sentries.TLeiShe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import calclavia.lib.render.RenderTaggedTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RLeiShe extends RenderTaggedTile
{
	public static final ResourceLocation TEXTURE_FILE = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.MODEL_PATH + "laser_turret_neutral.png");
	public static final ResourceLocation TEXTURE_FILE_FRIENDLY = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.MODEL_PATH + "laser_turret_friendly.png");
	public static final ResourceLocation TEXTURE_FILE_HOSTILE = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.MODEL_PATH + "laser_turret_hostile.png");
	public static final MLeiShe MODEL = new MLeiShe();

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		super.renderTileEntityAt(t, x, y, z, f);

		if (t instanceof TLeiShe)
		{
			TLeiShe tileEntity = (TLeiShe) t;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5f, (float) y + 1.5f, (float) z + 0.5f);

			this.setTextureBaseOnState(tileEntity);
			render(tileEntity.currentRotationYaw, tileEntity.currentRotationPitch, tileEntity.barrelRotation);

			GL11.glPopMatrix();
		}
	}

	public static void render(float renderYaw, float renderPitch)
	{
		render(renderYaw, renderPitch, 0);
	}

	public static void render(float renderYaw, float renderPitch, float barrelRotation)
	{
		GL11.glRotatef(180F, 0F, 0F, 1F);
		GL11.glRotatef(180F, 0F, 1F, 0F);
		// Render base yaw rotation
		GL11.glRotatef(renderYaw, 0F, 1F, 0F);
		MODEL.renderYaw(0.0625F);
		// Render gun pitch rotation
		GL11.glRotatef(renderPitch, 1F, 0F, 0F);
		MODEL.renderYawPitch(0.0625F, barrelRotation);
	}

	public void setTextureBaseOnState(TPaoDaiBase tileEntity)
	{
		EntityPlayer player = this.getPlayer();

		if (tileEntity.getPlatform() != null)
		{
			AccessLevel level = tileEntity.getPlatform().getUserAccess(player.username);

			if (level == AccessLevel.ADMIN)
			{
				this.func_110628_a(TEXTURE_FILE);
				return;
			}
			else if (level.ordinal() >= AccessLevel.USER.ordinal())
			{
				this.func_110628_a(TEXTURE_FILE_FRIENDLY);
				return;
			}
		}

		this.func_110628_a(TEXTURE_FILE_HOSTILE);

	}
}