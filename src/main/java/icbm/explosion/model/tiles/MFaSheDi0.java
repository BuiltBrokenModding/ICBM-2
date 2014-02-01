package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MFaSheDi0 extends ModelBase
{
    // fields
    ModelRenderer Shape1;
    ModelRenderer Shape6;
    ModelRenderer Shape8;
    ModelRenderer Shape13;

    public MFaSheDi0()
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
        Shape8 = new ModelRenderer(this, 0, 20);
        Shape8.addBox(0F, 0F, 0F, 2, 12, 2);
        Shape8.setRotationPoint(6F, 11F, -1F);
        Shape8.setTextureSize(128, 128);
        Shape8.mirror = true;
        setRotation(Shape8, 0F, 0F, 0F);
        Shape13 = new ModelRenderer(this, 0, 20);
        Shape13.addBox(0F, 0F, 0F, 2, 12, 2);
        Shape13.setRotationPoint(-7F, 11F, -1F);
        Shape13.setTextureSize(128, 128);
        Shape13.mirror = true;
        setRotation(Shape13, 0F, 0F, 0F);
    }

    public void render(float f5)
    {
        Shape1.render(f5);
        Shape6.render(f5);
        Shape8.render(f5);
        Shape13.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
