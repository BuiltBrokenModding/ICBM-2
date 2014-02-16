package icbm.sentry.render;

import icbm.Reference;
import icbm.sentry.models.ModelLaserTurret;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import calclavia.lib.render.RenderUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SentryRenderLaserTurret extends SentryRenderer
{
	public static final ModelLaserTurret MODEL = new ModelLaserTurret();

	public SentryRenderLaserTurret()
	{
		super(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "laser_turret_neutral.png"));
		textureFriendly = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "laser_turret_friendly.png");
		textureHostile = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "laser_turret_hostile.png");
	}

	@Override
	public void render(ForgeDirection side, TileTurret sentry, float yaw, float pitch)
	{
		GL11.glTranslatef(0.5f, 2.2f, 0.5f);
		GL11.glScalef(1.5f, 1.5f, 1.5f);
		GL11.glRotatef(180F, 0F, 0F, 1F);
		// Render base yaw rotation
		// GL11.glRotatef(yaw, 0F, 1F, 0F);
		MODEL.renderYaw(0.0625F);
		// Render gun pitch rotation
		// GL11.glRotatef(pitch, 1F, 0F, 0F);
		MODEL.renderYawPitch(0.0625F, 0);

	}

	@Override
	public void renderInventoryItem(ItemStack itemStack)
	{
		RenderUtility.bind(textureNeutral);
		GL11.glTranslatef(0.5f, 1.5f, 0.9f);
		GL11.glRotatef(180F, 0F, 0F, 1F);
		// Render base yaw rotation
		// GL11.glRotatef(yaw, 0F, 1F, 0F);
		MODEL.renderYaw(0.0625F);
		// Render gun pitch rotation
		// GL11.glRotatef(pitch, 1F, 0F, 0F);
		MODEL.renderYawPitch(0.0625F, 0);
	}
}