package com.snoworange.mousse.module.modules.render;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ShulkerPreview extends Module {

    private static final ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("textures/27slots.png");;

    public ShulkerPreview() {
        super("ShulkerPreview", "ur dad", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    public static int itemX = 16;
    public static int itemY = -98;
    public static int itemTextureX = 8;
    public static int itemTextureY = -97;

    public static Minecraft mc = Minecraft.getMinecraft();

    public static void renderToolTip(final ItemStack itemStack, final int n, final int n2, final CallbackInfo callbackInfo) {
        final NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10)) {
            final NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
            if (blockEntityTag.hasKey("Items", 9)) {

                NonNullList nonnulllist = NonNullList.withSize(27, (Object) ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist);

                GlStateManager.enableBlend();
                GlStateManager.disableRescaleNormal();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();

                mc.getRenderItem().zLevel = 300;

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(SHULKER_GUI_TEXTURE);
                Gui.drawModalRectWithCustomSizedTexture(n + itemTextureX, n2 + itemTextureY, 0, 0, 256, 256, 256, 256);

                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                RenderHelper.enableGUIStandardItemLighting();

                for (int i = 0; i < nonnulllist.size(); ++i) {

                    final int iX = n + itemX + i % 9 * 18;
                    final int iY = n2 + itemY + ((i / 9 + 1) * 18) + 1;

                    final ItemStack stack = (ItemStack) nonnulllist.get(i);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(stack, iX, iY);
                    mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, iX, iY, (String) null);
                }

                RenderHelper.disableStandardItemLighting();
                mc.getRenderItem().zLevel = 0.0f;
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                RenderHelper.enableStandardItemLighting();
                GlStateManager.enableRescaleNormal();
            }
        }
    }
}
