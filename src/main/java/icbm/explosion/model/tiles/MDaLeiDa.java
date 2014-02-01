package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MDaLeiDa extends ModelBase
{
    // fields
    ModelRenderer Main_Base;
    ModelRenderer Main_Base_Outer_Layer;
    ModelRenderer Pivot_Support_1;
    ModelRenderer Pivot_Support_2;
    ModelRenderer Pivot_Support_3;
    ModelRenderer Pivot_Point;
    ModelRenderer Dish_Support;
    ModelRenderer Focus_Support_1;
    ModelRenderer Focus_Support_2;
    ModelRenderer Focus_Point;
    ModelRenderer Main_Dish_Support;
    ModelRenderer Main_Dish_Piece;
    ModelRenderer Secondary_Dish_Piece_1;
    ModelRenderer Secondary_Dish_Piece_2;

    public MDaLeiDa()
    {
        textureWidth = 128;
        textureHeight = 128;

        Main_Base = new ModelRenderer(this, 0, 29);
        Main_Base.addBox(0F, 0F, 0F, 14, 10, 14);
        Main_Base.setRotationPoint(-7F, 14F, -7F);
        Main_Base.setTextureSize(128, 128);
        Main_Base.mirror = true;
        setRotation(Main_Base, 0F, 0F, 0F);
        Main_Base_Outer_Layer = new ModelRenderer(this, 0, 0);
        Main_Base_Outer_Layer.addBox(0F, 0F, 0F, 16, 4, 16);
        Main_Base_Outer_Layer.setRotationPoint(-8F, 20F, -8F);
        Main_Base_Outer_Layer.setTextureSize(128, 128);
        Main_Base_Outer_Layer.mirror = true;
        setRotation(Main_Base_Outer_Layer, 0F, 0F, 0F);
        Pivot_Support_1 = new ModelRenderer(this, 0, 63);
        Pivot_Support_1.addBox(0F, 0F, 0F, 12, 1, 12);
        Pivot_Support_1.setRotationPoint(-6F, 13F, -6F);
        Pivot_Support_1.setTextureSize(128, 128);
        Pivot_Support_1.mirror = true;
        setRotation(Pivot_Support_1, 0F, 0F, 0F);
        Pivot_Support_2 = new ModelRenderer(this, 87, 63);
        Pivot_Support_2.addBox(-2F, 0F, -1F, 10, 1, 10);
        Pivot_Support_2.setRotationPoint(-5F, 13F, 0F);
        Pivot_Support_2.setTextureSize(128, 128);
        Pivot_Support_2.mirror = true;
        setRotation(Pivot_Support_2, 0F, 0.7853982F, 0F);
        Pivot_Support_3 = new ModelRenderer(this, 0, 78);
        Pivot_Support_3.addBox(0F, 0F, 0F, 8, 2, 8);
        Pivot_Support_3.setRotationPoint(-4F, 11F, -4F);
        Pivot_Support_3.setTextureSize(128, 128);
        Pivot_Support_3.mirror = true;
        setRotation(Pivot_Support_3, 0F, 0F, 0F);
        Pivot_Point = new ModelRenderer(this, 0, 89);
        Pivot_Point.addBox(-2F, 0F, -2F, 4, 6, 4);
        Pivot_Point.setRotationPoint(0F, 5F, 0F);
        Pivot_Point.setTextureSize(128, 128);
        Pivot_Point.mirror = true;
        setRotation(Pivot_Point, 0F, 0F, 0F);
        Dish_Support = new ModelRenderer(this, 17, 89);
        Dish_Support.addBox(-1F, 0F, -4F, 2, 2, 8);
        Dish_Support.setRotationPoint(0F, 5F, 0F);
        Dish_Support.setTextureSize(128, 128);
        Dish_Support.mirror = true;
        setRotation(Dish_Support, 0F, 0F, 0F);
        Focus_Support_1 = new ModelRenderer(this, 0, 100);
        Focus_Support_1.addBox(-15F, 0F, 3F, 19, 2, 1);
        Focus_Support_1.setRotationPoint(0F, 5F, 0F);
        Focus_Support_1.setTextureSize(128, 128);
        Focus_Support_1.mirror = true;
        setRotation(Focus_Support_1, 0F, -0.2094395F, 0F);
        Focus_Support_2 = new ModelRenderer(this, 0, 100);
        Focus_Support_2.addBox(-15F, 0F, -4F, 19, 2, 1);
        Focus_Support_2.setRotationPoint(0F, 5F, 0F);
        Focus_Support_2.setTextureSize(128, 128);
        Focus_Support_2.mirror = true;
        setRotation(Focus_Support_2, 0F, 0.2094395F, 0F);
        Focus_Point = new ModelRenderer(this, 41, 100);
        Focus_Point.addBox(-16F, 0F, -1F, 1, 4, 2);
        Focus_Point.setRotationPoint(0F, 3F, 0F);
        Focus_Point.setTextureSize(128, 128);
        Focus_Point.mirror = true;
        setRotation(Focus_Point, 0F, 0F, 0F);
        Main_Dish_Support = new ModelRenderer(this, 0, 105);
        Main_Dish_Support.addBox(3F, 0F, -6F, 2, 2, 12);
        Main_Dish_Support.setRotationPoint(0F, 5F, 0F);
        Main_Dish_Support.setTextureSize(128, 128);
        Main_Dish_Support.mirror = true;
        setRotation(Main_Dish_Support, 0F, 0F, 0F);
        Main_Dish_Piece = new ModelRenderer(this, 51, 95);
        Main_Dish_Piece.addBox(4F, 0F, -9F, 1, 15, 18);
        Main_Dish_Piece.setRotationPoint(0F, -10F, 0F);
        Main_Dish_Piece.setTextureSize(128, 128);
        Main_Dish_Piece.mirror = true;
        setRotation(Main_Dish_Piece, 0F, 0F, 0F);
        Secondary_Dish_Piece_1 = new ModelRenderer(this, 51, 63);
        Secondary_Dish_Piece_1.addBox(8F, 0F, 4F, 1, 15, 16);
        Secondary_Dish_Piece_1.setRotationPoint(0F, -10F, 0F);
        Secondary_Dish_Piece_1.setTextureSize(128, 128);
        Secondary_Dish_Piece_1.mirror = true;
        setRotation(Secondary_Dish_Piece_1, 0F, -0.6108652F, 0F);
        Secondary_Dish_Piece_2 = new ModelRenderer(this, 51, 63);
        Secondary_Dish_Piece_2.addBox(8F, 0F, -20F, 1, 15, 16);
        Secondary_Dish_Piece_2.setRotationPoint(0F, -10F, 0F);
        Secondary_Dish_Piece_2.setTextureSize(128, 128);
        Secondary_Dish_Piece_2.mirror = true;
        setRotation(Secondary_Dish_Piece_2, 0F, 0.6108652F, 0F);
    }

    public void render(float rotation, float f5)
    {
        Main_Base.render(f5);
        Main_Base_Outer_Layer.render(f5);
        Pivot_Support_1.render(f5);
        Pivot_Support_2.render(f5);
        Pivot_Support_3.render(f5);

        // Rotatable Pivot Support
        Pivot_Point.rotateAngleY = rotation;
        Pivot_Point.render(f5);
        Dish_Support.rotateAngleY = rotation;
        Dish_Support.render(f5);

        // Rotatable Dish
        Focus_Support_1.rotateAngleY = rotation - 0.15F;
        Focus_Support_1.render(f5);
        Focus_Support_2.rotateAngleY = rotation + 0.15F;
        Focus_Support_2.render(f5);
        Focus_Point.rotateAngleY = rotation;
        Focus_Point.render(f5);
        Main_Dish_Support.rotateAngleY = rotation;
        Main_Dish_Support.render(f5);
        Main_Dish_Piece.rotateAngleY = rotation;
        Main_Dish_Piece.render(f5);
        Secondary_Dish_Piece_1.rotateAngleY = rotation - 0.6F;
        Secondary_Dish_Piece_1.render(f5);
        Secondary_Dish_Piece_2.rotateAngleY = rotation + 0.6F;
        Secondary_Dish_Piece_2.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
