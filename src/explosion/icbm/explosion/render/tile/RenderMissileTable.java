package icbm.explosion.render.tile;

import icbm.core.ICBMCore;
import icbm.explosion.machines.TileEntityRadarStation;
import icbm.explosion.missile.modular.TileEntityMissileTable;
import icbm.explosion.model.tiles.ModelMissileClaw;
import icbm.explosion.model.tiles.ModelMissileCradel;
import icbm.explosion.model.tiles.ModelRadarStation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMissileTable extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE = new ResourceLocation(ICBMCore.DOMAIN, ICBMCore.MODEL_PATH + "grey.png");

    public static final ModelMissileCradel MODEL = new ModelMissileCradel();
    public static final ModelMissileClaw MODEL_CLAW = new ModelMissileClaw();


    public void renderAModelAt(TileEntityMissileTable tileEntity, double x, double y, double z, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        this.bindTexture(TEXTURE_FILE);

        MODEL.render(0.0625F);
        //center claw
        MODEL_CLAW.render(0.0625F);
        //left
        MODEL_CLAW.render(0.0625F);
        //right
        MODEL_CLAW.render(0.0625F);


        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderAModelAt((TileEntityMissileTable) tileentity, d, d1, d2, f);
    }
}