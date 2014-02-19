package icbm.explosion.render.tile;

import icbm.Reference;
import icbm.explosion.machines.TileLauncherFrame;
import icbm.explosion.model.tiles.MFaSheJia;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RFaSheJia extends TileEntitySpecialRenderer
{
    public static final MFaSheJia MODEL = new MFaSheJia();
    public static final ResourceLocation TEXTURE_FILE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "launcher_0.png");

    @Override
    public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float var8)
    {
        TileLauncherFrame tileEntity = (TileLauncherFrame) var1;

        if (tileEntity != null && tileEntity.worldObj != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.5F, (float) y + 1.25F, (float) z + 0.5F);
            GL11.glScalef(1f, 0.85f, 1f);

            this.bindTexture(TEXTURE_FILE);
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

            if (tileEntity.getDirection() != ForgeDirection.NORTH && tileEntity.getDirection() != ForgeDirection.SOUTH)
            {
                GL11.glRotatef(90F, 0.0F, 180F, 1.0F);
            }

            MODEL.render(0.0625F);

            GL11.glPopMatrix();
        }
    }

}
