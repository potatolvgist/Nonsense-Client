package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;

public class ItemScores {

    public static final ItemScoreCalculator NONE = stack -> 0.0F;
    public static final ItemScoreCalculator STACK_SIZE = stack -> stack.stackSize;
    public static final ItemScoreCalculator DURABILITY = stack -> {
        if (!stack.getItem().isDamageable()) {
            return Float.MAX_VALUE;
        }
        return stack.getMaxDamage() - stack.getItemDamage();
    };

    public static float sword(ItemStack stack) {
        InventoryUtil.checkItem(stack, item -> item instanceof ItemSword);

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

    public static float pickaxe(ItemStack stack) {
        InventoryUtil.checkItem(stack, item -> item instanceof ItemPickaxe);

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

    public static float axe(ItemStack stack) {
        InventoryUtil.checkItem(stack, item -> item instanceof ItemAxe);

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

    public static float shovel(ItemStack stack) {
        InventoryUtil.checkItem(stack, item -> item instanceof ItemSpade);

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

    public static float helmet(ItemStack stack) {
        InventoryUtil.checkItem(stack, item -> item instanceof ItemArmor && ((ItemArmor) item).armorType == 0);

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

    public static float chestplate(ItemStack stack) {
        InventoryUtil.checkItem(stack, item -> item instanceof ItemArmor && ((ItemArmor) item).armorType == 1);

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

    public static float leggings(ItemStack stack) {
        InventoryUtil.checkItem(stack, item -> item instanceof ItemArmor && ((ItemArmor) item).armorType == 2);

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

    public static float boots(ItemStack stack) {
        InventoryUtil.checkItem(stack, item -> item instanceof ItemArmor && ((ItemArmor) item).armorType == 4);

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

    public static float bow(ItemStack stack) {
        InventoryUtil.checkItem(stack, item -> item instanceof ItemBow);

        float power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack) * 1.25F;
        float punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack) * 0.5F;

        return power + punch;
    }

    public static float fishingRod(ItemStack stack) {
        InventoryUtil.checkItem(stack, item -> item instanceof ItemFishingRod);

        float kb = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack) * 1.25F;

        return kb;
    }

    public static float potion(ItemStack stack, Potion potion) {
        InventoryUtil.checkItem(stack, item -> item instanceof ItemPotion);

        ItemPotion item = (ItemPotion)stack.getItem();
        List<PotionEffect> effects = item.getEffects(stack);
        for (PotionEffect effect : effects) {
            if (effect.getPotionID() == potion.id) {
                return effect.getAmplifier() + 1.0F;
            }
        }

        return 0.0F;
    }

}
