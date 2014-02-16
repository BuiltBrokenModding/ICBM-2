package icbm.sentry.render;

import icbm.Reference;
import icbm.sentry.models.ModelSentryCannon;
import icbm.sentry.turret.block.TileSentry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SentryRenderGunTurret extends SentryRenderer
{
	public static final ModelSentryCannon MODEL = new ModelSentryCannon();

	public SentryRenderGunTurret()
	{
		super(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "gun_turret_neutral.png"));
		TEXTURE_FILE_FRIENDLY = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "gun_turret_friendly.png");
		TEXTURE_FILE_HOSTILE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "gun_turret_hostile.png");
	}

	@Override
	public void render(ForgeDirection side, TileSentry tile, float yaw, float pitch)
	{
		GL11.glTranslatef(0.5f, 2.2f, 0.5f);
		GL11.glScalef(1.5f, 1.5f, 1.5f);
		GL11.glRotatef(180F, 0F, 0F, 1F);
		// Render base yaw rotation
		// GL11.glRotatef(yaw, 0F, 1F, 0F);
		MODEL.renderYaw(0.0625F);
		// Render gun pitch rotation
		// GL11.glRotatef(pitch, 1F, 0F, 0F);
		MODEL.renderYawPitch(0.0625F);

	}

	@Override
	public void renderInventoryItem(ItemStack itemStack)
	{

	}
}