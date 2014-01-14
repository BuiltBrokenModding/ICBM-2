package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MFaSheDiRail2 extends ModelBase
{
    // fields
    ModelRenderer Shape9;
    ModelRenderer Shape20;
    ModelRenderer Shape21;
    ModelRenderer Shape16;

    public MFaSheDiRail2()
    {
        textureWidth = 256;
        textureHeight = 256;

        Shape9 = new ModelRenderer(this, 170, 0);
        Shape9.addBox(0F, 0F, 0F, 10, 37, 10);
        Shape9.setRotationPoint(-22F, -19F, -5F);
        Shape9.setTextureSize(256, 256);
        Shape9.mirror = true;
        setRotation(Shape9, 0F, 0F, 0F);
        Shape20 = new ModelRenderer(this, 170, 50);
        Shape20.addBox(0F, 0F, 0F, 1, 40, 2);
        Shape20.setRotationPoint(-19F, -22F, -6F);
        Shape20.setTextureSize(256, 256);
        Shape20.mirror = true;
        setRotation(Shape20, 0F, 0F, 0F);
        Shape21 = new ModelRenderer(this, 100, 0);
        Shape21.addBox(0F, 0F, 0F, 16, 6, 14);
        Shape21.setRotationPoint(-25F, 18F, -7F);
        Shape21.setTextureSize(256, 256);
        Shape21.mirror = true;
        setRotation(Shape21, 0F, 0F, 0F);
        Shape16 = new ModelRenderer(this, 170, 50);
        Shape16.addBox(0F, 0F, 0F, 1, 40, 2);
        Shape16.setRotationPoint(-16F, -22F, -6F);
        Shape16.setTextureSize(256, 256);
        Shape16.mirror = true;
        setRotation(Shape16, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Shape9.render(f5);
        Shape20.render(f5);
        Shape21.render(f5);
        Shape16.render(f5);
    }

    public void render(float f5)
    {
        Shape9.render(f5);
        Shape20.render(f5);
        Shape21.render(f5);
        Shape16.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
