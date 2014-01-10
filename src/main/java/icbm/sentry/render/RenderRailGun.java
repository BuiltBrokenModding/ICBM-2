package icbm.sentry.render;

import icbm.Reference;
import icbm.sentry.models.ModelRailgun;
import icbm.sentry.turret.mount.TileEntityRailGun;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRailGun extends RenderTaggedTile
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "railgun.png");
    public static final ModelRailgun MODEL = new ModelRailgun();

    @Override
    public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
    {
        super.renderTileEntityAt(t, x, y, z, f);

        if (t instanceof TileEntityRailGun)
        {
            TileEntityRailGun tileEntity = (TileEntityRailGun) t;

            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.5F, (float) y + 2.2F, (float) z + 0.5F);
            GL11.glScalef(1.5f, 1.5f, 1.5f);
            this.bindTexture(TEXTURE);
            GL11.glRotatef(180F, 0F, 0F, 1F);
            GL11.glRotatef(180F, 0F, 1F, 0F);
            MODEL.render((float) Math.toRadians(tileEntity.getYawServo().getRotation()), (float) Math.toRadians(tileEntity.getPitchServo().getRotation()), 0.0625F);
            GL11.glPopMatrix();
        }
    }
}