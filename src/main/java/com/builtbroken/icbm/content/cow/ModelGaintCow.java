package com.builtbroken.icbm.content.cow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelGaintCow extends ModelCow
{
    public ModelGaintCow()
    {
        super();
        //Set rotations
        --this.leg1.rotationPointX;
        ++this.leg2.rotationPointX;
        this.leg1.rotationPointZ += 0.0F;
        this.leg2.rotationPointZ += 0.0F;
        --this.leg3.rotationPointX;
        ++this.leg4.rotationPointX;
        --this.leg3.rotationPointZ;
        --this.leg4.rotationPointZ;
        this.field_78151_h += 2.0F;
    }

    @Override
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);

        float scaleFactor = scale();

        GL11.glPushMatrix();
        GL11.glTranslated(0, -4, 0);
        GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
        this.head.render(p_78088_7_);
        this.body.render(p_78088_7_);
        this.leg1.render(p_78088_7_);
        this.leg2.render(p_78088_7_);
        this.leg3.render(p_78088_7_);
        this.leg4.render(p_78088_7_);
        GL11.glPopMatrix();
    }

    protected float scale()
    {
        return 4;
    }

    public void reloadModel(boolean reset)
    {
        if (reset)
        {
            ModelCow cow = new ModelCow();
            head = cow.head;
            body = cow.body;
            leg1 = cow.leg2;
            leg2 = cow.leg2;
            leg3 = cow.leg3;
            leg4 = cow.leg4;
        }
        ModelRenderer[] renders = new ModelRenderer[]{head, body, leg1, leg2, leg3, leg4};

        for (ModelRenderer renderer : renders)
        {

        }
    }
}