package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MFaSheShiMuo0 extends ModelBase
{
    // fields
    ModelRenderer Shape4;
    ModelRenderer Shape5;

    public MFaSheShiMuo0()
    {
        textureWidth = 128;
        textureHeight = 128;

        Shape4 = new ModelRenderer(this, 0, 35);
        Shape4.addBox(0F, 0F, 0F, 2, 8, 1);
        Shape4.setRotationPoint(-1F, 16F, 0F);
        Shape4.setTextureSize(128, 128);
        Shape4.mirror = true;
        setRotation(Shape4, 0F, 0F, 0F);
        Shape5 = new ModelRenderer(this, 15, 30);
        Shape5.addBox(0F, 0F, 0F, 10, 1, 5);
        Shape5.setRotationPoint(-5F, 17F, -2F);
        Shape5.setTextureSize(128, 128);
        Shape5.mirror = true;
        setRotation(Shape5, 0.5235988F, 0F, 0F);
    }

    public void render(float f5)
    {
        Shape4.render(f5);
        Shape5.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
