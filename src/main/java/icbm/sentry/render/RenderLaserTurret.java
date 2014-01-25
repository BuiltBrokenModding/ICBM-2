package icbm.sentry.render;

import icbm.Reference;
import icbm.sentry.models.ModelLaserTurret;
import icbm.sentry.turret.TileSentry;
import icbm.sentry.turret.sentries.AutoSentryTwinLaser;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLaserTurret extends RenderTurret
{
    public static final ModelLaserTurret MODEL = new ModelLaserTurret();

    public RenderLaserTurret()
    {
        TEXTURE_FILE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "laser_turret_neutral.png");
        TEXTURE_FILE_FRIENDLY = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "laser_turret_friendly.png");
        TEXTURE_FILE_HOSTILE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "laser_turret_hostile.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
    {
        super.renderTileEntityAt(t, x, y, z, f);

        if (t instanceof TileSentry)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.5f, (float) y + 1.5f, (float) z + 0.5f);

            this.setTextureBaseOnState((TileSentry) t);
            render(((TileSentry) t).getYawServo().getRotation(), ((TileSentry) t).getPitchServo().getRotation(), 0);

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
}