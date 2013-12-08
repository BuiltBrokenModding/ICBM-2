package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MFaSheShiMuo2 extends ModelBase
{
    // fields
    ModelRenderer Shape27;
    ModelRenderer Shape29;
    ModelRenderer Shape30;
    ModelRenderer Shape31;

    public MFaSheShiMuo2()
    {
        textureWidth = 256;
        textureHeight = 256;

        Shape27 = new ModelRenderer(this, 0, 30);
        Shape27.addBox(0F, 0F, 0F, 8, 9, 3);
        Shape27.setRotationPoint(-4F, 15F, -2F);
        Shape27.setTextureSize(256, 256);
        Shape27.mirror = true;
        setRotation(Shape27, 0F, 0F, 0F);
        Shape29 = new ModelRenderer(this, 0, 19);
        Shape29.addBox(0F, 0F, 0F, 10, 1, 8);
        Shape29.setRotationPoint(-5F, 16F, -6F);
        Shape29.setTextureSize(256, 256);
        Shape29.mirror = true;
        setRotation(Shape29, 0.3141593F, 0F, 0F);
        Shape30 = new ModelRenderer(this, 0, 0);
        Shape30.addBox(0F, 0F, 0F, 10, 8, 1);
        Shape30.setRotationPoint(-5F, 6F, 5F);
        Shape30.setTextureSize(256, 256);
        Shape30.mirror = true;
        setRotation(Shape30, -0.4363323F, 0F, 0F);
        Shape31 = new ModelRenderer(this, 25, 0);
        Shape31.addBox(0F, 0F, 0F, 2, 10, 1);
        Shape31.setRotationPoint(-1F, 10F, 4F);
        Shape31.setTextureSize(256, 256);
        Shape31.mirror = true;
        setRotation(Shape31, -0.4363323F, 0F, 0F);
    }

    public void render(float f5)
    {
        Shape27.render(f5);
        Shape29.render(f5);
        Shape30.render(f5);
        Shape31.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
