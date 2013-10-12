package icbm.explosion.render.item;

import icbm.core.base.ModelICBM;
import icbm.explosion.zhapin.ExplosiveRegistry;
import icbm.explosion.zhapin.missile.ItemMissile;
import icbm.explosion.zhapin.missile.Missile;

import java.util.HashMap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.builtbroken.common.Pair;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderItemMissileNew implements IItemRenderer
{
    private static HashMap<Pair<Integer, Integer>, int[]> cache = new HashMap<Pair<Integer, Integer>, int[]>();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return this.shouldUseRenderHelper(type, item, null);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return item.getItem() instanceof ItemMissile;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        if (this.shouldUseRenderHelper(type, item, null))
        {
            Missile daoDan = (Missile) ExplosiveRegistry.get(item.getItemDamage());
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(daoDan.getMissileResource());
            Pair<Integer, Integer> par = new Pair<Integer, Integer>(item.getItemDamage(), type.ordinal());

            if (cache.containsKey(par))
            {
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GL11.glScalef(1.01F, 1.01F, 1.01F);
                int[] displayList = cache.get(par);
                for (int i = 0; i < displayList.length; i++)
                {
                    GL11.glCallList(displayList[i]);
                }

                GL11.glPopAttrib();
            }
            else
            {
                float scale = 0.7f;
                float right = 0f;
                ModelBase model = daoDan.getMissileModel();
                int[] display = new int[model.boxList.size()];

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_CULL_FACE);

                for (int s = 0; s < display.length; s++)
                {
                    display[s] = GLAllocation.generateDisplayLists(1);
                    GL11.glNewList(display[s], 4864 /*GL_COMPILE*/);

                    if (type == ItemRenderType.INVENTORY)
                    {
                        scale = 0.4f;
                        right = 0.15f;

                        if (daoDan.getTier() == 2 || !daoDan.hasBlockForm())
                        {
                            scale = scale / 1.5f;
                        }
                        else if (daoDan.getTier() == 3)
                        {
                            scale = scale / 1.7f;
                            right = 0.5f;
                        }
                        else if (daoDan.getTier() == 4)
                        {
                            scale = scale / 1.4f;
                            right = 0.2f;
                        }

                        GL11.glTranslatef(right, 0f, 0f);
                    }

                    if (type == ItemRenderType.EQUIPPED)
                    {
                        GL11.glTranslatef(1.15f, 1f, 0.5f);
                        GL11.glRotatef(180, 0, 0, 1f);
                    }
                    else
                    {
                        GL11.glRotatef(-90, 0, 0, 1f);
                    }

                    if (type == ItemRenderType.ENTITY)
                    {
                        scale = scale / 1.5f;
                    }
                    GL11.glScalef(scale, scale, scale);

                    ((ModelRenderer) model.boxList.get(s)).render(0.0625F);

                    GL11.glEndList();
                }

                GL11.glColor4f(1, 1, 1, 1);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);

                cache.put(par, display);

            }
        }
    }
}
