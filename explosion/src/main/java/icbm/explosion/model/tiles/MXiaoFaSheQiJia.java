package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MXiaoFaSheQiJia extends ModelBase
{

    ModelRenderer CLAMP_1_ROT;
    ModelRenderer CLAMP_2_ROT;
    ModelRenderer COUNTERBALANCE_1_ROT;
    ModelRenderer COUNTERBALANCE_2_ROT;
    ModelRenderer RAIL_1_ROT;
    ModelRenderer RAIL_2_ROT;
    ModelRenderer TORQUE_SUPPORT_ROT;

    public MXiaoFaSheQiJia()
    {
        textureWidth = 128;
        textureHeight = 128;

        CLAMP_1_ROT = new ModelRenderer(this, 20, 0);
        CLAMP_1_ROT.addBox(-1F, -1F, -17F, 6, 6, 1);
        CLAMP_1_ROT.setRotationPoint(0F, 13F, 0F);
        CLAMP_1_ROT.setTextureSize(128, 128);
        CLAMP_1_ROT.mirror = true;
        setRotation(CLAMP_1_ROT, 0F, 0F, -2.356194F);
        CLAMP_2_ROT = new ModelRenderer(this, 20, 0);
        CLAMP_2_ROT.addBox(-1F, -1F, 0F, 6, 6, 1);
        CLAMP_2_ROT.setRotationPoint(0F, 13F, 0F);
        CLAMP_2_ROT.setTextureSize(128, 128);
        CLAMP_2_ROT.mirror = true;
        setRotation(CLAMP_2_ROT, 0F, 0F, -2.356194F);
        COUNTERBALANCE_1_ROT = new ModelRenderer(this, 84, 0);
        COUNTERBALANCE_1_ROT.addBox(1F, -4F, 1F, 4, 4, 8);
        COUNTERBALANCE_1_ROT.setRotationPoint(0F, 14F, 0F);
        COUNTERBALANCE_1_ROT.setTextureSize(128, 128);
        COUNTERBALANCE_1_ROT.mirror = true;
        setRotation(COUNTERBALANCE_1_ROT, 0F, 0F, 0F);
        COUNTERBALANCE_2_ROT = new ModelRenderer(this, 84, 0);
        COUNTERBALANCE_2_ROT.addBox(-5F, -4F, 1F, 4, 4, 8);
        COUNTERBALANCE_2_ROT.setRotationPoint(0F, 14F, 0F);
        COUNTERBALANCE_2_ROT.setTextureSize(128, 128);
        COUNTERBALANCE_2_ROT.mirror = true;
        setRotation(COUNTERBALANCE_2_ROT, 0F, 0F, 0F);
        RAIL_1_ROT = new ModelRenderer(this, 65, 13);
        RAIL_1_ROT.addBox(-5F, -1F, -17F, 4, 1, 18);
        RAIL_1_ROT.setRotationPoint(0F, 14F, 0F);
        RAIL_1_ROT.setTextureSize(128, 128);
        RAIL_1_ROT.mirror = true;
        setRotation(RAIL_1_ROT, 0F, 0F, 0.7853982F);
        RAIL_2_ROT = new ModelRenderer(this, 65, 13);
        RAIL_2_ROT.addBox(-5F, 0F, -17F, 4, 1, 18);
        RAIL_2_ROT.setRotationPoint(0F, 14F, 0F);
        RAIL_2_ROT.setTextureSize(128, 128);
        RAIL_2_ROT.mirror = true;
        setRotation(RAIL_2_ROT, 0F, 0F, 2.356194F);
        TORQUE_SUPPORT_ROT = new ModelRenderer(this, 47, 17);
        TORQUE_SUPPORT_ROT.addBox(-2F, 0F, -2F, 4, 2, 4);
        TORQUE_SUPPORT_ROT.setRotationPoint(0F, 13F, 0F);
        TORQUE_SUPPORT_ROT.setTextureSize(128, 128);
        TORQUE_SUPPORT_ROT.mirror = true;
        setRotation(TORQUE_SUPPORT_ROT, 0F, 0F, 0F);
    }

    public void render(float f5)
    {
        CLAMP_1_ROT.render(f5);
        CLAMP_2_ROT.render(f5);
        COUNTERBALANCE_1_ROT.render(f5);
        COUNTERBALANCE_2_ROT.render(f5);
        RAIL_1_ROT.render(f5);
        RAIL_2_ROT.render(f5);
        TORQUE_SUPPORT_ROT.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

}
