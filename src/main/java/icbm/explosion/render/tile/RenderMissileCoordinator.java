package icbm.explosion.render.tile;

import icbm.Reference;
import icbm.explosion.machines.TileEntityMissileCoordinator;
import icbm.explosion.model.tiles.ModelMissileCoordinator;

import java.util.List;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMissileCoordinator extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "missile_coordinator_off.png");
    public static final ResourceLocation TEXTURE_FILE_ON = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "missile_coordinator_on.png");
    public static final ModelMissileCoordinator MODEL = new ModelMissileCoordinator();
    private float lastSeePlayer = 0;
    private float lastFlicker = 0;

    public void renderModelAt(TileEntityMissileCoordinator tileEntity, double x, double y, double z, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        int radius = 4;
        List players = tileEntity.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(tileEntity.xCoord - radius, tileEntity.yCoord - radius, tileEntity.zCoord - radius, tileEntity.xCoord + radius, tileEntity.yCoord + radius, tileEntity.zCoord + radius));

        // CalclaviaRenderHelper.disableLighting();
        // CalclaviaRenderHelper.enableBlending();

        if (players.size() > 0)
        {
            if (this.lastSeePlayer > 20 * 3)
            {
                this.bindTexture(TEXTURE_FILE_ON);
            }
            else
            {
                // Flicker for first 3 seconds when player comes near.
                this.lastSeePlayer += f;

                if (Math.random() * 3 < this.lastSeePlayer / 3 && this.lastFlicker <= 0)
                {
                    this.lastFlicker = 8;
                    this.bindTexture(TEXTURE_FILE_ON);
                }
                else
                {
                    this.bindTexture(TEXTURE_FILE);
                }

                this.lastFlicker -= f;
            }

        }
        else
        {
            this.lastSeePlayer = 0;
            this.bindTexture(TEXTURE_FILE);
        }

        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        switch (tileEntity.getDirection().ordinal())
        {
            case 2:
                GL11.glRotatef(180F, 0.0F, 180F, 1.0F);
                break;
            case 4:
                GL11.glRotatef(90F, 0.0F, 180F, 1.0F);
                break;
            case 5:
                GL11.glRotatef(-90F, 0.0F, 180F, 1.0F);
                break;
        }

        // CalclaviaRenderHelper.disableBlending();
        // CalclaviaRenderHelper.enableLighting();

        MODEL.render(0, 0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderModelAt((TileEntityMissileCoordinator) tileentity, d, d1, d2, f);
    }
}
