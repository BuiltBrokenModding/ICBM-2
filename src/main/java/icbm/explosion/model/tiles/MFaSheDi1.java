package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MFaSheDi1 extends ModelBase
{
    // fields
    ModelRenderer Shape1;
    ModelRenderer Shape6;
    ModelRenderer Shape2;
    ModelRenderer Shape3;

    public MFaSheDi1()
    {
        textureWidth = 128;
        textureHeight = 128;

        Shape1 = new ModelRenderer(this, 0, 111);
        Shape1.addBox(0F, 0F, 0F, 16, 1, 16);
        Shape1.setRotationPoint(-8F, 23F, -8F);
        Shape1.setTextureSize(128, 128);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
        Shape6 = new ModelRenderer(this, 0, 111);
        Shape6.addBox(0F, 0F, 0F, 16, 1, 16);
        Shape6.setRotationPoint(-8F, 19F, -8F);
        Shape6.setTextureSize(128, 128);
        Shape6.mirror = true;
        setRotation(Shape6, 0F, 0F, 0F);
        Shape2 = new ModelRenderer(this, 63, 0);
        Shape2.addBox(0F, 0F, 0F, 2, 4, 2);
        Shape2.setRotationPoint(-8F, 20F, -1F);
        Shape2.setTextureSize(128, 128);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);
        Shape3 = new ModelRenderer(this, 63, 0);
        Shape3.addBox(0F, 0F, 0F, 2, 4, 2);
        Shape3.setRotationPoint(6F, 20F, -1F);
        Shape3.setTextureSize(128, 128);
        Shape3.mirror = true;
        setRotation(Shape3, 0F, 0F, 0F);
    }

    public void render(float f5)
    {
        Shape1.render(f5);
        Shape6.render(f5);
        Shape2.render(f5);
        Shape3.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
