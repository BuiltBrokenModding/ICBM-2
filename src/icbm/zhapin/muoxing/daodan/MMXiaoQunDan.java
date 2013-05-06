package icbm.zhapin.muoxing.daodan;

import icbm.core.di.MICBM;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MMXiaoQunDan extends MICBM
{
	// fields
	ModelRenderer MAIN_MISSILE_MODULE;
	ModelRenderer MOTOR_MODULE;
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
	ModelRenderer SHRAPNEL_PIECE_1;
	ModelRenderer SHRAPNEL_PIECE_2;
	ModelRenderer SHRAPNEL_PIECE_3;
	ModelRenderer SHRAPNEL_PIECE_4;

	public MMXiaoQunDan()
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
		WING_T_A_1 = new ModelRenderer(this, 59, 0);
		WING_T_A_1.addBox(-1F, 0F, 0F, 2, 10, 10);
		WING_T_A_1.setRotationPoint(0F, -24F, 0F);
		WING_T_A_1.setTextureSize(128, 128);
		WING_T_A_1.mirror = true;
		setRotation(WING_T_A_1, -0.7853982F, 0.7853982F, 0F);
		WING_T_B_1 = new ModelRenderer(this, 59, 0);
		WING_T_B_1.addBox(-1F, 0F, 0F, 2, 10, 10);
		WING_T_B_1.setRotationPoint(0F, -24F, 0F);
		WING_T_B_1.setTextureSize(128, 128);
		WING_T_B_1.mirror = true;
		setRotation(WING_T_B_1, -0.7853982F, -0.7853982F, 0F);
		WING_T_A_2 = new ModelRenderer(this, 25, 0);
		WING_T_A_2.addBox(-1F, 0F, -7F, 2, 10, 14);
		WING_T_A_2.setRotationPoint(0F, -17F, 0F);
		WING_T_A_2.setTextureSize(128, 128);
		WING_T_A_2.mirror = true;
		setRotation(WING_T_A_2, 0F, 0.7853982F, 0F);
		WING_T_B_2 = new ModelRenderer(this, 25, 0);
		WING_T_B_2.addBox(-1F, 0F, -7F, 2, 10, 14);
		WING_T_B_2.setRotationPoint(0F, -17F, 0F);
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
		WARHEAD_1 = new ModelRenderer(this, 0, 87);
		WARHEAD_1.addBox(0F, 0F, 0F, 8, 4, 8);
		WARHEAD_1.setRotationPoint(-4F, -30F, -4F);
		WARHEAD_1.setTextureSize(128, 128);
		WARHEAD_1.mirror = true;
		setRotation(WARHEAD_1, 0F, 0F, 0F);
		WARHEAD_2 = new ModelRenderer(this, 0, 101);
		WARHEAD_2.addBox(0F, 0F, 0F, 6, 4, 6);
		WARHEAD_2.setRotationPoint(-3F, -34F, -3F);
		WARHEAD_2.setTextureSize(128, 128);
		WARHEAD_2.mirror = true;
		setRotation(WARHEAD_2, 0F, 0F, 0F);
		SHRAPNEL_PIECE_1 = new ModelRenderer(this, 0, 112);
		SHRAPNEL_PIECE_1.addBox(0F, 0F, 0F, 10, 2, 2);
		SHRAPNEL_PIECE_1.setRotationPoint(-5F, -29F, -1F);
		SHRAPNEL_PIECE_1.setTextureSize(128, 128);
		SHRAPNEL_PIECE_1.mirror = true;
		setRotation(SHRAPNEL_PIECE_1, 0F, 0F, 0F);
		SHRAPNEL_PIECE_2 = new ModelRenderer(this, 27, 112);
		SHRAPNEL_PIECE_2.addBox(0F, 0F, 0F, 2, 2, 10);
		SHRAPNEL_PIECE_2.setRotationPoint(-1F, -29F, -5F);
		SHRAPNEL_PIECE_2.setTextureSize(128, 128);
		SHRAPNEL_PIECE_2.mirror = true;
		setRotation(SHRAPNEL_PIECE_2, 0F, 0F, 0F);
		SHRAPNEL_PIECE_3 = new ModelRenderer(this, 0, 119);
		SHRAPNEL_PIECE_3.addBox(0F, 0F, 0F, 8, 2, 2);
		SHRAPNEL_PIECE_3.setRotationPoint(-4F, -33F, -1F);
		SHRAPNEL_PIECE_3.setTextureSize(128, 128);
		SHRAPNEL_PIECE_3.mirror = true;
		setRotation(SHRAPNEL_PIECE_3, 0F, 0F, 0F);
		SHRAPNEL_PIECE_4 = new ModelRenderer(this, 53, 112);
		SHRAPNEL_PIECE_4.addBox(0F, 0F, 0F, 2, 2, 8);
		SHRAPNEL_PIECE_4.setRotationPoint(-1F, -33F, -4F);
		SHRAPNEL_PIECE_4.setTextureSize(128, 128);
		SHRAPNEL_PIECE_4.mirror = true;
		setRotation(SHRAPNEL_PIECE_4, 0F, 0F, 0F);
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
		WING_T_A_1.render(f5);
		WING_T_B_1.render(f5);
		WING_T_A_2.render(f5);
		WING_T_B_2.render(f5);
		WING_B_B_2.render(f5);
		WING_B_A_2.render(f5);
		WARHEAD_1.render(f5);
		WARHEAD_2.render(f5);
		SHRAPNEL_PIECE_1.render(f5);
		SHRAPNEL_PIECE_2.render(f5);
		SHRAPNEL_PIECE_3.render(f5);
		SHRAPNEL_PIECE_4.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
