package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldItem implements LayerRenderer<EntityLivingBase>
{
    private final RendererLivingEntity<?> livingEntityRenderer;

    public LayerHeldItem(RendererLivingEntity<?> livingEntityRendererIn)
    {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        ItemStack itemstack = entitylivingbaseIn.getHeldItem();

        if (itemstack != null)
        {
            GlStateManager.pushMatrix();

            if (this.livingEntityRenderer.getMainModel().isChild)
            {
                float f = 0.5F;
                GlStateManager.translate(0.0F, 0.625F, 0.0F);
                GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.scale(f, f, f);
            }

            if (entitylivingbaseIn instanceof EntityPlayer) {
                if (((EntityPlayer)entitylivingbaseIn).isBlocking()) {
                    if (entitylivingbaseIn.isSneaking()) {
                        ((ModelBiped)livingEntityRenderer.getMainModel()).postRenderArm(0.0325F);
                        GlStateManager.scale(1.05F, 1.05F, 1.05F);
                        GlStateManager.translate(-0.58F, 0.32F, -0.07F);
                        GlStateManager.rotate(-24405.0F, 137290.0F, -2009900.0F, -2654900.0F);
                    } else {
                        ((ModelBiped)livingEntityRenderer.getMainModel()).postRenderArm(0.0325F);
                        GlStateManager.scale(1.05F, 1.05F, 1.05F);
                        GlStateManager.translate(-0.45F, 0.25F, -0.07F);
                        GlStateManager.rotate(-24405.0F, 137290.0F, -2009900.0F, -2654900.0F);
                    }
                } else {
                    ((ModelBiped)livingEntityRenderer.getMainModel()).postRenderArm(0.0625F);
                }

                if (!((EntityPlayer)entitylivingbaseIn).isBlocking() && !entitylivingbaseIn.isSneaking()) {
                    GlStateManager.translate(-0.0855F, 0.4775F, 0.1585F);
                    GlStateManager.rotate(-19.0F, 20.0F, 0.0F, -6.0F);
                } else if (entitylivingbaseIn.isSneaking() && !((EntityPlayer)entitylivingbaseIn).isBlocking()) {
                    GlStateManager.translate(-0.0525F, 0.5375F, 0.0725F);
                    GlStateManager.rotate(16.0F, -8.0F, 0.0F, 5.0F);
                    GlStateManager.translate(0.0F, -0.1F, 0.09523F);
                } else if (((EntityPlayer)entitylivingbaseIn).isBlocking()) {
                    GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);
                }
            } else {
                ((ModelBiped) this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F);
                GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);
            }

            if (entitylivingbaseIn instanceof EntityPlayer && ((EntityPlayer)entitylivingbaseIn).fishEntity != null)
            {
                itemstack = new ItemStack(Items.fishing_rod, 0);
            }

            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item).getRenderType() == 2)
            {
                GlStateManager.translate(0.0F, 0.1875F, -0.3125F);
                GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                float f1 = 0.375F;
                GlStateManager.scale(-f1, -f1, f1);
            }

            if (entitylivingbaseIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.203125F, 0.0F);
            }

            minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
