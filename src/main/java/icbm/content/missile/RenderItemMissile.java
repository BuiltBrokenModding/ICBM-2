package icbm.content.missile;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import icbm.api.explosion.IExplosive;

@SideOnly(Side.CLIENT)
public class RenderItemMissile implements IItemRenderer
{
    HashMap<IExplosive, IModelCustom> cache = new HashMap<IExplosive, IModelCustom>();

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
            IExplosive missile = ((ItemMissile)item.getItem()).getExplosive(item);

            float scale = 0.7f;
            float right = 0f;

            if (type == ItemRenderType.INVENTORY)
            {
                scale = 0.4f;
                right = -0.5f;

                //if (missile.getTier() == 2 || !missile.hasBlockForm())
                //{
                //    scale = scale / 1.5f;
                //}
                //else if (missile.getTier() == 3)
                //{
                //    scale = scale / 1.7f;
                //    right = -0.65f;
                //}
                //else if (missile.getTier() == 4)
                //{
                //    scale = scale / 1.4f;
                //    right = -0.45f;
                //}

                GL11.glTranslatef(right, 0f, 0f);
            }

            if (type == ItemRenderType.EQUIPPED)
            {
                GL11.glTranslatef(1f, 0.3f, 0.5f);
                GL11.glRotatef(0, 0, 0, 1f);
            }
            else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
            {
                GL11.glTranslatef(1.15f, -1f, 0.5f);
                GL11.glRotatef(0, 0, 0, 1f);
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

            //FMLClientHandler.instance().getClient().renderEngine.bindTexture(missile.getMissileResource());

            synchronized (RenderMissile.cache)
            {
                if (!RenderMissile.cache.containsKey(missile))
                {
                    //RenderMissile.cache.put(missile, missile.getMissileModel());
                }
                IModelCustom model = RenderMissile.cache.get(missile);
                if(model != null)
                    model.renderAll();
            }
        }
    }
}
