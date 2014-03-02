package icbm.sentry.render;

import icbm.Reference;
import icbm.sentry.models.ModelRailgun;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import calclavia.lib.render.RenderUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRailGun extends TurretRenderer
{
	public static final ModelRailgun MODEL = new ModelRailgun();

	public RenderRailGun()
	{
		super(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "railgun.png"));
	}

	@Override
	public void render(ForgeDirection side, TileTurret tile, float yaw, float pitch)
	{
		GL11.glTranslatef(0.5f, 2.2f, 0.5f);
		GL11.glScalef(1.5f, 1.5f, 1.5f);
		GL11.glRotatef(180F, 0F, 0F, 1F);
		MODEL.render((float) Math.toRadians(yaw), (float) Math.toRadians(pitch), 0.0625F);
	}

	@Override
	public void renderInventoryItem(ItemStack itemStack)
	{
		RenderUtility.bind(textureNeutral);
		GL11.glTranslatef(0.5f, 1.35f, 0.7f);
		GL11.glRotatef(180F, 0F, 0F, 1F);
		MODEL.render(0, 0, 0.0625F);
	}
}