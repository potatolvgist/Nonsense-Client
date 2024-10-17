package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

public class InventoryUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int ONLY_HOT_BAR_BEGIN = 36;
    public static final int END = 45;

    public static int serverItem = 0;

    public static int currentItem() {
        return serverItem;
    }

    public static boolean placeStackInHotbar(ItemStack stack) {
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) == null) {
                mc.thePlayer.inventory.mainInventory[i] = stack.copy();
                PacketUtil.send(new C10PacketCreativeInventoryAction(i + 36, stack));
                return true;
            }
        }

        return false;
    }

    public static void click(int slot, int mouseButton, boolean shiftClick) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, mouseButton, shiftClick ? 1 : 0, mc.thePlayer);
    }

    public static void drop(int slot) {
        mc.playerController.windowClick(0, slot, 1, 4, mc.thePlayer);
    }

    public static void swap(int slot, int hSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hSlot, 2, mc.thePlayer);
    }

    public static float getSwordScore(ItemStack stack) {
        checkItem(stack, item -> item instanceof ItemSword);

        ItemSword item = (ItemSword)stack.getItem();

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 50) {
                return 0.0F;
            }
        }

        float sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
        float fire = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.1F;

        return item.getDamageVsEntity() + sharpness + fire;
    }

    public static float getPickaxeScore(ItemStack stack) {
        checkItem(stack, item -> item instanceof ItemPickaxe);

        ItemPickaxe item = (ItemPickaxe)stack.getItem();

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 50) {
                return 0.0F;
            }
        }

        float efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 1.25F;

        return item.getToolMaterial().getEfficiencyOnProperMaterial() + efficiency;
    }

    public static float getAxeScore(ItemStack stack) {
        checkItem(stack, item -> item instanceof ItemAxe);

        ItemAxe item = (ItemAxe)stack.getItem();

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 50) {
                return 0.0F;
            }
        }

        float efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 1.25F;

        return item.getToolMaterial().getEfficiencyOnProperMaterial() + efficiency;
    }

    public static float getShovelScore(ItemStack stack) {
        checkItem(stack, item -> item instanceof ItemSpade);

        ItemSpade item = (ItemSpade)stack.getItem();

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 50) {
                return 0.0F;
            }
        }

        float efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 1.25F;

        return item.getToolMaterial().getEfficiencyOnProperMaterial() + efficiency;
    }

    public static float getHelmetScore(ItemStack stack) {
        checkItem(stack, item -> item instanceof ItemArmor && ((ItemArmor) item).armorType == 0);

        ItemArmor item = (ItemArmor) stack.getItem();

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 50) {
                return 0.0F;
            }
        }

        float protection = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 2.5F;
        float thorns = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) * 0.1F;
        float respiration = EnchantmentHelper.getEnchantmentLevel(Enchantment.respiration.effectId, stack) * 0.1F;
        float aqua = EnchantmentHelper.getEnchantmentLevel(Enchantment.aquaAffinity.effectId, stack) * 0.1F;

        return item.damageReduceAmount + protection + thorns + respiration + aqua;
    }

    public static float getChestplateScore(ItemStack stack) {
        checkItem(stack, item -> item instanceof ItemArmor && ((ItemArmor) item).armorType == 1);

        ItemArmor item = (ItemArmor) stack.getItem();

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 50) {
                return 0.0F;
            }
        }

        float protection = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 2.5F;
        float thorns = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) * 0.1F;

        return item.damageReduceAmount + protection + thorns;
    }

    public static float getLeggingsScore(ItemStack stack) {
        checkItem(stack, item -> item instanceof ItemArmor && ((ItemArmor) item).armorType == 2);

        ItemArmor item = (ItemArmor) stack.getItem();

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 50) {
                return 0.0F;
            }
        }

        float protection = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 2.5F;
        float thorns = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) * 0.1F;

        return item.damageReduceAmount + protection + thorns;
    }

    public static float getBootsScore(ItemStack stack) {
        checkItem(stack, item -> item instanceof ItemArmor && ((ItemArmor) item).armorType == 4);

        ItemArmor item = (ItemArmor) stack.getItem();

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 50) {
                return 0.0F;
            }
        }

        float protection = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 2.5F;
        float thorns = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) * 0.1F;
        float depthStrider = EnchantmentHelper.getEnchantmentLevel(Enchantment.depthStrider.effectId, stack) * 0.1F;
        float featherFalling = EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) * 0.01F;

        return item.damageReduceAmount + protection + thorns + depthStrider + featherFalling;
    }

    private static void checkItem(ItemStack stack, ItemCheck check) {
        if (stack == null) {
            throw new IllegalArgumentException("stack cannot be null");
        }

        check.check(stack.getItem());
    }

    private interface ItemCheck {
        boolean check(Item item);
    }

}
