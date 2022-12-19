package com.snoworange.mousse.module.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
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

public class Tooltip extends Module {

    private static final ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("textures/27slots.png");

    public static BooleanSetting shulker;
    public static BooleanSetting alwaysDura;
    public static BooleanSetting repaircost;

    public Tooltip() {
        super("Tooltip", "modifies tooltip", Category.MISC);
    }

    @Override
    public void init() {
        super.init();

        shulker = new BooleanSetting("Shulker Box", true);
        alwaysDura = new BooleanSetting("Always Durability", true);
        repaircost = new BooleanSetting("Render Repair Cost", false);

        addSetting(shulker, alwaysDura);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Main.sendMessage("[" + this.getName() + "] " + ChatFormatting.DARK_RED +  "WARN: the module is broken and woudn't work as intended!!");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    //Shulker
    public static Minecraft mc = Minecraft.getMinecraft();

    public static void renderShulker(final ItemStack itemStack, final int n, final int n2, final CallbackInfo callbackInfo) {
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
                Gui.drawModalRectWithCustomSizedTexture(n + 8, n2 - 97, 0, 0, 256, 256, 256, 256);

                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                RenderHelper.enableGUIStandardItemLighting();

                for (int i = 0; i < nonnulllist.size(); ++i) {

                    final int iX = n + 16 + i % 9 * 18;
                    final int iY = n2 - 98 + ((i / 9 + 1) * 18) + 1;

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