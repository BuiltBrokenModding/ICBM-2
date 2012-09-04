package icbm.models;

import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelMissileCluster extends ModelBase
{
	//fields
	 ModelRenderer MAIN_BODY;
    ModelRenderer BASE_WINGS_1;
    ModelRenderer BASE_WINGS_2;
    ModelRenderer UPPER_WINGS_1;
    ModelRenderer UPPER_WINGS_2;
    ModelRenderer WARHEAD_1_BASE;
    ModelRenderer WARHEAD_2_HEAD;
    ModelRenderer PROPULSION_MODULE_1;
    ModelRenderer PROPULSION_MODULE_2;
    ModelRenderer WARHEAD_2_BASE;
    ModelRenderer WARHEAD_1_HEAD;
    ModelRenderer WARHEAD_3_HEAD;
    ModelRenderer WARHEAD_3_BASE;
    ModelRenderer WARHEAD_4_BASE;
    ModelRenderer WARHEAD_4_HEAD;
    ModelRenderer Shape1;
    ModelRenderer Shape2;
  
  public ModelMissileCluster()
  {
    textureWidth = 128;
    textureHeight = 128;
    
      MAIN_BODY = new ModelRenderer(this, 0, 0);
      MAIN_BODY.addBox(0F, 0F, 0F, 6, 40, 6);
      MAIN_BODY.setRotationPoint(-3F, -16F, -3F);
      MAIN_BODY.setTextureSize(128, 128);
      MAIN_BODY.mirror = true;
      setRotation(MAIN_BODY, 0F, 0F, 0F);
      BASE_WINGS_1 = new ModelRenderer(this, 25, 0);
      BASE_WINGS_1.addBox(0F, 0F, 0F, 2, 5, 18);
      BASE_WINGS_1.setRotationPoint(-1F, 19F, -9F);
      BASE_WINGS_1.setTextureSize(128, 128);
      BASE_WINGS_1.mirror = true;
      setRotation(BASE_WINGS_1, 0F, 0F, 0F);
      BASE_WINGS_2 = new ModelRenderer(this, 25, 30);
      BASE_WINGS_2.addBox(0F, 0F, 0F, 18, 5, 2);
      BASE_WINGS_2.setRotationPoint(-9F, 19F, -1F);
      BASE_WINGS_2.setTextureSize(128, 128);
      BASE_WINGS_2.mirror = true;
      setRotation(BASE_WINGS_2, 0F, 0F, 0F);
      UPPER_WINGS_1 = new ModelRenderer(this, 0, 65);
      UPPER_WINGS_1.addBox(0F, 0F, 0F, 10, 4, 2);
      UPPER_WINGS_1.setRotationPoint(-5F, -13F, -1F);
      UPPER_WINGS_1.setTextureSize(128, 128);
      UPPER_WINGS_1.mirror = true;
      setRotation(UPPER_WINGS_1, 0F, 0F, 0F);
      UPPER_WINGS_2 = new ModelRenderer(this, 0, 50);
      UPPER_WINGS_2.addBox(0F, 0F, 0F, 2, 4, 10);
      UPPER_WINGS_2.setRotationPoint(-1F, -13F, -5F);
      UPPER_WINGS_2.setTextureSize(128, 128);
      UPPER_WINGS_2.mirror = true;
      setRotation(UPPER_WINGS_2, 0F, 0F, 0F);
      WARHEAD_1_BASE = new ModelRenderer(this, 0, 80);
      WARHEAD_1_BASE.addBox(0F, 0F, 0F, 4, 16, 4);
      WARHEAD_1_BASE.setRotationPoint(-7F, 8F, -7F);
      WARHEAD_1_BASE.setTextureSize(128, 128);
      WARHEAD_1_BASE.mirror = true;
      setRotation(WARHEAD_1_BASE, 0F, 0F, 0F);
      WARHEAD_2_HEAD = new ModelRenderer(this, 0, 73);
      WARHEAD_2_HEAD.addBox(0F, 0F, 0F, 2, 4, 2);
      WARHEAD_2_HEAD.setRotationPoint(4F, 4F, -6F);
      WARHEAD_2_HEAD.setTextureSize(128, 128);
      WARHEAD_2_HEAD.mirror = true;
      setRotation(WARHEAD_2_HEAD, 0F, 0F, 0F);
      PROPULSION_MODULE_1 = new ModelRenderer(this, 70, 0);
      PROPULSION_MODULE_1.addBox(0F, 0F, 0F, 10, 9, 10);
      PROPULSION_MODULE_1.setRotationPoint(-5F, 15F, -5F);
      PROPULSION_MODULE_1.setTextureSize(128, 128);
      PROPULSION_MODULE_1.mirror = true;
      setRotation(PROPULSION_MODULE_1, 0F, 0F, 0F);
      PROPULSION_MODULE_2 = new ModelRenderer(this, 70, 20);
      PROPULSION_MODULE_2.addBox(0F, 0F, 0F, 8, 4, 8);
      PROPULSION_MODULE_2.setRotationPoint(-4F, 11F, -4F);
      PROPULSION_MODULE_2.setTextureSize(128, 128);
      PROPULSION_MODULE_2.mirror = true;
      setRotation(PROPULSION_MODULE_2, 0F, 0F, 0F);
      WARHEAD_2_BASE = new ModelRenderer(this, 0, 80);
      WARHEAD_2_BASE.addBox(0F, 0F, 0F, 4, 16, 4);
      WARHEAD_2_BASE.setRotationPoint(3F, 8F, -7F);
      WARHEAD_2_BASE.setTextureSize(128, 128);
      WARHEAD_2_BASE.mirror = true;
      setRotation(WARHEAD_2_BASE, 0F, 0F, 0F);
      WARHEAD_1_HEAD = new ModelRenderer(this, 0, 73);
      WARHEAD_1_HEAD.addBox(0F, 0F, 0F, 2, 4, 2);
      WARHEAD_1_HEAD.setRotationPoint(-6F, 4F, -6F);
      WARHEAD_1_HEAD.setTextureSize(128, 128);
      WARHEAD_1_HEAD.mirror = true;
      setRotation(WARHEAD_1_HEAD, 0F, 0F, 0F);
      WARHEAD_3_HEAD = new ModelRenderer(this, 0, 73);
      WARHEAD_3_HEAD.addBox(0F, 0F, 0F, 2, 4, 2);
      WARHEAD_3_HEAD.setRotationPoint(-6F, 4F, 4F);
      WARHEAD_3_HEAD.setTextureSize(128, 128);
      WARHEAD_3_HEAD.mirror = true;
      setRotation(WARHEAD_3_HEAD, 0F, 0F, 0F);
      WARHEAD_3_BASE = new ModelRenderer(this, 0, 80);
      WARHEAD_3_BASE.addBox(0F, 0F, 0F, 4, 16, 4);
      WARHEAD_3_BASE.setRotationPoint(-7F, 8F, 3F);
      WARHEAD_3_BASE.setTextureSize(128, 128);
      WARHEAD_3_BASE.mirror = true;
      setRotation(WARHEAD_3_BASE, 0F, 0F, 0F);
      WARHEAD_4_BASE = new ModelRenderer(this, 0, 80);
      WARHEAD_4_BASE.addBox(0F, 0F, 0F, 4, 16, 4);
      WARHEAD_4_BASE.setRotationPoint(3F, 8F, 3F);
      WARHEAD_4_BASE.setTextureSize(128, 128);
      WARHEAD_4_BASE.mirror = true;
      setRotation(WARHEAD_4_BASE, 0F, 0F, 0F);
      WARHEAD_4_HEAD = new ModelRenderer(this, 0, 73);
      WARHEAD_4_HEAD.addBox(0F, 0F, 0F, 2, 4, 2);
      WARHEAD_4_HEAD.setRotationPoint(4F, 4F, 4F);
      WARHEAD_4_HEAD.setTextureSize(128, 128);
      WARHEAD_4_HEAD.mirror = true;
      setRotation(WARHEAD_4_HEAD, 0F, 0F, 0F);
      Shape1 = new ModelRenderer(this, 25, 38);
      Shape1.addBox(0F, 0F, 0F, 4, 4, 4);
      Shape1.setRotationPoint(-2F, -20F, -2F);
      Shape1.setTextureSize(128, 128);
      Shape1.mirror = true;
      setRotation(Shape1, 0F, 0F, 0F);
      Shape2 = new ModelRenderer(this, 42, 38);
      Shape2.addBox(1F, 0F, 0F, 2, 3, 2);
      Shape2.setRotationPoint(-2F, -23F, -1F);
      Shape2.setTextureSize(128, 128);
      Shape2.mirror = true;
      setRotation(Shape2, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5);
    MAIN_BODY.render(f5);
    BASE_WINGS_1.render(f5);
    BASE_WINGS_2.render(f5);
    UPPER_WINGS_1.render(f5);
    UPPER_WINGS_2.render(f5);
    WARHEAD_1_BASE.render(f5);
    WARHEAD_2_HEAD.render(f5);
    PROPULSION_MODULE_1.render(f5);
    PROPULSION_MODULE_2.render(f5);
    WARHEAD_2_BASE.render(f5);
    WARHEAD_1_HEAD.render(f5);
    WARHEAD_3_HEAD.render(f5);
    WARHEAD_3_BASE.render(f5);
    WARHEAD_4_BASE.render(f5);
    WARHEAD_4_HEAD.render(f5);
    Shape1.render(f5);
    Shape2.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5);
  }
}