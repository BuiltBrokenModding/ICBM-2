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
public class RenderGunTurret extends TurretRenderer
{
    public static final IModelCustom MODEL = AdvancedModelLoader.loadModel(Reference.MODEL_DIRECTORY + "turret_gun.tcn");
    public static final String[] yawOnly = new String[] { "BaseYawR", "BaseYawRPlate", "RightBrace", "RightBraceF", "RightBraceF2", "LeftBrace", "LeftBraceF", "FrontPlate", "SideDecor", "midPlate", "AmmoBox" };

    public RenderGunTurret()
    {
        super(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "gun_turret_neutral.png"));
        textureFriendly = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "gun_turret_friendly.png");
        textureHostile = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "gun_turret_hostile.png");
    }

    @Override
    public void render(ForgeDirection side, TileTurret tile, double yaw, double pitch)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        // Render base yaw rotation
        GL11.glRotated(yaw, 0, 1, 0);
        MODEL.renderOnly(yawOnly);
        // Render gun pitch rotation
        GL11.glRotated(pitch, 1, 0, 0);
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