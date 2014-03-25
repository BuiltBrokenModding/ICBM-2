package icbm.explosion.model.missiles;

import icbm.ModelICBM;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MMDuQi extends ModelICBM
{
    // fields
    ModelRenderer MAIN_MISSILE_MODULE;
    ModelRenderer MOTOR_MODULE;
    ModelRenderer WING_B_A_1;
    ModelRenderer WING_B_B_1;
    ModelRenderer WING_T_B_1;
    ModelRenderer WING_T_A_1;
    ModelRenderer WING_T_B_2;
    ModelRenderer WING_T_A_2;
    ModelRenderer WING_B_B_2;
    ModelRenderer WING_B_A_2;
    ModelRenderer CHEMICAL_CONTROL_MODULE;
    ModelRenderer CHEMICAL_CONDUCT;
    ModelRenderer CHEM_WARHEAD_1;
    ModelRenderer CHEM_WARHEAD_2;

    public MMDuQi()
    {
        textureWidth = 128;
        textureHeight = 128;

        MAIN_MISSILE_MODULE = new ModelRenderer(this, 0, 0);
        MAIN_MISSILE_MODULE.addBox(0F, 0F, 0F, 6, 50, 6);
        MAIN_MISSILE_MODULE.setRotationPoint(-3F, -26F, -3F);
        MAIN_MISSILE_MODULE.setTextureSize(128, 128);
        MAIN_MISSILE_MODULE.mirror = true;
        setRotation(MAIN_MISSILE_MODULE, 0F, 0F, 0F);
        MOTOR_MODULE = new ModelRenderer(this, 0, 57);
        MOTOR_MODULE.addBox(-5F, 0F, -5F, 10, 16, 10);
        MOTOR_MODULE.setRotationPoint(0F, 8F, 0F);
        MOTOR_MODULE.setTextureSize(128, 128);
        MOTOR_MODULE.mirror = true;
        setRotation(MOTOR_MODULE, 0F, 0.7853982F, 0F);
        WING_B_A_1 = new ModelRenderer(this, 59, 26);
        WING_B_A_1.addBox(-1F, 0F, -9F, 2, 12, 18);
        WING_B_A_1.setRotationPoint(0F, 12F, 0F);
        WING_B_A_1.setTextureSize(128, 128);
        WING_B_A_1.mirror = true;
        setRotation(WING_B_A_1, 0F, 0.7853982F, 0F);
        WING_B_B_1 = new ModelRenderer(this, 59, 26);
        WING_B_B_1.addBox(-1F, 0F, -9F, 2, 12, 18);
        WING_B_B_1.setRotationPoint(0F, 12F, 0F);
        WING_B_B_1.setTextureSize(128, 128);
        WING_B_B_1.mirror = true;
        setRotation(WING_B_B_1, 0F, -0.7853982F, 0F);
        WING_T_B_1 = new ModelRenderer(this, 59, 0);
        WING_T_B_1.addBox(-1F, 0F, 0F, 2, 10, 10);
        WING_T_B_1.setRotationPoint(0F, -24F, 0F);
        WING_T_B_1.setTextureSize(128, 128);
        WING_T_B_1.mirror = true;
        setRotation(WING_T_B_1, -0.7853982F, 0.7853982F, 0F);
        WING_T_A_1 = new ModelRenderer(this, 59, 0);
        WING_T_A_1.addBox(-1F, 0F, 0F, 2, 10, 10);
        WING_T_A_1.setRotationPoint(0F, -24F, 0F);
        WING_T_A_1.setTextureSize(128, 128);
        WING_T_A_1.mirror = true;
        setRotation(WING_T_A_1, -0.7853982F, -0.7853982F, 0F);
        WING_T_B_2 = new ModelRenderer(this, 25, 0);
        WING_T_B_2.addBox(-1F, 0F, -7F, 2, 10, 14);
        WING_T_B_2.setRotationPoint(0F, -17F, 0F);
        WING_T_B_2.setTextureSize(128, 128);
        WING_T_B_2.mirror = true;
        setRotation(WING_T_B_2, 0F, 0.7853982F, 0F);
        WING_T_A_2 = new ModelRenderer(this, 25, 0);
        WING_T_A_2.addBox(-1F, 0F, -7F, 2, 10, 14);
        WING_T_A_2.setRotationPoint(0F, -17F, 0F);
        WING_T_A_2.setTextureSize(128, 128);
        WING_T_A_2.mirror = true;
        setRotation(WING_T_A_2, 0F, -0.7853982F, 0F);
        WING_B_B_2 = new ModelRenderer(this, 25, 26);
        WING_B_B_2.addBox(-1F, 0F, 0F, 2, 13, 13);
        WING_B_B_2.setRotationPoint(0F, 3F, 0F);
        WING_B_B_2.setTextureSize(128, 128);
        WING_B_B_2.mirror = true;
        setRotation(WING_B_B_2, -0.7853982F, -0.7853982F, 0F);
        WING_B_A_2 = new ModelRenderer(this, 25, 26);
        WING_B_A_2.addBox(-1F, 0F, 0F, 2, 13, 13);
        WING_B_A_2.setRotationPoint(0F, 3F, 0F);
        WING_B_A_2.setTextureSize(128, 128);
        WING_B_A_2.mirror = true;
        setRotation(WING_B_A_2, -0.7853982F, 0.7853982F, 0F);
        CHEMICAL_CONTROL_MODULE = new ModelRenderer(this, 0, 86);
        CHEMICAL_CONTROL_MODULE.addBox(-4F, 0F, -4F, 8, 8, 8);
        CHEMICAL_CONTROL_MODULE.setRotationPoint(0F, -4F, 0F);
        CHEMICAL_CONTROL_MODULE.setTextureSize(128, 128);
        CHEMICAL_CONTROL_MODULE.mirror = true;
        setRotation(CHEMICAL_CONTROL_MODULE, 0F, 0F, 0F);
        CHEMICAL_CONDUCT = new ModelRenderer(this, 34, 86);
        CHEMICAL_CONDUCT.addBox(0F, 0F, 0F, 1, 24, 1);
        CHEMICAL_CONDUCT.setRotationPoint(0F, -28F, -4F);
        CHEMICAL_CONDUCT.setTextureSize(128, 128);
        CHEMICAL_CONDUCT.mirror = true;
        setRotation(CHEMICAL_CONDUCT, 0F, 0F, 0F);
        CHEM_WARHEAD_1 = new ModelRenderer(this, 0, 103);
        CHEM_WARHEAD_1.addBox(-4F, 0F, -4F, 8, 5, 8);
        CHEM_WARHEAD_1.setRotationPoint(0F, -31F, 0F);
        CHEM_WARHEAD_1.setTextureSize(128, 128);
        CHEM_WARHEAD_1.mirror = true;
        setRotation(CHEM_WARHEAD_1, 0F, 0.7853982F, 0F);
        CHEM_WARHEAD_2 = new ModelRenderer(this, 40, 86);
        CHEM_WARHEAD_2.addBox(-3F, 0F, -3F, 6, 5, 6);
        CHEM_WARHEAD_2.setRotationPoint(0F, -36F, 0F);
        CHEM_WARHEAD_2.setTextureSize(128, 128);
        CHEM_WARHEAD_2.mirror = true;
        setRotation(CHEM_WARHEAD_2, 0F, 0.7853982F, 0F);
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
        MAIN_MISSILE_MODULE.render(f5);
        MOTOR_MODULE.render(f5);
        WING_B_A_1.render(f5);
        WING_B_B_1.render(f5);
        WING_T_B_1.render(f5);
        WING_T_A_1.render(f5);
        WING_T_B_2.render(f5);
        WING_T_A_2.render(f5);
        WING_B_B_2.render(f5);
        WING_B_A_2.render(f5);
        CHEMICAL_CONTROL_MODULE.render(f5);
        CHEMICAL_CONDUCT.render(f5);
        CHEM_WARHEAD_1.render(f5);
        CHEM_WARHEAD_2.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
