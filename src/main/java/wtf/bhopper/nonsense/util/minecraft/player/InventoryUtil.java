package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

import java.util.List;

public class InventoryUtil implements MinecraftInstance {

    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int ONLY_HOT_BAR_BEGIN = 36;
    public static final int END = 45;

    public static final ScoreCheck NO_SCORE = stack -> 0.0F;
    public static final ScoreCheck STACK_SIZE_SCORE = stack -> stack.stackSize;
    public static final ScoreCheck DURABILITY_SCORE = stack -> {
        if (!stack.getItem().isDamageable()) {
            return Float.MAX_VALUE;
        }
        return stack.getMaxDamage() - stack.getItemDamage();
    };

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

    public static ItemStack getStackInSlot(int slot) {
        return mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
    }

    public static ItemStack[] getStacks() {
        ItemStack[] stacks = new ItemStack[END - EXCLUDE_ARMOR_BEGIN];
        for (int i = EXCLUDE_ARMOR_BEGIN; i < END; i++) {
            stacks[i - EXCLUDE_ARMOR_BEGIN] = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        }
        return stacks;
    }

    public static void click(int slot, int mouseButton, boolean shiftClick) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, mouseButton, shiftClick ? 1 : 0, mc.thePlayer);
    }

    public static void drop(int slot) {
        mc.playerController.windowClick(0, slot, 1, 4, mc.thePlayer);
    }

    public static void swap(int srcSlot, int dstSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, srcSlot, dstSlot, 2, mc.thePlayer);
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

    public static float getBowScore(ItemStack stack) {
        checkItem(stack, item -> item instanceof ItemBow);

        ItemBow item = (ItemBow)stack.getItem();

//        if (stack.isItemStackDamageable()) {
//            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
//            if (damageLeft < 50) {
//                return 0.0F;
//            }
//        }

        float power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack) * 1.25F;
        float punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack) * 0.5F;

        return power + punch;
    }

    public static float getRodScore(ItemStack stack) {
        checkItem(stack, item -> item instanceof ItemFishingRod);

        ItemFishingRod item = (ItemFishingRod)stack.getItem();

//        if (stack.isItemStackDamageable()) {
//            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
//            if (damageLeft < 50) {
//                return 0.0F;
//            }
//        }

        float kb = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack) * 1.25F;

        return kb;
    }

    public static float getPotionScore(ItemStack stack, Potion potion) {
        checkItem(stack, item -> item instanceof ItemPotion);

        ItemPotion item = (ItemPotion)stack.getItem();
        List<PotionEffect> effects = item.getEffects(stack);
        for (PotionEffect effect : effects) {
            if (effect.getPotionID() == potion.id) {
                return effect.getAmplifier() + 1.0F;
            }
        }

        return 0.0F;
    }

    public static int getAmountOfStacks(StackCheck check, boolean includeArmor) {
        int amount = 0;
        for (int i = includeArmor ? INCLUDE_ARMOR_BEGIN : EXCLUDE_ARMOR_BEGIN; i < END; i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null && check.check(stack)) {
                amount++;
            }
        }
        return amount;
    }

    public static SearchResult getBest(StackCheck check, ScoreCheck score, boolean includeArmor) {
        int bestSlot = -1;
        float bestScore = 0.0F;
        ItemStack bestStack = null;
        for (int i = includeArmor ? INCLUDE_ARMOR_BEGIN : EXCLUDE_ARMOR_BEGIN; i < END; i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null && check.check(stack)) {
                float stackScore = score.getScore(stack);
                if (stackScore > bestScore || bestSlot == -1) {
                    bestScore = stackScore;
                    bestSlot = i;
                    bestStack = stack;
                }
            }
        }
        return new SearchResult(bestStack, bestSlot, bestScore);
    }

    private static void checkItem(ItemStack stack, ItemCheck check) {
        if (stack == null) {
            throw new IllegalArgumentException("stack cannot be null");
        }

        check.check(stack.getItem());
    }

    public static boolean isArmor(ItemStack stack, int type) {
        if (stack.getItem() instanceof ItemArmor) {
            return ((ItemArmor) stack.getItem()).armorType == type;
        }
        return false;
    }

    public static boolean isBlock(ItemStack stack, Block... blocks) {
        if (stack.getItem() instanceof ItemBlock) {
            for (Block block : blocks) {
                if (((ItemBlock) stack.getItem()).getBlock() == block) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isPotion(ItemStack stack, Potion... effects) {
        if (stack.getItem() instanceof ItemPotion) {
            List<PotionEffect> stackEffects = ((ItemPotion) stack.getItem()).getEffects(stack);
            for (Potion effect : effects) {
                for (PotionEffect stackEffect : stackEffects) {
                    if (effect.id == stackEffect.getPotionID()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static class SearchResult {
        public ItemStack stack;
        public int slot;
        public float score;

        public SearchResult(ItemStack stack, int slot, float score) {
            this.stack = stack;
            this.slot = slot;
            this.score = score;
        }

        public boolean valid() {
            return this.slot != -1;
        }
    }

    public interface StackCheck {
        boolean check(ItemStack stack);
    }

    public interface ScoreCheck {
        float getScore(ItemStack stack);
    }

    private interface ItemCheck {
        boolean check(Item item);
    }

}
