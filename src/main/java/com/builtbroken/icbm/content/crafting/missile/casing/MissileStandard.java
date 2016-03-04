package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.api.ICustomMissileRender;
import com.builtbroken.icbm.client.Assets;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.obj.GroupObject;
import org.lwjgl.opengl.GL11;

/**
 * Standard Missile Casing
 * Created by robert on 12/29/2014.
 */
public final class MissileStandard extends Missile implements ICustomMissileRender
{
    private static final float inventoryScale = 0.4f;
    private static final float worldScale = 1f;

    private static GroupObject BODY;
    private static GroupObject FINS;

    public MissileStandard(ItemStack stack)
    {
        super(stack, MissileCasings.STANDARD);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderMissileItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.STANDARD_MISSILE_TEXTURE);

        if (type == IItemRenderer.ItemRenderType.INVENTORY)
        {
            GL11.glScalef(inventoryScale, inventoryScale, inventoryScale);
            GL11.glRotatef(2f, 0, 0, 1);
            GL11.glRotatef(10f, 0, 1, 0);
            GL11.glTranslatef(.1f, -3.4f, 0f);
        }
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glTranslatef(2, 1, 2);
            GL11.glRotatef(-60f, 1, 0, 0);
            GL11.glRotatef(100f, 0, 0, 1);
        }
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED)
        {
            GL11.glRotatef(30f, 1, 0, 0);
            GL11.glRotatef(70f, 0, 0, 1);
            //neg forward, neg down, pos right
            GL11.glTranslatef(-1.7f, -1, 2.3f);
        }
        render();
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderMissileInWorld()
    {
        GL11.glScalef(worldScale, worldScale, worldScale);
        render();
        return true;
    }

    private final void render()
    {
        if (BODY == null || FINS == null)
        {
            for (GroupObject object : Assets.STANDARD_MISSILE_MODEL.groupObjects)
            {
                if (object.name.equals("Body_SmallSilo.002"))
                {
                    BODY = object;
                }
                else if (object.name.equals("Fins_SmallSilo.006"))
                {
                    FINS = object;
                }
            }
        }
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.STANDARD_MISSILE_TEXTURE);
        BODY.render();
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.STANDARD_MISSILE_FINS_TEXTURE);
        FINS.render();
    }
}
