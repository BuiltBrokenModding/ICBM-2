package icbm.sentry.render;

import icbm.Reference;
import icbm.sentry.models.ModelSentryCannon;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.ForgeDirection;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

import calclavia.lib.render.RenderUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SentryRenderGunTurret extends SentryRenderer
{
	public static final IModelCustom MODEL = AdvancedModelLoader.loadModel(Reference.MODEL_DIRECTORY + "turret_gun.tcn");

	public SentryRenderGunTurret()
	{
		super(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "gun_turret_neutral.png"));
		textureFriendly = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "gun_turret_friendly.png");
		textureHostile = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "gun_turret_hostile.png");
	}

	@Override
	public void render(ForgeDirection side, TileTurret tile, float yaw, float pitch)
	{
		String[] yawOnly = new String[] { "BaseYawR", "BaseYawRPlate", "RightBrace", "RightBraceF", "RightBraceF2", "LeftBrace", "LeftBraceF", "FrontPlate", "SideDecor", "midPlate", "AmmoBox" };
		GL11.glPushMatrix();
		GL11.glTranslatef(0.5f, 0.5f, 0.5f);
		// Render base yaw rotation
		GL11.glRotatef(yaw, 0, 1, 0);
		MODEL.renderOnly(yawOnly);
		// Render gun pitch rotation
		GL11.glRotatef(pitch, 1, 0, 0);
		MODEL.renderAllExcept(yawOnly);
		GL11.glPopMatrix();

	}

	@Override
	public void renderInventoryItem(ItemStack itemStack)
	{
		RenderUtility.bind(textureNeutral);
		GL11.glTranslatef(0.5f, 0.5f, 0.6f);
		MODEL.renderAll();
	}
}