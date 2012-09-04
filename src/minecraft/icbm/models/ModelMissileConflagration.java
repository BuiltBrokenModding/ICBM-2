package icbm.models;

import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelMissileConflagration extends ModelBase
{
  //fields
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;
    ModelRenderer Shape6;
    ModelRenderer Shape7;
    ModelRenderer Shape8;
    ModelRenderer Shape9;
    ModelRenderer Shape10;
    ModelRenderer Shape11;
  
  public ModelMissileConflagration()
  {
    textureWidth = 128;
    textureHeight = 128;
    
      Shape1 = new ModelRenderer(this, 0, 0);
      Shape1.addBox(0F, 0F, 0F, 6, 70, 6);
      Shape1.setRotationPoint(-3F, -46F, -3F);
      Shape1.setTextureSize(128, 128);
      Shape1.mirror = true;
      setRotation(Shape1, 0F, 0F, 0F);
      Shape2 = new ModelRenderer(this, 25, 0);
      Shape2.addBox(0F, 0F, 0F, 2, 5, 18);
      Shape2.setRotationPoint(-1F, 19F, -9F);
      Shape2.setTextureSize(128, 128);
      Shape2.mirror = true;
      setRotation(Shape2, 0F, 0F, 0F);
      Shape3 = new ModelRenderer(this, 25, 25);
      Shape3.addBox(0F, 0F, 0F, 18, 5, 2);
      Shape3.setRotationPoint(-9F, 19F, -1F);
      Shape3.setTextureSize(128, 128);
      Shape3.mirror = true;
      setRotation(Shape3, 0F, 0F, 0F);
      Shape4 = new ModelRenderer(this, 25, 65);
      Shape4.addBox(0F, 0F, 0F, 12, 12, 2);
      Shape4.setRotationPoint(-6F, -40F, -1F);
      Shape4.setTextureSize(128, 128);
      Shape4.mirror = true;
      setRotation(Shape4, 0F, 0F, 0F);
      Shape5 = new ModelRenderer(this, 25, 40);
      Shape5.addBox(0F, 0F, 0F, 2, 12, 12);
      Shape5.setRotationPoint(-1F, -40F, -6F);
      Shape5.setTextureSize(128, 128);
      Shape5.mirror = true;
      setRotation(Shape5, 0F, 0F, 0F);
      Shape6 = new ModelRenderer(this, 0, 80);
      Shape6.addBox(0F, 0F, 0F, 10, 15, 10);
      Shape6.setRotationPoint(-5F, 9F, -5F);
      Shape6.setTextureSize(128, 128);
      Shape6.mirror = true;
      setRotation(Shape6, 0F, 0F, 0F);
      Shape7 = new ModelRenderer(this, 0, 106);
      Shape7.addBox(0F, 0F, 0F, 8, 7, 8);
      Shape7.setRotationPoint(-4F, -36F, -4F);
      Shape7.setTextureSize(128, 128);
      Shape7.mirror = true;
      setRotation(Shape7, 0F, 0F, 0F);
      Shape8 = new ModelRenderer(this, 70, 0);
      Shape8.addBox(0F, 0F, 0F, 4, 4, 4);
      Shape8.setRotationPoint(-2F, -50F, -2F);
      Shape8.setTextureSize(128, 128);
      Shape8.mirror = true;
      setRotation(Shape8, 0F, 0F, 0F);
      Shape9 = new ModelRenderer(this, 50, 0);
      Shape9.addBox(0F, 0F, 0F, 1, 8, 4);
      Shape9.setRotationPoint(-2F, -56F, -2F);
      Shape9.setTextureSize(128, 128);
      Shape9.mirror = true;
      setRotation(Shape9, 0F, 0F, 0.1745329F);
      Shape10 = new ModelRenderer(this, 0, 22);
      Shape10.addBox(-2F, 0F, 0F, 2, 21, 2);
      Shape10.setRotationPoint(1F, -60F, -1F);
      Shape10.setTextureSize(128, 128);
      Shape10.mirror = true;
      setRotation(Shape10, 0F, 0F, 0F);
      Shape9 = new ModelRenderer(this, 50, 0);
      Shape9.addBox(0F, 0F, 0F, 1, 8, 4);
      Shape9.setRotationPoint(1F, -56F, -2F);
      Shape9.setTextureSize(128, 128);
      Shape9.mirror = true;
      setRotation(Shape9, 0F, 0F, -0.1745329F);
      Shape10 = new ModelRenderer(this, 0, 0);
      Shape10.addBox(-1F, 0F, 0F, 3, 1, 2);
      Shape10.setRotationPoint(0F, -51F, -1F);
      Shape10.setTextureSize(128, 128);
      Shape10.mirror = true;
      setRotation(Shape10, 0F, 0F, -0.5235988F);
      Shape11 = new ModelRenderer(this, 25, 0);
      Shape11.addBox(0F, 0F, 0F, 4, 8, 1);
      Shape11.setRotationPoint(-2F, -56F, 1F);
      Shape11.setTextureSize(128, 128);
      Shape11.mirror = true;
      setRotation(Shape11, 0.1745329F, 0F, 0F);
      Shape11 = new ModelRenderer(this, 25, 0);
      Shape11.addBox(0F, 0F, 0F, 4, 8, 1);
      Shape11.setRotationPoint(-2F, -56F, -2F);
      Shape11.setTextureSize(128, 128);
      Shape11.mirror = true;
      setRotation(Shape11, -0.1745329F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5);
    Shape1.render(f5);
    Shape2.render(f5);
    Shape3.render(f5);
    Shape4.render(f5);
    Shape5.render(f5);
    Shape6.render(f5);
    Shape7.render(f5);
    Shape8.render(f5);
    Shape9.render(f5);
    Shape10.render(f5);
    Shape9.render(f5);
    Shape10.render(f5);
    Shape11.render(f5);
    Shape11.render(f5);
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
