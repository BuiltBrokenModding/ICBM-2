package icbm.explosion.render.tile;

import icbm.core.ICBMCore;
import icbm.core.base.ModelICBM;
import icbm.explosion.missile.ExplosiveRegistry;
import icbm.explosion.missile.missile.Missile;
import icbm.explosion.missile.modular.TileEntityMissileTable;
import icbm.explosion.model.tiles.ModelMissileClaw;
import icbm.explosion.model.tiles.ModelMissileCradel;

import java.util.HashMap;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMissileTable extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE = new ResourceLocation(ICBMCore.DOMAIN, ICBMCore.MODEL_PATH + "grey.png");

    public static final ModelMissileCradel MODEL = new ModelMissileCradel();
    public static final ModelMissileClaw MODEL_CLAW = new ModelMissileClaw(-2);
    public static final ModelMissileClaw MODEL_CLAW2 = new ModelMissileClaw(12);
    public static final ModelMissileClaw MODEL_CLAW3 = new ModelMissileClaw(-16);
    private static HashMap<Missile, ModelICBM> cache = new HashMap<Missile, ModelICBM>();;

    public void renderAModelAt(TileEntityMissileTable tileEntity, double x, double y, double z, float f)
    {
        GL11.glPushMatrix();
        this.bindTexture(TEXTURE_FILE);
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        if (tileEntity.placedSide == ForgeDirection.UP || tileEntity.placedSide == ForgeDirection.DOWN)
        {
            //line up on the x
            if (tileEntity.rotationSide == 1)
            {
                GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
            }
            else if (tileEntity.rotationSide == 2)
            {
                GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
            }
            else if (tileEntity.rotationSide == 3)
            {
                GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
            }
            if (tileEntity.placedSide == ForgeDirection.DOWN)
            {
                GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0, -2f, 0);
            }
        }
        else if (tileEntity.placedSide == ForgeDirection.EAST)
        {
            GL11.glRotatef(-90F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-1f, -1f, 0);
            if (tileEntity.rotationSide == 0)
            {
                GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
            }
            else if (tileEntity.rotationSide == 1)
            {
                GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
            }
            else if (tileEntity.rotationSide == 2)
            {
                GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
            }
        }
        else if (tileEntity.placedSide == ForgeDirection.WEST)
        {
            GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(1f, -1f, 0);
            if (tileEntity.rotationSide == 0)
            {
                GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
            }
            else if (tileEntity.rotationSide == 1)
            {
                GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
            }
            else if (tileEntity.rotationSide == 2)
            {
                GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
            }
        }
        else if (tileEntity.placedSide == ForgeDirection.NORTH)
        {
            GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0, -1f, -1f);
            if (tileEntity.rotationSide == 1)
            {
                GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
            }
            else if (tileEntity.rotationSide == 2)
            {
                GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
            }
            else if (tileEntity.rotationSide == 3)
            {
                GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
            }
        }
        else if (tileEntity.placedSide == ForgeDirection.SOUTH)
        {
            GL11.glRotatef(-90F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0, -1f, 1f);
            if (tileEntity.rotationSide == 1)
            {
                GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
            }
            else if (tileEntity.rotationSide == 2)
            {
                GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
            }
            else if (tileEntity.rotationSide == 3)
            {
                GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
            }
        }

        MODEL.render(0.0625F);
        //center claw
        MODEL_CLAW.render(0.0625F);
        MODEL_CLAW2.render(0.0625F);
        MODEL_CLAW3.render(0.0625F);

        Missile missile = (Missile) ExplosiveRegistry.get(0);

        GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
        GL11.glScalef(0.8f, 0.8f, 0.8f);
        GL11.glTranslatef(1.0f, 0f, 0f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(missile.getMissileResource());

        if (!this.cache.containsKey(missile))
        {
            this.cache.put(missile, missile.getMissileModel());
        }

        this.cache.get(missile).render(0.0625F);

        GL11.glPopMatrix();

    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderAModelAt((TileEntityMissileTable) tileentity, d, d1, d2, f);
    }
}