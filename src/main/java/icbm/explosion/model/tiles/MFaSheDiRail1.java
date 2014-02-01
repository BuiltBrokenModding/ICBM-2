package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MFaSheDiRail1 extends ModelBase
{
    // fields
    ModelRenderer Shape11;
    ModelRenderer Shape12;
    ModelRenderer Shape13;
    ModelRenderer Shape14;
    ModelRenderer Shape15;
    ModelRenderer Shape16;
    ModelRenderer Shape20;
    ModelRenderer Shape22;
    ModelRenderer Shape23;
    ModelRenderer Shape24;
    ModelRenderer Shape25;
    ModelRenderer Shape28;
    ModelRenderer Shape29;

    public MFaSheDiRail1()
    {
        textureWidth = 128;
        textureHeight = 128;

        Shape11 = new ModelRenderer(this, 35, 52);
        Shape11.addBox(0F, 0F, 0F, 1, 41, 4);
        Shape11.setRotationPoint(-16F, -23F, -2F);
        Shape11.setTextureSize(128, 128);
        Shape11.mirror = true;
        setRotation(Shape11, 0F, -0.7853982F, 0F);
        Shape12 = new ModelRenderer(this, 46, 52);
        Shape12.addBox(0F, 0F, 0F, 3, 19, 2);
        Shape12.setRotationPoint(-18F, -1F, -3F);
        Shape12.setTextureSize(128, 128);
        Shape12.mirror = true;
        setRotation(Shape12, 0F, -0.7853982F, 0F);
        Shape13 = new ModelRenderer(this, 0, 20);
        Shape13.addBox(0F, 0F, 0F, 2, 12, 2);
        Shape13.setRotationPoint(-16F, 12F, -1F);
        Shape13.setTextureSize(128, 128);
        Shape13.mirror = true;
        setRotation(Shape13, 0F, -0.7853982F, 0F);
        Shape14 = new ModelRenderer(this, 0, 10);
        Shape14.addBox(0F, 0F, 0F, 2, 4, 1);
        Shape14.setRotationPoint(-16F, -27F, -2F);
        Shape14.setTextureSize(128, 128);
        Shape14.mirror = true;
        setRotation(Shape14, 0F, -0.7853982F, 0F);
        Shape15 = new ModelRenderer(this, 0, 0);
        Shape15.addBox(0F, 0F, 0F, 16, 1, 8);
        Shape15.setRotationPoint(-24F, 23F, 0F);
        Shape15.setTextureSize(128, 128);
        Shape15.mirror = true;
        setRotation(Shape15, 0F, 0F, 0F);
        Shape16 = new ModelRenderer(this, 57, 52);
        Shape16.addBox(0F, 0F, 0F, 11, 6, 6);
        Shape16.setRotationPoint(-20F, 18F, -8F);
        Shape16.setTextureSize(128, 128);
        Shape16.mirror = true;
        setRotation(Shape16, 0F, -0.7853982F, 0F);
        Shape20 = new ModelRenderer(this, 0, 10);
        Shape20.addBox(0F, 0F, 0F, 2, 4, 1);
        Shape20.setRotationPoint(-18F, -27F, 0F);
        Shape20.setTextureSize(128, 128);
        Shape20.mirror = true;
        setRotation(Shape20, 0F, -0.7853982F, 0F);
        Shape22 = new ModelRenderer(this, 8, 10);
        Shape22.addBox(0F, 0F, 0F, 9, 1, 1);
        Shape22.setRotationPoint(-17F, 5F, -1F);
        Shape22.setTextureSize(128, 128);
        Shape22.mirror = true;
        setRotation(Shape22, 0F, 0F, 0F);
        Shape23 = new ModelRenderer(this, 8, 10);
        Shape23.addBox(0F, 0F, 0F, 9, 1, 1);
        Shape23.setRotationPoint(-17F, -10F, -1F);
        Shape23.setTextureSize(128, 128);
        Shape23.mirror = true;
        setRotation(Shape23, 0F, 0F, 0F);
        Shape24 = new ModelRenderer(this, 15, 20);
        Shape24.addBox(0F, 0F, 0F, 6, 1, 1);
        Shape24.setRotationPoint(-21F, 5F, 6F);
        Shape24.setTextureSize(128, 128);
        Shape24.mirror = true;
        setRotation(Shape24, 0F, 1.134464F, 0F);
        Shape25 = new ModelRenderer(this, 15, 20);
        Shape25.addBox(0F, 0F, 0F, 6, 1, 1);
        Shape25.setRotationPoint(-20F, 5F, 6F);
        Shape25.setTextureSize(128, 128);
        Shape25.mirror = true;
        setRotation(Shape25, 0F, -1.047198F, 0F);
        Shape28 = new ModelRenderer(this, 15, 19);
        Shape28.addBox(0F, 0F, 0F, 6, 1, 1);
        Shape28.setRotationPoint(-20F, -10F, 6F);
        Shape28.setTextureSize(128, 128);
        Shape28.mirror = true;
        setRotation(Shape28, 0F, -1.047198F, 0F);
        Shape29 = new ModelRenderer(this, 15, 19);
        Shape29.addBox(0F, 0F, 0F, 6, 1, 1);
        Shape29.setRotationPoint(-21F, -10F, 6F);
        Shape29.setTextureSize(128, 128);
        Shape29.mirror = true;
        setRotation(Shape29, 0F, 1.047198F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Shape11.render(f5);
        Shape12.render(f5);
        Shape13.render(f5);
        Shape14.render(f5);
        Shape15.render(f5);
        Shape16.render(f5);
        Shape20.render(f5);
        Shape22.render(f5);
        Shape23.render(f5);
        Shape24.render(f5);
        Shape25.render(f5);
        Shape28.render(f5);
        Shape29.render(f5);
    }

    public void render(float f5)
    {
        Shape11.render(f5);
        Shape12.render(f5);
        Shape13.render(f5);
        Shape14.render(f5);
        Shape15.render(f5);
        Shape16.render(f5);
        Shape20.render(f5);
        Shape22.render(f5);
        Shape23.render(f5);
        Shape24.render(f5);
        Shape25.render(f5);
        Shape28.render(f5);
        Shape29.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
