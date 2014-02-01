package icbm.explosion.render.tile;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import icbm.Reference;
import icbm.core.prefab.render.ModelICBM;
import icbm.explosion.missile.ExplosiveRegistry;
import icbm.explosion.missile.missile.Missile;
import icbm.explosion.missile.modular.TileMissileAssembler;
import icbm.explosion.model.tiles.ModelMissileAssemblerClaw;
import icbm.explosion.model.tiles.ModelMissileAssemblerPanel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class RenderMissileAssembler extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "grey.png");

    public static final ModelMissileAssemblerPanel MODEL_PANEL = new ModelMissileAssemblerPanel();
    public static final ModelMissileAssemblerClaw MODEL_CLAW1 = new ModelMissileAssemblerClaw(-2);
    public static final ModelMissileAssemblerClaw MODEL_CLAW2 = new ModelMissileAssemblerClaw(12);
    public static final ModelMissileAssemblerClaw MODEL_CLAW3 = new ModelMissileAssemblerClaw(-16);
    private static HashMap<Missile, ModelICBM> cache = new HashMap<Missile, ModelICBM>();;

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
        if (tileEntity.missileID >= 0)
        {
            Missile missile = (Missile) ExplosiveRegistry.get(tileEntity.missileID);
            float scale = 0.8f;
            float right = 0f;

            if (missile.getTier() == 2 || !missile.hasBlockForm())
            {
                // scale = scale / 1.5f;
            }
            else if (missile.getTier() == 3)
            {
                // scale = scale / 1.7f;
            }
            else if (missile.getTier() == 4)
            {
                // scale = scale / 1.4f;
            }

            GL11.glTranslatef(right, 0f, 0f);

            GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(1.0f, 0f, 0f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(missile.getMissileResource());

            if (!RenderMissileAssembler.cache.containsKey(missile))
            {
                RenderMissileAssembler.cache.put(missile, missile.getMissileModel());
            }

            RenderMissileAssembler.cache.get(missile).render(0.0625F);
        }

        GL11.glPopMatrix();

    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderAModelAt((TileMissileAssembler) tileentity, d, d1, d2, f);
    }
}