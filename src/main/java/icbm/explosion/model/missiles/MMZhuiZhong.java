package icbm.explosion.model.missiles;

import icbm.core.prefab.render.ModelICBM;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MMZhuiZhong extends ModelICBM
{
	// fields
	ModelRenderer MAIN_MODULE;
	ModelRenderer MOTOR_MODULE;
	ModelRenderer WING_1A;
	ModelRenderer WING_2A;
	ModelRenderer WING_3B;
	ModelRenderer WING_4B;
	ModelRenderer WING_3A;
	ModelRenderer WING_4A;
	ModelRenderer WING_2B;
	ModelRenderer WING_1B;
	ModelRenderer WARHEAD_1;
	ModelRenderer WARHEAD_2;
	ModelRenderer WARHEAD_3;
	ModelRenderer WARHEAD_4;

	public MMZhuiZhong()
	{
		textureWidth = 128;
		textureHeight = 128;

		MAIN_MODULE = new ModelRenderer(this, 0, 0);
		MAIN_MODULE.addBox(0F, 0F, 0F, 6, 50, 6);
		MAIN_MODULE.setRotationPoint(-3F, -26F, -3F);
		MAIN_MODULE.setTextureSize(128, 128);
		MAIN_MODULE.mirror = true;
		setRotation(MAIN_MODULE, 0F, 0F, 0F);
		MOTOR_MODULE = new ModelRenderer(this, 0, 57);
		MOTOR_MODULE.addBox(-5F, 0F, -5F, 10, 16, 10);
		MOTOR_MODULE.setRotationPoint(0F, 8F, 0F);
		MOTOR_MODULE.setTextureSize(128, 128);
		MOTOR_MODULE.mirror = true;
		setRotation(MOTOR_MODULE, 0F, 0.7853982F, 0F);
		WING_1A = new ModelRenderer(this, 59, 26);
		WING_1A.addBox(-1F, 0F, -9F, 2, 12, 18);
		WING_1A.setRotationPoint(0F, 12F, 0F);
		WING_1A.setTextureSize(128, 128);
		WING_1A.mirror = true;
		setRotation(WING_1A, 0F, 0.7853982F, 0F);
		WING_2A = new ModelRenderer(this, 59, 26);
		WING_2A.addBox(-1F, 0F, -9F, 2, 12, 18);
		WING_2A.setRotationPoint(0F, 12F, 0F);
		WING_2A.setTextureSize(128, 128);
		WING_2A.mirror = true;
		setRotation(WING_2A, 0F, -0.7853982F, 0F);
		WING_3B = new ModelRenderer(this, 59, 0);
		WING_3B.addBox(-1F, 0F, 0F, 2, 10, 10);
		WING_3B.setRotationPoint(0F, -24F, 0F);
		WING_3B.setTextureSize(128, 128);
		WING_3B.mirror = true;
		setRotation(WING_3B, -0.7853982F, 0.7853982F, 0F);
		WING_4B = new ModelRenderer(this, 59, 0);
		WING_4B.addBox(-1F, 0F, 0F, 2, 10, 10);
		WING_4B.setRotationPoint(0F, -24F, 0F);
		WING_4B.setTextureSize(128, 128);
		WING_4B.mirror = true;
		setRotation(WING_4B, -0.7853982F, -0.7853982F, 0F);
		WING_3A = new ModelRenderer(this, 25, 0);
		WING_3A.addBox(-1F, 0F, -7F, 2, 10, 14);
		WING_3A.setRotationPoint(0F, -17F, 0F);
		WING_3A.setTextureSize(128, 128);
		WING_3A.mirror = true;
		setRotation(WING_3A, 0F, 0.7853982F, 0F);
		WING_4A = new ModelRenderer(this, 25, 0);
		WING_4A.addBox(-1F, 0F, -7F, 2, 10, 14);
		WING_4A.setRotationPoint(0F, -17F, 0F);
		WING_4A.setTextureSize(128, 128);
		WING_4A.mirror = true;
		setRotation(WING_4A, 0F, -0.7853982F, 0F);
		WING_2B = new ModelRenderer(this, 25, 26);
		WING_2B.addBox(-1F, 0F, 0F, 2, 13, 13);
		WING_2B.setRotationPoint(0F, 3F, 0F);
		WING_2B.setTextureSize(128, 128);
		WING_2B.mirror = true;
		setRotation(WING_2B, -0.7853982F, -0.7853982F, 0F);
		WING_1B = new ModelRenderer(this, 25, 26);
		WING_1B.addBox(-1F, 0F, 0F, 2, 13, 13);
		WING_1B.setRotationPoint(0F, 3F, 0F);
		WING_1B.setTextureSize(128, 128);
		WING_1B.mirror = true;
		setRotation(WING_1B, -0.7853982F, 0.7853982F, 0F);
		WARHEAD_1 = new ModelRenderer(this, 0, 85);
		WARHEAD_1.addBox(0F, 0F, 0F, 4, 6, 4);
		WARHEAD_1.setRotationPoint(-2F, -32F, -2F);
		WARHEAD_1.setTextureSize(128, 128);
		WARHEAD_1.mirror = true;
		setRotation(WARHEAD_1, 0F, 0F, 0F);
		WARHEAD_2 = new ModelRenderer(this, 0, 97);
		WARHEAD_2.addBox(0F, 0F, 0F, 6, 6, 6);
		WARHEAD_2.setRotationPoint(-3F, -38F, -3F);
		WARHEAD_2.setTextureSize(128, 128);
		WARHEAD_2.mirror = true;
		setRotation(WARHEAD_2, 0F, 0F, 0F);
		WARHEAD_3 = new ModelRenderer(this, 26, 97);
		WARHEAD_3.addBox(0F, 0F, 0F, 4, 3, 4);
		WARHEAD_3.setRotationPoint(-2F, -41F, -2F);
		WARHEAD_3.setTextureSize(128, 128);
		WARHEAD_3.mirror = true;
		setRotation(WARHEAD_3, 0F, 0F, 0F);
		WARHEAD_4 = new ModelRenderer(this, 26, 105);
		WARHEAD_4.addBox(0F, 0F, 0F, 2, 3, 2);
		WARHEAD_4.setRotationPoint(-1F, -44F, -1F);
		WARHEAD_4.setTextureSize(128, 128);
		WARHEAD_4.mirror = true;
		setRotation(WARHEAD_4, 0F, 0F, 0F);
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
		MOTOR_MODULE.render(f5);
		WING_1A.render(f5);
		WING_2A.render(f5);
		WING_3B.render(f5);
		WING_4B.render(f5);
		WING_3A.render(f5);
		WING_4A.render(f5);
		WING_2B.render(f5);
		WING_1B.render(f5);
		WARHEAD_1.render(f5);
		WARHEAD_2.render(f5);
		WARHEAD_3.render(f5);
		WARHEAD_4.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
