package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MFaSheJia extends ModelBase
{
    // fields
    ModelRenderer Shape18;
    ModelRenderer Shape19;

    public MFaSheJia()
    {
        textureWidth = 128;
        textureHeight = 128;

        Shape18 = new ModelRenderer(this, 57, 65);
        Shape18.addBox(0F, 0F, 0F, 16, 6, 8);
        Shape18.setRotationPoint(-8F, 18F, -4F);
        Shape18.setTextureSize(128, 128);
        Shape18.mirror = true;
        setRotation(Shape18, 0F, 0F, 0F);
        Shape19 = new ModelRenderer(this, 0, 52);
        Shape19.addBox(0F, 0F, 0F, 8, 50, 8);
        Shape19.setRotationPoint(-4F, -32F, -4F);
        Shape19.setTextureSize(128, 128);
        Shape19.mirror = true;
        setRotation(Shape19, 0F, 0F, 0F);
    }

    public void render(float f5)
    {
        Shape18.render(f5);
        Shape19.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
