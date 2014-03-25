package icbm.explosion.model.missiles;

import icbm.ModelICBM;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MMWuQi extends ModelICBM
{
    // fields
    ModelRenderer MAIN_MISSILE_MODULE;
    ModelRenderer PROPULSOR_MODULE;
    ModelRenderer WING_B_A_1;
    ModelRenderer WING_B_B_1;
    ModelRenderer WING_T_A_1;
    ModelRenderer WING_T_B_1;
    ModelRenderer WING_T_A_2;
    ModelRenderer WING_T_B_2;
    ModelRenderer WING_B_B_2;
    ModelRenderer WING_B_A_2;
    ModelRenderer WARHEAD_1;
    ModelRenderer WARHEAD_2;
    ModelRenderer WARHEAD_3;
    ModelRenderer WARHEAD_4;
    ModelRenderer WARHEAD_5;
    ModelRenderer SCREEN;
    ModelRenderer POLEDEPTH;
    ModelRenderer POLEWIDTH;
    ModelRenderer WINGTWIDTH;

    public MMWuQi()
    {
        textureWidth = 128;
        textureHeight = 128;

        MAIN_MISSILE_MODULE = new ModelRenderer(this, 0, 0);
        MAIN_MISSILE_MODULE.addBox(0F, 0F, 0F, 6, 50, 6);
        MAIN_MISSILE_MODULE.setRotationPoint(-3F, -26F, -3F);
        MAIN_MISSILE_MODULE.setTextureSize(128, 128);
        MAIN_MISSILE_MODULE.mirror = true;
        setRotation(MAIN_MISSILE_MODULE, 0F, 0F, 0F);
        PROPULSOR_MODULE = new ModelRenderer(this, 0, 57);
        PROPULSOR_MODULE.addBox(-5F, 0F, -5F, 10, 16, 10);
        PROPULSOR_MODULE.setRotationPoint(0F, 8F, 0F);
        PROPULSOR_MODULE.setTextureSize(128, 128);
        PROPULSOR_MODULE.mirror = true;
        setRotation(PROPULSOR_MODULE, 0F, 0.7853982F, 0F);
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
        WING_T_A_1 = new ModelRenderer(this, 59, 0);
        WING_T_A_1.addBox(-1F, 0F, 0F, 2, 10, 10);
        WING_T_A_1.setRotationPoint(0F, -26F, 0F);
        WING_T_A_1.setTextureSize(128, 128);
        WING_T_A_1.mirror = true;
        setRotation(WING_T_A_1, -0.7853982F, 0.7853982F, 0F);
        WING_T_B_1 = new ModelRenderer(this, 59, 0);
        WING_T_B_1.addBox(-1F, 0F, 0F, 2, 10, 10);
        WING_T_B_1.setRotationPoint(0F, -26F, 0F);
        WING_T_B_1.setTextureSize(128, 128);
        WING_T_B_1.mirror = true;
        setRotation(WING_T_B_1, -0.7853982F, -0.7853982F, 0F);
        WING_T_A_2 = new ModelRenderer(this, 25, 0);
        WING_T_A_2.addBox(-1F, 0F, -7F, 2, 12, 14);
        WING_T_A_2.setRotationPoint(0F, -19F, 0F);
        WING_T_A_2.setTextureSize(128, 128);
        WING_T_A_2.mirror = true;
        setRotation(WING_T_A_2, 0F, 0.7853982F, 0F);
        WING_T_B_2 = new ModelRenderer(this, 25, 0);
        WING_T_B_2.addBox(-1F, 0F, -7F, 2, 12, 14);
        WING_T_B_2.setRotationPoint(0F, -19F, 0F);
        WING_T_B_2.setTextureSize(128, 128);
        WING_T_B_2.mirror = true;
        setRotation(WING_T_B_2, 0F, -0.7853982F, 0F);
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
        WARHEAD_1 = new ModelRenderer(this, 0, 85);
        WARHEAD_1.addBox(-4F, 0F, -4F, 7, 7, 7);
        WARHEAD_1.setRotationPoint(0.5F, -29F, 0.5F);
        WARHEAD_1.setTextureSize(128, 128);
        WARHEAD_1.mirror = true;
        setRotation(WARHEAD_1, 0F, 0F, 0F);
        WARHEAD_2 = new ModelRenderer(this, 33, 85);
        WARHEAD_2.addBox(-3F, 0F, -3F, 5, 3, 5);
        WARHEAD_2.setRotationPoint(0.5F, -32F, 0.5F);
        WARHEAD_2.setTextureSize(128, 128);
        WARHEAD_2.mirror = true;
        setRotation(WARHEAD_2, 0F, 0F, 0F);
        WARHEAD_3 = new ModelRenderer(this, 59, 85);
        WARHEAD_3.addBox(-2F, 0F, -2F, 3, 3, 3);
        WARHEAD_3.setRotationPoint(0.5F, -35F, 0.5F);
        WARHEAD_3.setTextureSize(128, 128);
        WARHEAD_3.mirror = true;
        setRotation(WARHEAD_3, 0F, 0F, 0F);
        WARHEAD_4 = new ModelRenderer(this, 78, 85);
        WARHEAD_4.addBox(-1F, 0F, -1F, 2, 3, 2);
        WARHEAD_4.setRotationPoint(0F, -38F, 0F);
        WARHEAD_4.setTextureSize(128, 128);
        WARHEAD_4.mirror = true;
        setRotation(WARHEAD_4, 0F, 0F, 0F);
        WARHEAD_5 = new ModelRenderer(this, 89, 85);
        WARHEAD_5.addBox(0F, 0F, 0F, 1, 3, 1);
        WARHEAD_5.setRotationPoint(-0.5F, -41F, -0.5F);
        WARHEAD_5.setTextureSize(128, 128);
        WARHEAD_5.mirror = true;
        setRotation(WARHEAD_5, 0F, 0F, 0F);
        SCREEN = new ModelRenderer(this, 97, 0);
        SCREEN.addBox(0F, 0F, 0F, 4, 10, 8);
        SCREEN.setRotationPoint(-2F, -6F, -4F);
        SCREEN.setTextureSize(128, 128);
        SCREEN.mirror = true;
        setRotation(SCREEN, 0F, 0F, 0F);
        POLEDEPTH = new ModelRenderer(this, 45, 59);
        POLEDEPTH.addBox(0F, 0F, 0F, 2, 2, 10);
        POLEDEPTH.setRotationPoint(-1F, -31F, -5F);
        POLEDEPTH.setTextureSize(128, 128);
        POLEDEPTH.mirror = true;
        setRotation(POLEDEPTH, 0F, 0F, 0F);
        POLEWIDTH = new ModelRenderer(this, 45, 73);
        POLEWIDTH.addBox(0F, 0F, 0F, 10, 2, 2);
        POLEWIDTH.setRotationPoint(-5F, -31F, -1F);
        POLEWIDTH.setTextureSize(128, 128);
        POLEWIDTH.mirror = true;
        setRotation(POLEWIDTH, 0F, 0F, 0F);
        WINGTWIDTH = new ModelRenderer(this, 70, 57);
        WINGTWIDTH.addBox(0F, 0F, 0F, 14, 12, 2);
        WINGTWIDTH.setRotationPoint(-7F, -19F, -1F);
        WINGTWIDTH.setTextureSize(128, 128);
        WINGTWIDTH.mirror = true;
        setRotation(WINGTWIDTH, 0F, 0F, 0F);
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
        PROPULSOR_MODULE.render(f5);
        WING_B_A_1.render(f5);
        WING_B_B_1.render(f5);
        WING_T_A_1.render(f5);
        WING_T_B_1.render(f5);
        WING_T_A_2.render(f5);
        WING_T_B_2.render(f5);
        WING_B_B_2.render(f5);
        WING_B_A_2.render(f5);
        WARHEAD_1.render(f5);
        WARHEAD_2.render(f5);
        WARHEAD_3.render(f5);
        WARHEAD_4.render(f5);
        WARHEAD_5.render(f5);
        SCREEN.render(f5);
        POLEDEPTH.render(f5);
        POLEWIDTH.render(f5);
        WINGTWIDTH.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
