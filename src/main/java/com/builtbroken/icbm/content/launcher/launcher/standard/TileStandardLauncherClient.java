package com.builtbroken.icbm.content.launcher.launcher.standard;

import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/13/2016.
 */
public class TileStandardLauncherClient extends TileStandardLauncher
{
    private IMissile missile;
    private AxisAlignedBB renderBounds;

    private static boolean processedModel = false;
    private static GroupObject[] frame = new GroupObject[7];
    private static GroupObject warhead;
    private static List<GroupObject> engine = new ArrayList();
    private static GroupObject guidance;
    private static GroupObject[][] skinLayers = new GroupObject[9][4];

    @Override
    public Tile newTile()
    {
        return new TileStandardLauncherClient();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        if (renderBounds == null)
        {
            renderBounds = AxisAlignedBB.getBoundingBox(x(), y(), z(), z() + 1, y() + 1, z() + 1);
        }
        return renderBounds;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos center, float f, int pass)
    {
        if (missile != null)
        {
            //Render launcher
            GL11.glPushMatrix();
            Pos pos = center.add(getDirection()).add(0.5, 1.9, 0.5);
            GL11.glTranslatef(pos.xf(), pos.yf(), pos.zf());
            GL11.glRotatef(45f, 0, 1, 0);
            if (missile instanceof ICustomMissileRender)
            {
                ((ICustomMissileRender) missile).renderMissileInWorld(0, 0, f);
            }
            GL11.glPopMatrix();
        }
        else if (recipe != null)
        {
            if (!processedModel)
            {
                processModel();
            }
            //Render launcher
            GL11.glPushMatrix();

            Pos pos = center;

            final float yf = 2.2f;
            switch (ForgeDirection.getOrientation(getMetadata()))
            {
                case NORTH:
                    pos = pos.add(-0.65, yf, 0.95);
                    break;
                case SOUTH:
                    pos = pos.add(-0.65, yf, 2.95);
                    break;
                case EAST:
                    pos = pos.add(.35, yf, 1.95);
                    break;
                case WEST:
                    pos = pos.add(-1.65, yf, 1.95);
                    break;
            }
            GL11.glTranslatef(pos.xf(), pos.yf(), pos.zf());
            GL11.glRotatef(45f, 0, 1, 0);
            GL11.glScalef(0.85f, 0.85f, 0.85f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
            //TODO render crafting progress
            //TODO render ghost of missile frame

            //Render frame pieces
            for (int i = 0; i < recipe.frameLevel && i < frame.length; i++)
            {
                frame[i].render();
            }
            //Only render guts of missile if frame is completed
            if (recipe.frameCompleted)
            {
                if (recipe.warhead != null)
                {
                    warhead.render();
                }
                if (recipe.rocketComputer != null)
                {
                    guidance.render();
                }
                if (recipe.rocketEngine != null)
                {
                    for (GroupObject o : engine)
                    {
                        o.render();
                    }
                }
                //Render Skin
                if (recipe.platesContained > 0)
                {
                    for (int i = 0; i < recipe.platesContained; i++)
                    {
                        //TODO fix layers as they are not perfect, could be naming of parts
                        int layer = i / StandardMissileCrafting.PLATE_PER_LEVEL_COUNT;
                        int set = i % StandardMissileCrafting.PLATE_PER_LEVEL_COUNT;
                        if (layer < skinLayers.length)
                        {
                            skinLayers[layer][set].render();
                        }
                    }
                }
            }

            GL11.glPopMatrix();
        }
    }

    private static void processModel()
    {
        processedModel = true;
        for (GroupObject object : Assets.STANDARD_MISSILE_MODEL_2.groupObjects)
        {
            if (object.name.contains("frame"))
            {
                String name = object.name.split("_")[0];
                int layer = 0;
                if (name.contains("1"))
                {
                    layer = 0;
                }
                else if (name.contains("2"))
                {
                    layer = 1;
                }
                else if (name.contains("3"))
                {
                    layer = 2;
                }
                else if (name.contains("4"))
                {
                    layer = 3;
                }
                else if (name.contains("5"))
                {
                    layer = 4;
                }
                else if (name.contains("6"))
                {
                    layer = 5;
                }
                else if (name.contains("7"))
                {
                    layer = 6;
                }
                frame[layer] = object;
            }
            else if (object.name.contains("CPcore"))
            {
                guidance = object;
            }
            else if (object.name.contains("fual") || object.name.contains("fire"))
            {
                engine.add(object);
            }
            else if (object.name.contains("boom"))
            {
                warhead = object;
            }
            else if (object.name.contains("skiln"))
            {
                String name = object.name.split("_")[0];

                int set = 0;
                int layer = 0;

                if (name.contains("1"))
                {
                    layer = 0;
                }
                else if (name.contains("2"))
                {
                    layer = 1;
                }
                else if (name.contains("3"))
                {
                    layer = 2;
                }
                else if (name.contains("4"))
                {
                    layer = 3;
                }
                else if (name.contains("5"))
                {
                    layer = 4;
                }
                else if (name.contains("6"))
                {
                    layer = 5;
                }
                else if (name.contains("7"))
                {
                    layer = 6;
                }
                else if (name.contains("8"))
                {
                    layer = 7;
                }
                else if (name.contains("9"))
                {
                    layer = 8;
                }

                if (name.contains("RT"))
                {
                    set = 0;
                }
                else if (name.contains("LB"))
                {
                    set = 1;
                }
                else if (name.contains("RB"))
                {
                    set = 2;
                }
                else if (name.contains("LT"))
                {
                    set = 3;
                }
                skinLayers[layer][set] = object;
            }
        }
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        byte type = buf.readByte();
        if (type == 0)
        {
            ItemStack missileStack = ByteBufUtils.readItemStack(buf);
            if(missileStack.getItem() instanceof IMissileItem)
            {
                missile = ((IMissileItem) missileStack.getItem()).toMissile(missileStack);
            }
            else
            {
                missile = MissileModuleBuilder.INSTANCE.buildMissile(missileStack);
            }
        }
        else if (type == 1)
        {
            this.missile = null;
            if (recipe == null)
            {
                recipe = new StandardMissileCrafting();
            }
            recipe.readBytes(buf);
        }
        else if (type == 2 || type == 3)
        {
            this.missile = null;
            this.recipe = null;
            this.isCrafting = false;
        }

        if (missile != null || recipe != null)
        {
            //TODO modified to fit missile rotation
            renderBounds = new Cube(-1, 0, -1, 2, 10, 2).add(x(), y(), z()).toAABB();
        }
    }
}
