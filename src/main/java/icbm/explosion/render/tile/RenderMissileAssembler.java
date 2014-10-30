package icbm.explosion.render.tile;

import icbm.Reference;
import icbm.explosion.ex.Explosion;
import icbm.explosion.explosive.ExplosiveRegistry;
import icbm.explosion.machines.TileMissileAssembler;
import icbm.explosion.model.tiles.ModelMissileAssemblerClaw;
import icbm.explosion.model.tiles.ModelMissileAssemblerPanel;
import icbm.explosion.render.entity.RenderMissile;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
/** @author Darkguardsman */
public class RenderMissileAssembler extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "missileAssembler.png");

    public static final ModelMissileAssemblerPanel MODEL_PANEL = new ModelMissileAssemblerPanel();
    public static final ModelMissileAssemblerClaw MODEL_CLAW1 = new ModelMissileAssemblerClaw(-2);
    public static final ModelMissileAssemblerClaw MODEL_CLAW2 = new ModelMissileAssemblerClaw(12);
    public static final ModelMissileAssemblerClaw MODEL_CLAW3 = new ModelMissileAssemblerClaw(-16);

    public void renderAModelAt(TileMissileAssembler tileEntity, double x, double y, double z, float f)
    {
        GL11.glPushMatrix();
        this.bindTexture(TEXTURE_FILE);
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        if (tileEntity.placedSide == ForgeDirection.UP || tileEntity.placedSide == ForgeDirection.DOWN)
        {
            // line up on the x
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

        MODEL_PANEL.render(0.0625F);
        MODEL_CLAW1.render(0.0625F);
        MODEL_CLAW2.render(0.0625F);
        MODEL_CLAW3.render(0.0625F);
        if (tileEntity.missileID >= 0 && ExplosiveRegistry.get(tileEntity.missileID) != null)
        {
            Explosion missile = (Explosion) ExplosiveRegistry.get(tileEntity.missileID);
            float scale = 0.8f;
            float right = 1f;

            GL11.glTranslatef(right, 0f, 0f);

            GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(1.0f, 0f, 0f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(missile.getMissileResource());

            synchronized (RenderMissile.cache)
            {
                if (!RenderMissile.cache.containsKey(missile))
                {
                    RenderMissile.cache.put(missile, missile.getMissileModel());
                }

                RenderMissile.cache.get(missile).renderAll();
            }
        }

        GL11.glPopMatrix();

    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderAModelAt((TileMissileAssembler) tileentity, d, d1, d2, f);
    }
}