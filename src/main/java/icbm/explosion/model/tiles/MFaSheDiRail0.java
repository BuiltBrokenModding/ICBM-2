package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MFaSheDiRail0 extends ModelBase
{
    // fields
    ModelRenderer Shape7;
    ModelRenderer Shape8;
    ModelRenderer Shape9;
    ModelRenderer Shape10;
    ModelRenderer Shape11;
    ModelRenderer Shape12;
    ModelRenderer Shape13;
    ModelRenderer Shape14;
    ModelRenderer Shape15;
    ModelRenderer Shape16;
    ModelRenderer Shape17;

    public MFaSheDiRail0()
    {
        textureWidth = 128;
        textureHeight = 128;

        Shape7 = new ModelRenderer(this, 35, 52);
        Shape7.addBox(0F, 0F, 0F, 1, 41, 4);
        Shape7.setRotationPoint(8F, -24F, -2F);
        Shape7.setTextureSize(128, 128);
        Shape7.mirror = true;
        setRotation(Shape7, 0F, 0F, 0F);
        Shape8 = new ModelRenderer(this, 0, 20);
        Shape8.addBox(0F, 0F, 0F, 2, 12, 2);
        Shape8.setRotationPoint(6F, 11F, -1F);
        Shape8.setTextureSize(128, 128);
        Shape8.mirror = true;
        setRotation(Shape8, 0F, 0F, 0F);
        Shape9 = new ModelRenderer(this, 46, 52);
        Shape9.addBox(0F, 0F, 0F, 3, 19, 2);
        Shape9.setRotationPoint(9F, -2F, -1F);
        Shape9.setTextureSize(128, 128);
        Shape9.mirror = true;
        setRotation(Shape9, 0F, 0F, 0F);
        Shape10 = new ModelRenderer(this, 0, 10);
        Shape10.addBox(0F, 0F, 0F, 2, 4, 2);
        Shape10.setRotationPoint(7F, -28F, -1F);
        Shape10.setTextureSize(128, 128);
        Shape10.mirror = true;
        setRotation(Shape10, 0F, 0F, 0F);
        Shape11 = new ModelRenderer(this, 35, 52);
        Shape11.addBox(0F, 0F, 0F, 1, 41, 4);
        Shape11.setRotationPoint(-8F, -24F, -2F);
        Shape11.setTextureSize(128, 128);
        Shape11.mirror = true;
        setRotation(Shape11, 0F, 0F, 0F);
        Shape12 = new ModelRenderer(this, 46, 52);
        Shape12.addBox(0F, 0F, 0F, 3, 19, 2);
        Shape12.setRotationPoint(-11F, -2F, -1F);
        Shape12.setTextureSize(128, 128);
        Shape12.mirror = true;
        setRotation(Shape12, 0F, 0F, 0F);
        Shape13 = new ModelRenderer(this, 0, 20);
        Shape13.addBox(0F, 0F, 0F, 2, 12, 2);
        Shape13.setRotationPoint(-7F, 11F, -1F);
        Shape13.setTextureSize(128, 128);
        Shape13.mirror = true;
        setRotation(Shape13, 0F, 0F, 0F);
        Shape14 = new ModelRenderer(this, 0, 10);
        Shape14.addBox(0F, 0F, 0F, 2, 4, 2);
        Shape14.setRotationPoint(-8F, -28F, -1F);
        Shape14.setTextureSize(128, 128);
        Shape14.mirror = true;
        setRotation(Shape14, 0F, 0F, 0F);
        Shape15 = new ModelRenderer(this, 0, 0);
        Shape15.addBox(0F, 0F, 0F, 48, 1, 8);
        Shape15.setRotationPoint(-24F, 23F, -4F);
        Shape15.setTextureSize(128, 128);
        Shape15.mirror = true;
        setRotation(Shape15, 0F, 0F, 0F);
        Shape16 = new ModelRenderer(this, 57, 52);
        Shape16.addBox(0F, 0F, 0F, 9, 6, 6);
        Shape16.setRotationPoint(-16F, 17F, -3F);
        Shape16.setTextureSize(128, 128);
        Shape16.mirror = true;
        setRotation(Shape16, 0F, 0F, 0F);
        Shape17 = new ModelRenderer(this, 57, 52);
        Shape17.addBox(0F, 0F, 0F, 9, 6, 6);
        Shape17.setRotationPoint(8F, 17F, -3F);
        Shape17.setTextureSize(128, 128);
        Shape17.mirror = true;
        setRotation(Shape17, 0F, 0F, 0F);
    }

    public void render(float f5)
    {
        Shape7.render(f5);
        Shape8.render(f5);
        Shape9.render(f5);
        Shape10.render(f5);
        Shape11.render(f5);
        Shape12.render(f5);
        Shape13.render(f5);
        Shape14.render(f5);
        Shape15.render(f5);
        Shape16.render(f5);
        Shape17.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
