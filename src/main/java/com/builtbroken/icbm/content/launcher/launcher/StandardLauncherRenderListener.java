package com.builtbroken.icbm.content.launcher.launcher;

import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.missile.client.RenderMissile;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.mc.framework.block.imp.IBlockListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListenerBuilder;
import com.builtbroken.mc.seven.framework.block.listeners.client.ITileRenderListener;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.seven.framework.block.listeners.TileListener;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.obj.GroupObject;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/13/2016.
 */
public class StandardLauncherRenderListener extends TileListener implements IBlockListener, ITileRenderListener
{
    private static boolean processedModel = false;
    private static GroupObject[] frame = new GroupObject[7];
    private static GroupObject warhead;
    private static List<GroupObject> engine = new ArrayList();
    private static GroupObject guidance;
    private static GroupObject[][] skinLayers = new GroupObject[9][4];

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(TileEntity tile, double xx, double yy, double zz, float f)
    {
        //Convert render position to pos for easy math
        final Pos center = new Pos(xx, yy, zz);

        //Only render if valid type
        if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TileStandardLauncher)
        {
            //Get type
            TileStandardLauncher launcher = (TileStandardLauncher) ((ITileNodeHost) tile).getTileNode();

            //Render missile
            if (launcher.getMissile() != null)
            {
                //Render launcher
                GL11.glPushMatrix();

                //Calculate render position
                Pos pos = center.add(0.5, 0, 0.5).add(launcher.getDirection());

                //Offset 1 more if medium
                if(launcher.missileSize == MissileSize.MEDIUM)
                {
                    pos = pos.add(launcher.getDirection());
                }

                //Offset render position based on missile type
                if(launcher.getMissile().getCenterOffset() != null)
                {
                    pos = pos.add(launcher.getMissile().getCenterOffset());
                }

                //Apply position
                GL11.glTranslatef(pos.xf(), pos.yf(), pos.zf());

                //Slight rotation to make missiles look nicer
                GL11.glRotatef(45f, 0, 1, 0);

                //Trigger render
                RenderMissile.renderMissile(launcher.getMissile(), 0, 90, null);

                //Reset
                GL11.glPopMatrix();
            }
            //Render recipe
            else if (launcher.recipe != null)
            {
                if (!processedModel)
                {
                    processModel();
                }
                //Render launcher
                GL11.glPushMatrix();

                Pos pos = center;

                final float yf = 2.2f;
                switch (launcher.getDirection())
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
                for (int i = 0; i < launcher.recipe.frameLevel && i < frame.length; i++)
                {
                    frame[i].render();
                }
                //Only render guts of missile if frame is completed
                if (launcher.recipe.frameCompleted)
                {
                    if (launcher.recipe.warhead != null)
                    {
                        warhead.render();
                    }
                    if (launcher.recipe.rocketComputer != null)
                    {
                        guidance.render();
                    }
                    if (launcher.recipe.rocketEngine != null)
                    {
                        for (GroupObject o : engine)
                        {
                            o.render();
                        }
                    }
                    //Render Skin
                    if (launcher.recipe.platesContained > 0)
                    {
                        for (int i = 0; i < launcher.recipe.platesContained; i++)
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
    }

    @Override
    public List<String> getListenerKeys()
    {
        List<String> list = new ArrayList();
        list.add("tilerender");
        return list;
    }

    private static void processModel()
    {
        //TODO convert to JSON
        //TODO in order to convert to JSON this will require a bit of for looping and layered rendering. Layered in such a way that only 1 render pass is done but all the sub parts are included
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

    public static class Builder implements ITileEventListenerBuilder
    {
        @Override
        public ITileEventListener createListener(Block block)
        {
            return new StandardLauncherRenderListener();
        }

        @Override
        public String getListenerKey()
        {
            return "StandardLauncherRenderListener";
        }
    }
}
