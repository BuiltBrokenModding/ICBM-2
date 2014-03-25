package icbm.explosion.model.missiles;

import icbm.ModelICBM;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEndothermicMissile extends ModelICBM
{
    // fields
    ModelRenderer MAIN_MODULE;
    ModelRenderer MOTOR_MODULE_1;
    ModelRenderer MOTOR_MODULE_2;
    ModelRenderer MOTOR_MODULE_3;
    ModelRenderer MOTOR_MODULE_4;
    ModelRenderer C1;
    ModelRenderer C2;
    ModelRenderer C3;
    ModelRenderer C4;
    ModelRenderer T1;
    ModelRenderer T2;
    ModelRenderer T3;
    ModelRenderer T4;
    ModelRenderer WING_1A;
    ModelRenderer WING_2A;
    ModelRenderer WING_1B;
    ModelRenderer WING_2B;
    ModelRenderer WING_3A;
    ModelRenderer WING_3B;
    ModelRenderer WING_4B;
    ModelRenderer WING_4A;
    ModelRenderer TOP;
    ModelRenderer Shape1;
    ModelRenderer Shape1a;
    ModelRenderer Shape1b;
    ModelRenderer Shape1c;
    ModelRenderer Shape1d;
    ModelRenderer Shape1e;

    public ModelEndothermicMissile()
    {
        textureWidth = 128;
        textureHeight = 128;

        MAIN_MODULE = new ModelRenderer(this, 0, 0);
        MAIN_MODULE.addBox(0F, 0F, 0F, 8, 70, 8);
        MAIN_MODULE.setRotationPoint(-4F, -63F, -4F);
        MAIN_MODULE.setTextureSize(128, 128);
        MAIN_MODULE.mirror = true;
        setRotation(MAIN_MODULE, 0F, 0F, 0F);
        MOTOR_MODULE_1 = new ModelRenderer(this, 0, 79);
        MOTOR_MODULE_1.addBox(0F, 0F, 0F, 6, 20, 6);
        MOTOR_MODULE_1.setRotationPoint(-8F, 0F, -8F);
        MOTOR_MODULE_1.setTextureSize(128, 128);
        MOTOR_MODULE_1.mirror = true;
        setRotation(MOTOR_MODULE_1, 0F, 0F, 0F);
        MOTOR_MODULE_2 = new ModelRenderer(this, 0, 79);
        MOTOR_MODULE_2.addBox(0F, 0F, 0F, 6, 20, 6);
        MOTOR_MODULE_2.setRotationPoint(-8F, 0F, 2F);
        MOTOR_MODULE_2.setTextureSize(128, 128);
        MOTOR_MODULE_2.mirror = true;
        setRotation(MOTOR_MODULE_2, 0F, 0F, 0F);
        MOTOR_MODULE_3 = new ModelRenderer(this, 0, 79);
        MOTOR_MODULE_3.addBox(0F, 0F, 0F, 6, 20, 6);
        MOTOR_MODULE_3.setRotationPoint(2F, 0F, -8F);
        MOTOR_MODULE_3.setTextureSize(128, 128);
        MOTOR_MODULE_3.mirror = true;
        setRotation(MOTOR_MODULE_3, 0F, 0F, 0F);
        MOTOR_MODULE_4 = new ModelRenderer(this, 0, 79);
        MOTOR_MODULE_4.addBox(0F, 0F, 0F, 6, 20, 6);
        MOTOR_MODULE_4.setRotationPoint(2F, 0F, 2F);
        MOTOR_MODULE_4.setTextureSize(128, 128);
        MOTOR_MODULE_4.mirror = true;
        setRotation(MOTOR_MODULE_4, 0F, 0F, 0F);
        C1 = new ModelRenderer(this, 0, 106);
        C1.addBox(0F, 0F, 0F, 2, 1, 2);
        C1.setRotationPoint(-6F, 20F, -6F);
        C1.setTextureSize(128, 128);
        C1.mirror = true;
        setRotation(C1, 0F, 0F, 0F);
        C2 = new ModelRenderer(this, 0, 106);
        C2.addBox(0F, 0F, 0F, 2, 1, 2);
        C2.setRotationPoint(-6F, 20F, 4F);
        C2.setTextureSize(128, 128);
        C2.mirror = true;
        setRotation(C2, 0F, 0F, 0F);
        C3 = new ModelRenderer(this, 0, 106);
        C3.addBox(0F, 0F, 0F, 2, 1, 2);
        C3.setRotationPoint(4F, 20F, -6F);
        C3.setTextureSize(128, 128);
        C3.mirror = true;
        setRotation(C3, 0F, 0F, 0F);
        C4 = new ModelRenderer(this, 0, 106);
        C4.addBox(0F, 0F, 0F, 2, 1, 2);
        C4.setRotationPoint(4F, 20F, 4F);
        C4.setTextureSize(128, 128);
        C4.mirror = true;
        setRotation(C4, 0F, 0F, 0F);
        T1 = new ModelRenderer(this, 0, 111);
        T1.addBox(0F, 0F, 0F, 4, 3, 4);
        T1.setRotationPoint(-7F, 21F, -7F);
        T1.setTextureSize(128, 128);
        T1.mirror = true;
        setRotation(T1, 0F, 0F, 0F);
        T2 = new ModelRenderer(this, 0, 111);
        T2.addBox(0F, 0F, 0F, 4, 3, 4);
        T2.setRotationPoint(-7F, 21F, 3F);
        T2.setTextureSize(128, 128);
        T2.mirror = true;
        setRotation(T2, 0F, 0F, 0F);
        T3 = new ModelRenderer(this, 0, 111);
        T3.addBox(0F, 0F, 0F, 4, 3, 4);
        T3.setRotationPoint(3F, 21F, -7F);
        T3.setTextureSize(128, 128);
        T3.mirror = true;
        setRotation(T3, 0F, 0F, 0F);
        T4 = new ModelRenderer(this, 0, 111);
        T4.addBox(0F, 0F, 0F, 4, 3, 4);
        T4.setRotationPoint(3F, 21F, 3F);
        T4.setTextureSize(128, 128);
        T4.mirror = true;
        setRotation(T4, 0F, 0F, 0F);
        WING_1A = new ModelRenderer(this, 43, 15);
        WING_1A.addBox(-1F, 0F, 0F, 2, 12, 12);
        WING_1A.setRotationPoint(0F, -10F, 0F);
        WING_1A.setTextureSize(128, 128);
        WING_1A.mirror = true;
        setRotation(WING_1A, -0.7853982F, 0F, 0F);
        WING_2A = new ModelRenderer(this, 43, 0);
        WING_2A.addBox(0F, 0F, -1F, 12, 12, 2);
        WING_2A.setRotationPoint(0F, -10F, 0F);
        WING_2A.setTextureSize(128, 128);
        WING_2A.mirror = true;
        setRotation(WING_2A, 0F, 0F, 0.7853982F);
        WING_1B = new ModelRenderer(this, 72, 28);
        WING_1B.addBox(-1F, 0F, -8F, 2, 25, 16);
        WING_1B.setRotationPoint(0F, -2F, 0F);
        WING_1B.setTextureSize(128, 128);
        WING_1B.mirror = true;
        setRotation(WING_1B, 0F, 0F, 0F);
        WING_2B = new ModelRenderer(this, 72, 0);
        WING_2B.addBox(-8F, 0F, -1F, 16, 25, 2);
        WING_2B.setRotationPoint(0F, -2F, 0F);
        WING_2B.setTextureSize(128, 128);
        WING_2B.mirror = true;
        setRotation(WING_2B, 0F, 0F, 0F);
        WING_3A = new ModelRenderer(this, 34, 55);
        WING_3A.addBox(-1F, 0F, -8F, 2, 10, 16);
        WING_3A.setRotationPoint(0F, -41F, 0F);
        WING_3A.setTextureSize(128, 128);
        WING_3A.mirror = true;
        setRotation(WING_3A, 0F, 0F, 0F);
        WING_3B = new ModelRenderer(this, 34, 82);
        WING_3B.addBox(-1F, -6F, -6F, 2, 12, 12);
        WING_3B.setRotationPoint(0F, -41F, 0F);
        WING_3B.setTextureSize(128, 128);
        WING_3B.mirror = true;
        setRotation(WING_3B, 0.7853982F, 0F, 0F);
        WING_4B = new ModelRenderer(this, 34, 41);
        WING_4B.addBox(-8F, 0F, -1F, 16, 10, 2);
        WING_4B.setRotationPoint(0F, -41F, 0F);
        WING_4B.setTextureSize(128, 128);
        WING_4B.mirror = true;
        setRotation(WING_4B, 0F, 0F, 0F);
        WING_4A = new ModelRenderer(this, 34, 107);
        WING_4A.addBox(-6F, -6F, -1F, 12, 12, 2);
        WING_4A.setRotationPoint(0F, -41F, 0F);
        WING_4A.setTextureSize(128, 128);
        WING_4A.mirror = true;
        setRotation(WING_4A, 0F, 0F, -0.7853982F);
        TOP = new ModelRenderer(this, 72, 70);
        TOP.addBox(0F, 0F, 0F, 10, 10, 10);
        TOP.setRotationPoint(-5F, -64F, -5F);
        TOP.setTextureSize(128, 128);
        TOP.mirror = true;
        setRotation(TOP, 0F, 0F, 0F);
        Shape1 = new ModelRenderer(this, 108, 92);
        Shape1.addBox(0F, 0F, 0F, 2, 6, 2);
        Shape1.setRotationPoint(-4F, -70F, -4F);
        Shape1.setTextureSize(128, 128);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
        Shape1a = new ModelRenderer(this, 108, 92);
        Shape1a.addBox(0F, 0F, 0F, 2, 6, 2);
        Shape1a.setRotationPoint(-4F, -70F, 2F);
        Shape1a.setTextureSize(128, 128);
        Shape1a.mirror = true;
        setRotation(Shape1a, 0F, 0F, 0F);
        Shape1b = new ModelRenderer(this, 108, 92);
        Shape1b.addBox(0F, 0F, 0F, 2, 6, 2);
        Shape1b.setRotationPoint(2F, -70F, -4F);
        Shape1b.setTextureSize(128, 128);
        Shape1b.mirror = true;
        setRotation(Shape1b, 0F, 0F, 0F);
        Shape1c = new ModelRenderer(this, 108, 92);
        Shape1c.addBox(0F, 0F, 0F, 2, 6, 2);
        Shape1c.setRotationPoint(2F, -70F, 2F);
        Shape1c.setTextureSize(128, 128);
        Shape1c.mirror = true;
        setRotation(Shape1c, 0F, 0F, 0F);
        Shape1d = new ModelRenderer(this, 64, 92);
        Shape1d.addBox(0F, 0F, 0F, 6, 10, 6);
        Shape1d.setRotationPoint(-3F, -74F, -3F);
        Shape1d.setTextureSize(128, 128);
        Shape1d.mirror = true;
        setRotation(Shape1d, 0F, 0F, 0F);
        Shape1e = new ModelRenderer(this, 90, 92);
        Shape1e.addBox(0F, 0F, 0F, 4, 6, 4);
        Shape1e.setRotationPoint(-2F, -80F, -2F);
        Shape1e.setTextureSize(128, 128);
        Shape1e.mirror = true;
        setRotation(Shape1e, 0F, 0F, 0F);
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
        MAIN_MODULE.render(f5);
        MOTOR_MODULE_1.render(f5);
        MOTOR_MODULE_2.render(f5);
        MOTOR_MODULE_3.render(f5);
        MOTOR_MODULE_4.render(f5);
        C1.render(f5);
        C2.render(f5);
        C3.render(f5);
        C4.render(f5);
        T1.render(f5);
        T2.render(f5);
        T3.render(f5);
        T4.render(f5);
        WING_1A.render(f5);
        WING_2A.render(f5);
        WING_1B.render(f5);
        WING_2B.render(f5);
        WING_3A.render(f5);
        WING_3B.render(f5);
        WING_4B.render(f5);
        WING_4A.render(f5);
        TOP.render(f5);
        Shape1.render(f5);
        Shape1a.render(f5);
        Shape1b.render(f5);
        Shape1c.render(f5);
        Shape1d.render(f5);
        Shape1e.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
