package icbm.sentry.render;

import icbm.Reference;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import calclavia.lib.render.RenderUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderAATurret extends TurretRenderer
{
	public static final IModelCustom MODEL = AdvancedModelLoader.loadModel(Reference.MODEL_DIRECTORY + "turret_aa.tcn");
	public static final String[] yawOnly = { "cannonRight", " cannonFaceRight", " cannonBarrelTopRight", " cannonBarrelBotRight", " cannonCapTopRight", " cannonCapBotRight", " cannonInFaceRight", " cannonBarrelCouple", " cannonBarrelNeck", " cannonCapBotLeft", " cannonCapTopLeft", " cannonFaceLeft", " cannonLeft", " cannonInFaceLeft", " cannonBarrelNeckLeft", " cannonBarrelBotLeft", " cannonBarrelCoupleLeft", " cannonBarrelTopLeft " };

	public RenderAATurret()
	{
		super(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "aa_turret_neutral.png"));
		textureFriendly = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "aa_turret_friendly.png");
		textureHostile = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "aa_turret_hostile.png");
	}

	@Override
	public void render(ForgeDirection side, TileTurret tile, double yaw, double pitch)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef(0.5f, 0.5f, 0.5f);
		GL11.glScalef(0.9f, 0.9f, 0.9f);
		// Render base yaw rotation
		GL11.glRotated(yaw, 0, 1, 0);
		MODEL.renderAllExcept(yawOnly);
		// Render gun pitch rotation
		GL11.glRotated(pitch - 5, 1, 0, 0);
		MODEL.renderOnly(yawOnly);
		GL11.glPopMatrix();
	}

	@Override
	public void renderInventoryItem(ItemStack itemStack)
	{
		RenderUtility.bind(textureNeutral);
		GL11.glTranslatef(0.5f, 0f, 0.6f);
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		MODEL.renderAll();
	}
}