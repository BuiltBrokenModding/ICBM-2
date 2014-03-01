package icbm.sentry.render;

import icbm.Reference;
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
public class SentryRenderLaserTurret extends SentryRenderer
{
	public static final IModelCustom MODEL = AdvancedModelLoader.loadModel(Reference.MODEL_DIRECTORY + "turret_laser.tcn");

	public SentryRenderLaserTurret()
	{
		super(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "laser_turret_neutral.png"));
		textureFriendly = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "laser_turret_friendly.png");
		textureHostile = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "laser_turret_hostile.png");
	}

	@Override
	public void render(ForgeDirection side, TileTurret sentry, float yaw, float pitch)
	{
		String[] pitchParts = new String[] { "body", "bodyTop", "bodyRight", "leftBarrel", "rightBarrel", "l1", "r1", "lCap", "rCap", "Hat", "LowerHat", "BatteryPack", "MiddleWire", "BatWire", "HatWire", "lEar1", "lEar2", "rEar1", "rEar2" };
		String[] spinParts = new String[] { "l2", "l3", "l4", "l5", "r2", "r3", "r4", "r5" };
		GL11.glPushMatrix();
		GL11.glTranslatef(0.5f, 0.5f, 0.5f);

		// Render base yaw rotation
		GL11.glRotatef(yaw, 0, 1, 0);
		MODEL.renderAllExcept(ArrayUtils.addAll(pitchParts, spinParts));

		// Render gun pitch rotation
		GL11.glRotatef(pitch, 1, 0, 0);
		MODEL.renderOnly(pitchParts);
		// TODO: These parts are supposed to spin when a shot is fired!
		GL11.glRotatef(pitch, 0, 0, 1);
		MODEL.renderOnly(spinParts);
		GL11.glPopMatrix();
	}

	@Override
	public void renderInventoryItem(ItemStack itemStack)
	{
		RenderUtility.bind(textureNeutral);
		GL11.glTranslatef(0.5f, 0.5f, 0.9f);
		MODEL.renderAll();
	}
}