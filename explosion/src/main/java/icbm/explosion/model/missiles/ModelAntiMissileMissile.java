package icbm.explosion.model.missiles;

import icbm.ModelICBM;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAntiMissileMissile extends ModelICBM
{
    // fields
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape2a;
    ModelRenderer Shape2b;
    ModelRenderer Shape2c;
    ModelRenderer Shape3d;
    ModelRenderer Shape3;
    ModelRenderer Shape3a;
    ModelRenderer Shape3b;
    ModelRenderer Shape4;
    ModelRenderer Shape4a;
    ModelRenderer Shape4b;
    ModelRenderer Shape4c;
    ModelRenderer Shape5;
    ModelRenderer Shape5a;
    ModelRenderer Shape4d;
    ModelRenderer Shape4e;
    ModelRenderer Shape4f;
    ModelRenderer Shape4g;
    ModelRenderer Shape6;
    ModelRenderer Shape6a;
    ModelRenderer Shape7a;
    ModelRenderer Shape7;
    ModelRenderer Shape8;
    ModelRenderer Shape8a;
    ModelRenderer Shape8b;
    ModelRenderer Shape8c;
    ModelRenderer Shape8d;

    public ModelAntiMissileMissile()
    {
        textureWidth = 128;
        textureHeight = 128;

        Shape1 = new ModelRenderer(this, 0, 0);
        Shape1.addBox(-4F, 0F, -4F, 8, 64, 8);
        Shape1.setRotationPoint(0F, -40F, 0F);
        Shape1.setTextureSize(128, 128);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
        Shape2 = new ModelRenderer(this, 34, 19);
        Shape2.addBox(0F, 0F, 0F, 14, 10, 1);
        Shape2.setRotationPoint(-7F, 14F, 6F);
        Shape2.setTextureSize(128, 128);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);
        Shape2a = new ModelRenderer(this, 34, 19);
        Shape2a.addBox(0F, 0F, 0F, 14, 10, 1);
        Shape2a.setRotationPoint(-7F, 14F, -7F);
        Shape2a.setTextureSize(128, 128);
        Shape2a.mirror = true;
        setRotation(Shape2a, 0F, 0F, 0F);
        Shape2b = new ModelRenderer(this, 34, 31);
        Shape2b.addBox(0F, 0F, 0F, 1, 10, 12);
        Shape2b.setRotationPoint(-7F, 14F, -6F);
        Shape2b.setTextureSize(128, 128);
        Shape2b.mirror = true;
        setRotation(Shape2b, 0F, 0F, 0F);
        Shape2c = new ModelRenderer(this, 34, 31);
        Shape2c.addBox(0F, 0F, 0F, 1, 10, 12);
        Shape2c.setRotationPoint(6F, 14F, -6F);
        Shape2c.setTextureSize(128, 128);
        Shape2c.mirror = true;
        setRotation(Shape2c, 0F, 0F, 0F);
        Shape3d = new ModelRenderer(this, 72, 0);
        Shape3d.addBox(-11F, 0F, -1F, 22, 12, 2);
        Shape3d.setRotationPoint(0F, 12F, 0F);
        Shape3d.setTextureSize(128, 128);
        Shape3d.mirror = true;
        setRotation(Shape3d, 0F, 0.7853982F, 0F);
        Shape3 = new ModelRenderer(this, 72, 0);
        Shape3.addBox(-11F, 0F, -1F, 22, 12, 2);
        Shape3.setRotationPoint(0F, 12F, 0F);
        Shape3.setTextureSize(128, 128);
        Shape3.mirror = true;
        setRotation(Shape3, 0F, -0.7853982F, 0F);
        Shape3a = new ModelRenderer(this, 34, 0);
        Shape3a.addBox(-8F, -8F, -1F, 16, 16, 2);
        Shape3a.setRotationPoint(0F, 12F, 0F);
        Shape3a.setTextureSize(128, 128);
        Shape3a.mirror = true;
        setRotation(Shape3a, 0F, 0.7853982F, 0.7853982F);
        Shape3b = new ModelRenderer(this, 34, 0);
        Shape3b.addBox(-8F, -8F, -1F, 16, 16, 2);
        Shape3b.setRotationPoint(0F, 12F, 0F);
        Shape3b.setTextureSize(128, 128);
        Shape3b.mirror = true;
        setRotation(Shape3b, 0F, -0.7853982F, 0.7853982F);
        Shape4 = new ModelRenderer(this, 22, 74);
        Shape4.addBox(0F, 0F, 0F, 2, 7, 5);
        Shape4.setRotationPoint(-1F, -23F, 4F);
        Shape4.setTextureSize(128, 128);
        Shape4.mirror = true;
        setRotation(Shape4, -0.5235988F, 0F, 0F);
        Shape4a = new ModelRenderer(this, 0, 103);
        Shape4a.addBox(0F, 0F, 0F, 5, 8, 2);
        Shape4a.setRotationPoint(4F, -23F, -1F);
        Shape4a.setTextureSize(128, 128);
        Shape4a.mirror = true;
        setRotation(Shape4a, 0F, 0F, 0.5235988F);
        Shape4b = new ModelRenderer(this, 0, 81);
        Shape4b.addBox(-1F, 0F, 0F, 2, 4, 5);
        Shape4b.setRotationPoint(0F, -35F, -4F);
        Shape4b.setTextureSize(128, 128);
        Shape4b.mirror = true;
        setRotation(Shape4b, -0.5235988F, 3.141593F, 0F);
        Shape4c = new ModelRenderer(this, 0, 103);
        Shape4c.addBox(0F, 0F, -1F, 5, 8, 2);
        Shape4c.setRotationPoint(-4F, -23F, 0F);
        Shape4c.setTextureSize(128, 128);
        Shape4c.mirror = true;
        setRotation(Shape4c, 0F, 3.141593F, 0.5235988F);
        Shape5 = new ModelRenderer(this, 0, 74);
        Shape5.addBox(-1F, 0F, -8F, 2, 12, 16);
        Shape5.setRotationPoint(0F, -32F, 0F);
        Shape5.setTextureSize(128, 128);
        Shape5.mirror = true;
        setRotation(Shape5, 0F, 0F, 0F);
        Shape5a = new ModelRenderer(this, 0, 74);
        Shape5a.addBox(-1F, 0F, -8F, 2, 12, 16);
        Shape5a.setRotationPoint(0F, -32F, 0F);
        Shape5a.setTextureSize(128, 128);
        Shape5a.mirror = true;
        setRotation(Shape5a, 0F, 1.570796F, 0F);
        Shape4d = new ModelRenderer(this, 22, 74);
        Shape4d.addBox(-1F, 0F, 0F, 2, 8, 5);
        Shape4d.setRotationPoint(0F, -23F, -4F);
        Shape4d.setTextureSize(128, 128);
        Shape4d.mirror = true;
        setRotation(Shape4d, -0.5235988F, 3.141593F, 0F);
        Shape4e = new ModelRenderer(this, 0, 81);
        Shape4e.addBox(-1F, 0F, 0F, 2, 4, 5);
        Shape4e.setRotationPoint(0F, -35F, 4F);
        Shape4e.setTextureSize(128, 128);
        Shape4e.mirror = true;
        setRotation(Shape4e, -0.5235988F, 0F, 0F);
        Shape4f = new ModelRenderer(this, 0, 74);
        Shape4f.addBox(0F, 0F, -1F, 5, 4, 2);
        Shape4f.setRotationPoint(-4F, -35F, 0F);
        Shape4f.setTextureSize(128, 128);
        Shape4f.mirror = true;
        setRotation(Shape4f, 0F, 3.141593F, 0.5235988F);
        Shape4g = new ModelRenderer(this, 0, 74);
        Shape4g.addBox(0F, 0F, 0F, 5, 4, 2);
        Shape4g.setRotationPoint(4F, -35F, -1F);
        Shape4g.setTextureSize(128, 128);
        Shape4g.mirror = true;
        setRotation(Shape4g, 0F, 0F, 0.5235988F);
        Shape6 = new ModelRenderer(this, 72, 16);
        Shape6.addBox(0F, 0F, 0F, 10, 1, 10);
        Shape6.setRotationPoint(-5F, -37F, -5F);
        Shape6.setTextureSize(128, 128);
        Shape6.mirror = true;
        setRotation(Shape6, 0F, 0F, 0F);
        Shape6a = new ModelRenderer(this, 72, 16);
        Shape6a.addBox(0F, 0F, 0F, 10, 1, 10);
        Shape6a.setRotationPoint(-5F, -41F, -5F);
        Shape6a.setTextureSize(128, 128);
        Shape6a.mirror = true;
        setRotation(Shape6a, 0F, 0F, 0F);
        Shape7a = new ModelRenderer(this, 72, 29);
        Shape7a.addBox(0F, 0F, 0F, 8, 4, 8);
        Shape7a.setRotationPoint(-4F, -45F, -4F);
        Shape7a.setTextureSize(128, 128);
        Shape7a.mirror = true;
        setRotation(Shape7a, 0F, 0F, 0F);
        Shape7 = new ModelRenderer(this, 72, 43);
        Shape7.addBox(0F, 0F, 0F, 6, 2, 6);
        Shape7.setRotationPoint(-3F, -47F, -3F);
        Shape7.setTextureSize(128, 128);
        Shape7.mirror = true;
        setRotation(Shape7, 0F, 0F, 0F);
        Shape8 = new ModelRenderer(this, 63, 33);
        Shape8.addBox(0F, 0F, 0F, 2, 2, 2);
        Shape8.setRotationPoint(2F, -46F, -1F);
        Shape8.setTextureSize(128, 128);
        Shape8.mirror = true;
        setRotation(Shape8, 0F, 0F, -0.7853982F);
        Shape8a = new ModelRenderer(this, 63, 33);
        Shape8a.addBox(0F, 0F, 0F, 2, 2, 2);
        Shape8a.setRotationPoint(-5F, -46F, -1F);
        Shape8a.setTextureSize(128, 128);
        Shape8a.mirror = true;
        setRotation(Shape8a, 0F, 0F, -0.7853982F);
        Shape8b = new ModelRenderer(this, 63, 33);
        Shape8b.addBox(0F, 0F, 0F, 2, 2, 2);
        Shape8b.setRotationPoint(-1F, -46F, 2F);
        Shape8b.setTextureSize(128, 128);
        Shape8b.mirror = true;
        setRotation(Shape8b, 0.7853982F, 0F, 0F);
        Shape8c = new ModelRenderer(this, 63, 33);
        Shape8c.addBox(0F, 0F, 0F, 2, 2, 2);
        Shape8c.setRotationPoint(-1F, -46F, -5F);
        Shape8c.setTextureSize(128, 128);
        Shape8c.mirror = true;
        setRotation(Shape8c, 0.7853982F, 0F, 0F);
        Shape8d = new ModelRenderer(this, 72, 53);
        Shape8d.addBox(0F, 0F, 0F, 4, 2, 4);
        Shape8d.setRotationPoint(-2F, -49F, -2F);
        Shape8d.setTextureSize(128, 128);
        Shape8d.mirror = true;
        setRotation(Shape8d, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        this.render(f5);
    }

    @Override
    public void render(float f5)
    {
        Shape1.render(f5);
        Shape2.render(f5);
        Shape2a.render(f5);
        Shape2b.render(f5);
        Shape2c.render(f5);
        Shape3d.render(f5);
        Shape3.render(f5);
        Shape3a.render(f5);
        Shape3b.render(f5);
        Shape4.render(f5);
        Shape4a.render(f5);
        Shape4b.render(f5);
        Shape4c.render(f5);
        Shape5.render(f5);
        Shape5a.render(f5);
        Shape4d.render(f5);
        Shape4e.render(f5);
        Shape4f.render(f5);
        Shape4g.render(f5);
        Shape6.render(f5);
        Shape6a.render(f5);
        Shape7a.render(f5);
        Shape7.render(f5);
        Shape8.render(f5);
        Shape8a.render(f5);
        Shape8b.render(f5);
        Shape8c.render(f5);
        Shape8d.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
