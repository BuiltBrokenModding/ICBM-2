package icbm.content.tile.launcher;

import icbm.Reference;
import icbm.content.render.models.*;
import icbm.content.render.models.ModelTier1Base;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLauncherBase extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE_0 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_0.png");
    public static final ResourceLocation TEXTURE_FILE_1 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_1.png");
    public static final ResourceLocation TEXTURE_FILE_2 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_2.png");

    public static final ModelTier1Base modelBase0 = new ModelTier1Base();
    public static final ModelTier1Rail modelRail0 = new ModelTier1Rail();

    public static final ModelTier2Base modelBase1 = new ModelTier2Base();
    public static final ModelTier2Rail modelRail1 = new ModelTier2Rail();

    public static final ModelTier3Base modelBase2 = new ModelTier3Base();
    public static final ModelTier3Rail modelRail2 = new ModelTier3Rail();

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f)
    {
        TileLauncherBase tileEntity = (TileLauncherBase) tileentity;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        if (tileEntity.getDirection() != ForgeDirection.NORTH && tileEntity.getDirection() != ForgeDirection.SOUTH)
        {
            GL11.glRotatef(90F, 0F, 180F, 1.0F);
        }

        // The missile launcher screen
        if (tileEntity.getTier() == 0)
        {
            this.bindTexture(TEXTURE_FILE_0);
            modelBase0.render(0.0625F);
            modelRail0.render(0.0625F);
        }
        else if (tileEntity.getTier() == 1)
        {
            this.bindTexture(TEXTURE_FILE_1);
            modelBase1.render(0.0625F);
            modelRail1.render(0.0625F);
            GL11.glRotatef(180F, 0F, 180F, 1.0F);
            modelRail1.render(0.0625F);
        }
        else if (tileEntity.getTier() == 2)
        {
            this.bindTexture(TEXTURE_FILE_2);
            modelBase2.render(0.0625F);
            modelRail2.render(0.0625F);
            GL11.glRotatef(180F, 0F, 180F, 1.0F);
            modelRail2.render(0.0625F);
        }

        GL11.glPopMatrix();
    }
}
