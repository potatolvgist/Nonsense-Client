package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

import java.util.List;

public class InventoryUtil implements MinecraftInstance {

    // Constants for slots in the inventory (https://wiki.vg/File:Inventory-slots.png)
    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int HOTBAR_BEGIN = 36;
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

    /**
     * Simulates a click in your inventory
     * @param slot Slot that was clicked
     * @param mouseButton which mouse button was used (0 -> left click, 1 -> right click)
     * @param shiftClick whether the simulated click should be a shift click
     */
    public static void click(int slot, int mouseButton, boolean shiftClick) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, mouseButton, shiftClick ? 1 : 0, mc.thePlayer);
    }

    /**
     * Drops an item in your inventory
     * @param slot Slot of the item to drop
     */
    public static void drop(int slot) {
        mc.playerController.windowClick(0, slot, 1, 4, mc.thePlayer);
    }

    /**
     * Moves an item directly to the hotbar (same as pressing a hotbar hotkey while in your inventory)
     * @param slot The inventory slot of the item to move
     * @param hotbarSlot The hotbar slot to move the item into (0-8)
     */
    public static void hotbarSwap(int slot, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarSlot, 2, mc.thePlayer);
    }

    public static int getAmountOfStacks(ItemTypeChecker check, boolean includeArmor) {
        int amount = 0;
        for (int i = includeArmor ? INCLUDE_ARMOR_BEGIN : EXCLUDE_ARMOR_BEGIN; i < END; i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null && check.check(stack)) {
                amount++;
            }
        }
        return amount;
    }

    public static SearchResult getBest(ItemTypeChecker check, ItemScoreCalculator score, boolean includeArmor) {
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

    public static void checkItem(ItemStack stack, ItemCheck check) {
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
            if (stackEffects == null) {
                return false;
            }
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

    public static int highestNonHotbarSlot(int currentSlot) {
        for (int slot = HOTBAR_BEGIN - 1; slot >= EXCLUDE_ARMOR_BEGIN; slot--) {
            if (getStackInSlot(slot) == null && slot > currentSlot) {
                return slot;
            }
        }

        return -1;
    }

    public static int stackAboveSwapSlot(int swapSlot, int rank, int currentSlot) {
        int slot = swapSlot - 9 * rank;
        ChatUtil.debug("SLOT: (%d, %d) -> %d", swapSlot, rank, slot);
        if (swapSlot != 0 && slot >= EXCLUDE_ARMOR_BEGIN && slot < END) {
            return slot;
        }

        return highestNonHotbarSlot(currentSlot);
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

    public interface ItemCheck {
        boolean check(Item item);
    }

}
