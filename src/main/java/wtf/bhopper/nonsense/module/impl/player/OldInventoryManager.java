package wtf.bhopper.nonsense.module.impl.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventPreUpdate;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.module.setting.impl.IntSetting;
import wtf.bhopper.nonsense.util.minecraft.inventory.ItemScores;
import wtf.bhopper.nonsense.util.minecraft.inventory.InventoryUtil;
import wtf.bhopper.nonsense.util.minecraft.inventory.ItemScoreCalculator;
import wtf.bhopper.nonsense.util.minecraft.inventory.ItemTypeChecker;
import wtf.bhopper.nonsense.util.misc.Clock;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal") // Annoying...
public class OldInventoryManager extends Module {

//    private static final List<String> BLACKLISTED_CONTAINERS = Arrays.asList("mode", "delivery", "menu", "selector", "game", "gui", "server", "inventory", "play", "teleporter", //
//            "shop", "melee", "armor", "block", "castle", "mini", "warp", "teleport", "user", "team", "tool", "sure", "trade", "cancel", "accept",  //
//            "soul", "book", "recipe", "profile", "tele", "port", "map", "kit", "select", "lobby", "vault", "lock", "anticheat", "travel", "settings", //
//            "user", "preference", "compass", "cake", "wars", "buy", "upgrade", "ranged", "potions", "utility", "collectibles");

    private final GroupSetting slotsGroup = new GroupSetting("Slots", "Slots", this);

    @SuppressWarnings("unchecked")
    private final EnumSetting<ItemSlot>[] slotSettings = (EnumSetting<ItemSlot>[]) Array.newInstance(EnumSetting.class, 9);

    private final GroupSetting autoArmorGroup = new GroupSetting("Auto Armor", "Auto armor", this);
    private final BooleanSetting enableAutoArmor = new BooleanSetting("Enable", "Enable auto armor", true);
    private final BooleanSetting armorHotbar = new BooleanSetting("Hotbar", "Put on armor in hotbar", true);
    private final BooleanSetting armorDrop = new BooleanSetting("Drop", "Drop armor", true);

    private final GroupSetting stealerGroup = new GroupSetting("Chest Stealer", "Check stealer", this);
    private final BooleanSetting stealerEnable = new BooleanSetting("Enable", "Enable chest stealer", true);
    private final BooleanSetting autoClose = new BooleanSetting("Auto Close", "Automatically closes the chest when done", true);
    private final BooleanSetting stealerWhitelist = new BooleanSetting("Whitelist GUI", "Don't take from certain GUI's", true);

    private final GroupSetting drops = new GroupSetting("Drop", "What items to drop", this);
    private final List<DropMasterSetting> dropMasterSettings;
    private final DropMasterSetting dropSwords = new DropMasterSetting("Swords", false);
    private final DropMasterSetting dropBows = new DropMasterSetting("Bows", false);
    private final DropMasterSetting dropTools = new DropMasterSetting("Tools", true);
    private final DropMasterSetting dropArmor = new DropMasterSetting("Armor", false);
    private final DropMasterSetting dropUtil = new DropMasterSetting("Util", false);
    private final DropMasterSetting dropFood = new DropMasterSetting("Food", false);
    private final DropMasterSetting dropBlocks = new DropMasterSetting("Blocks", true);
    private final DropMasterSetting dropPotions = new DropMasterSetting("Potions", false);
    private final BooleanSetting dropGarbage = new BooleanSetting("Garbage", "Drop Garbage", true);

    private final GroupSetting armorGroup = new GroupSetting("Armor", "Armor stacks", this);
    private final GroupSetting toolsGroup = new GroupSetting("Tools", "Tool stacks", this);
    private final GroupSetting utilsGroup = new GroupSetting("Utilities", "Utility stacks", this);
    private final GroupSetting potionsGroup = new GroupSetting("Potions", "Potion stacks", this);

    private final IntSetting delay = new IntSetting("Delay", "Delay", 0, 10000, 100, "%dms", null);
    private final BooleanSetting guiCheck = new BooleanSetting("Gui Check", "Check that that inventory is open", false);
    private final BooleanSetting actionCheck = new BooleanSetting("Action Check", "Check for conflictions with other modules", true);

    private final Clock timer = new Clock();

    public OldInventoryManager() {
        super("Inventory Manager", "Manage your inventory", Category.PLAYER);

        for (int i = 0; i < 9; i++) {
            String name = "Slot " + (i + 1);
            ItemSlot value = i == 0 ? ItemSlot.SWORD
                    : i == 1 ? ItemSlot.BLOCKS
                    : i == 8 ? ItemSlot.GOLDEN_APPLE
                    : ItemSlot.NONE;

            slotSettings[i] = new EnumSetting<>(name, name, value);
        }
        this.slotsGroup.add(this.slotSettings);

        this.autoArmorGroup.add(enableAutoArmor, armorHotbar, armorDrop);
        this.stealerGroup.add(this.stealerEnable, this.autoClose, this.stealerWhitelist);
        this.drops.add(dropSwords, dropBows, dropTools, dropArmor, dropUtil, dropFood, dropBlocks, dropPotions, dropGarbage);

        this.dropSwords.addSettings(toolsGroup, new DropSetting("Swords", stack -> stack.getItem() instanceof ItemSword, ItemScores::sword, 1));
        this.dropBows.addSettings(toolsGroup, new DropSetting("Bows", stack -> stack.getItem() instanceof ItemBow, ItemScores::bow, 1));
        this.dropTools.addSettings(toolsGroup,
                new DropSetting("Pickaxes", stack -> stack.getItem() instanceof ItemPickaxe, ItemScores::pickaxe, 1),
                new DropSetting("Axes", stack -> stack.getItem() instanceof ItemAxe, ItemScores::axe, 1),
                new DropSetting("Shovels", stack -> stack.getItem() instanceof ItemSpade, ItemScores::shovel, 1),
                new DropSetting("Fishing Rods", stack -> stack.getItem() == Items.fishing_rod, ItemScores::fishingRod, 1),
                new DropSetting("Shears", stack -> stack.getItem() == Items.shears, ItemScores.DURABILITY, 1),
                new DropSetting("Flint and Steel", stack -> stack.getItem() == Items.flint_and_steel, ItemScores.DURABILITY, 1)
        );
        this.dropBlocks.addSettings(utilsGroup, new DropSetting("Blocks", Scaffold::isValid, ItemScores.STACK_SIZE, 3));
        this.dropUtil.addSettings(utilsGroup,
                new DropSetting("Golden Apples", stack -> stack.getItem() == Items.golden_apple, ItemScores.STACK_SIZE, 3),
                new DropSetting("Ender Pearls", stack -> stack.getItem() == Items.ender_pearl, ItemScores.STACK_SIZE, 1),
                new DropSetting("Boats", stack -> stack.getItem() == Items.boat, ItemScores.STACK_SIZE, 0),
                new DropSetting("Arrows", stack -> stack.getItem() == Items.arrow, ItemScores.STACK_SIZE, 1),
                new DropSetting("Eggs and Snowballs", stack -> stack.getItem() == Items.egg || stack.getItem() == Items.snowball, ItemScores.STACK_SIZE, 1),
                new DropSetting("Spawn Eggs", stack -> stack.getItem() == Items.spawn_egg, ItemScores.STACK_SIZE, 0),
                new DropSetting("Food", stack -> stack.getItem() instanceof ItemFood && stack.getItem() != Items.golden_apple, ItemScores.STACK_SIZE, 1),
                new DropSetting("Water", stack -> stack.getItem() == Items.water_bucket, ItemScores.NONE, 1),
                new DropSetting("Lava", stack -> stack.getItem() == Items.lava_bucket, ItemScores.NONE, 1),
                new DropSetting("Milk", stack -> stack.getItem() == Items.milk_bucket, ItemScores.NONE, 1),
                new DropSetting("Sand and Gravel", stack -> InventoryUtil.isBlock(stack, Blocks.sand, Blocks.grass), ItemScores.STACK_SIZE, 1),
                new DropSetting("Cobwebs", stack -> InventoryUtil.isBlock(stack, Blocks.web), ItemScores.STACK_SIZE, 0)
        );
        this.dropArmor.addSettings(armorGroup,
                new DropSetting("Helmets", stack -> InventoryUtil.isArmor(stack, 0), ItemScores::helmet, 1),
                new DropSetting("Chestplates", stack -> InventoryUtil.isArmor(stack, 1), ItemScores::chestplate, 1),
                new DropSetting("Leggings", stack -> InventoryUtil.isArmor(stack, 2), ItemScores::leggings, 1),
                new DropSetting("Boots", stack -> InventoryUtil.isArmor(stack, 3), ItemScores::boots, 1)
        );
        this.dropPotions.addSettings(potionsGroup,
                // Speed needs to make sure it also doesn't have jump boost because of Frog Potions (even tho I'm not making Hypixel bypasses LOL)
                new DropSetting("Speed", stack -> InventoryUtil.isPotion(stack, Potion.moveSpeed) && !InventoryUtil.isPotion(stack, Potion.jump), stack -> ItemScores.potion(stack, Potion.moveSpeed), 3),
                new PotionDropSetting("Strength", Potion.damageBoost, 3),
                new PotionDropSetting("Regeneration", Potion.regeneration, 3),
                new PotionDropSetting("Healing", Potion.heal, 3),
                new PotionDropSetting("Jump Boost", Potion.jump, 0),
                new PotionDropSetting("Fire Resistance", Potion.fireResistance, 3),
                new PotionDropSetting("Resistance", Potion.resistance, 3),
                new PotionDropSetting("Invisibility", Potion.invisibility, 0),
                new PotionDropSetting("Absorption", Potion.absorption, 1),
                new PotionDropSetting("Weakness", Potion.weakness, 0),
                new PotionDropSetting("Slowness", Potion.moveSlowdown, 0),
                new PotionDropSetting("Poison", Potion.poison, 0),
                new PotionDropSetting("Harming", Potion.harm, 0)
        );

        // TODO: UHC items

        this.dropMasterSettings = Arrays.asList(dropSwords, dropBows, dropTools, dropArmor, dropUtil, dropFood, dropBlocks, dropPotions);

        this.addSettings(this.slotsGroup, this.autoArmorGroup, this.stealerGroup, this.drops, this.armorGroup, this.toolsGroup, this.utilsGroup, this.potionsGroup, this.delay, this.guiCheck, this.actionCheck);
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {

        if (mc.currentScreen instanceof GuiContainerCreative) {
            return;
        }

        if (this.actionCheck.get()) {
            if (Nonsense.module(Scaffold.class).isEnabled()) {
                return;
            }
        }

        if (!this.timer.hasReached(this.delay.get())) {
            return;
        }

        if (this.stealerEnable.get()) {
            if (mc.currentScreen instanceof GuiChest)
            {
                GuiChest chest = (GuiChest) mc.currentScreen;
                if (this.stealerWhitelist.get() && !chest.lowerChestInventory.getName().contains(I18n.format("container.chest"))) {
                    return;
                }

                if (this.steal(chest)) {
                    this.timer.reset();
                    return;
                } else if (this.autoClose.get()) {
                    mc.thePlayer.closeScreen();
                }

            }
        }

        boolean doNormal = true;
        boolean doArmor = true;

        if (this.guiCheck.get()) {
            if (!(mc.currentScreen instanceof GuiChest || mc.currentScreen instanceof GuiInventory)) {
                doNormal = false;
            }

            if (!(mc.currentScreen instanceof GuiInventory)) {
                doArmor = false;
            }
        }

        if (doNormal) {

            if (this.drop()) {
                this.timer.reset();
                return;
            }

            if (this.swap()) {
                this.timer.reset();
                return;
            }
        }

        if (this.enableAutoArmor.get() && doArmor) {
            if (this.autoArmor()) {
                this.timer.reset();
                return;
            }
        }


    }

    private boolean drop() {

        for (int i = InventoryUtil.INCLUDE_ARMOR_BEGIN; i < InventoryUtil.END; i++) {
            ItemStack stack = InventoryUtil.getStackInSlot(i);

            if (stack == null || stack.getItem() == null) {
                continue;
            }

            boolean checked = false;
            for (DropMasterSetting masterSetting : this.dropMasterSettings) {

                for (DropSetting dropSetting : masterSetting.dropSettings) {
                    if (dropSetting.check.check(stack)) {
                        checked = true;

                        if (!masterSetting.get())  {
                            continue;
                        }

                        InventoryUtil.SearchResult best = InventoryUtil.getBest(dropSetting.check, dropSetting.score, true);
                        if (dropSetting.get() == 0 || (best.slot != i && InventoryUtil.getAmountOfStacks(dropSetting.check, true) > dropSetting.get())) {
                            InventoryUtil.drop(i);
                            return true;
                        }

                    }
                }

            }

            if (!checked && this.dropGarbage.get()) {
                InventoryUtil.drop(i);
                return true;
            }

        }

        return false;
    }

    private boolean autoArmor() {

        if (this.autoArmor(0, ItemScores::helmet)) {
            return true;
        }

        if (this.autoArmor(1, ItemScores::chestplate)) {
            return true;
        }

        if (this.autoArmor(2, ItemScores::leggings)) {
            return true;
        }

        if (this.autoArmor(3, ItemScores::boots)) {
            return true;
        }

        return false;
    }

    private boolean swap() {

        List<Integer> blocked = new ArrayList<>();

        for (int i = 0; i < 9; i++) {

            InventoryUtil.SearchResult best = this.slotSettings[i].get().findBest();
            if (best.slot == -1) {
                // Best slot not found
                continue;
            }

            float scoreInPlace = 0.0F;
            try {
                ItemStack stack = InventoryUtil.getStackInSlot(i + InventoryUtil.HOTBAR_BEGIN);
                if (stack != null) {
                    scoreInPlace = this.slotSettings[i].get().scoreCheck.getScore(stack);
                }
            } catch (IllegalArgumentException ignored) {}

            if (scoreInPlace == best.score || best.slot == InventoryUtil.HOTBAR_BEGIN + i) {
                // Item is already in the best slot
                blocked.add(best.slot);
                continue;
            }

            if (blocked.contains(best.slot)) {
                // Previous slot contains that item
                continue;
            }

            InventoryUtil.hotbarSwap(best.slot, i);
            return true;

        }

        return false;

    }

    private boolean autoArmor(int type, ItemScoreCalculator scoreCheck) {
        InventoryUtil.SearchResult bestArmor = InventoryUtil.getBest(stack -> stack.getItem() instanceof ItemArmor && ((ItemArmor) stack.getItem()).armorType == type, scoreCheck, true);
        if (!bestArmor.valid()) {
            return false;
        }

        if (bestArmor.slot < InventoryUtil.EXCLUDE_ARMOR_BEGIN) {
            return false;
        }

        if (InventoryUtil.getStackInSlot(InventoryUtil.INCLUDE_ARMOR_BEGIN + type) != null) {
            InventoryUtil.click(InventoryUtil.INCLUDE_ARMOR_BEGIN + type, 0, true);
        } else {
            InventoryUtil.click(bestArmor.slot, 0, true);
        }

        return true;
    }

    private boolean steal(GuiChest chest) {

        if (this.inventoryFull()) {
            return false;
        }

        for (int i = 0; i < chest.lowerChestInventory.getSizeInventory(); i++) {
            ItemStack stack = chest.lowerChestInventory.getStackInSlot(i);
            if (stack != null) {

                // TODO: further item checking to speed up the amount of time it takes to loot a check

                mc.playerController.windowClick(chest.inventorySlots.windowId, i, 0, 1, mc.thePlayer);
                return true;
            }
        }

        return false;
    }

    private boolean needItem(ItemStack stack) {
        return false;
    }

    private boolean inventoryFull() {
        for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
            if (stack == null) {
                return false;
            }
        }

        return true;
    }

    public enum ActionMode {
        ALWAYS,
        INVENTORY,
        NOT_MOVING,
        NEVER
    }

    private enum ItemSlot {
        SWORD(stack -> stack.getItem() instanceof ItemSword, ItemScores::sword),
        BOW(stack -> stack.getItem() == Items.bow, ItemScores::bow),
        ROD(stack -> stack.getItem() == Items.fishing_rod, ItemScores::fishingRod),
        ENDER_PEARL(stack -> stack.getItem() == Items.ender_pearl, ItemScores.STACK_SIZE),
        GOLDEN_APPLE(stack -> stack.getItem() == Items.golden_apple, ItemScores.STACK_SIZE),
        GOLDEN_HEAD(stack -> stack.getItem() == Items.skull && stack.hasDisplayName() && stack.getDisplayName().toLowerCase().contains("golden head"), ItemScores.STACK_SIZE),
        BLOCKS(Scaffold::isValid, ItemScores.STACK_SIZE),
        PICKAXE(stack -> stack.getItem() instanceof ItemPickaxe, ItemScores::pickaxe),
        AXE(stack -> stack.getItem() instanceof ItemAxe, ItemScores::axe),
        SHOVEL(stack -> stack.getItem() instanceof ItemSpade, ItemScores::shovel),
        NONE(stack -> false, ItemScores.STACK_SIZE);

        private final ItemTypeChecker stackCheck;
        private final ItemScoreCalculator scoreCheck;

        ItemSlot(ItemTypeChecker stackCheck, ItemScoreCalculator comparator) {
            this.stackCheck = stackCheck;
            this.scoreCheck = comparator;
        }

        public boolean check(ItemStack stack) {
            if (stack == null) {
                return false;
            }
            return this.stackCheck.check(stack);
        }

        public InventoryUtil.SearchResult findBest() {
            int bestIndex = -1;
            float bestScore = 0.0F;
            ItemStack bestStack = null;
            for (int i = InventoryUtil.EXCLUDE_ARMOR_BEGIN; i < InventoryUtil.END; i++) {
                ItemStack stack = InventoryUtil.getStackInSlot(i);
                if (!this.check(stack)) {
                    continue;
                }

                float score = this.scoreCheck.getScore(stack);

                if (bestIndex == -1) {
                    bestIndex = i;
                    bestScore = score;
                    bestStack = stack;
                    continue;
                }

                if (score > bestScore) {
                    bestIndex = i;
                    bestScore = score;
                    bestStack = stack;
                }

            }

            return new InventoryUtil.SearchResult(bestStack, bestIndex, bestScore);
        }

    }

    public static class DropMasterSetting extends BooleanSetting {

        public final List<DropSetting> dropSettings = new ArrayList<>();

        public DropMasterSetting(String name, boolean defaultValue) {
            super(name, "Drop " + name, defaultValue);
        }

        public void addSettings(GroupSetting group, DropSetting... settings) {
            this.dropSettings.addAll(Arrays.asList(settings));
            group.add(settings);
        }
    }

    public static class DropSetting extends IntSetting {

        public final ItemTypeChecker check;
        public final ItemScoreCalculator score;

        public DropSetting(String name, ItemTypeChecker check, ItemScoreCalculator score, int defaultValue) {
            super(name, name, 0, 5, defaultValue, "%d stacks", null);
            this.check = check;
            this.score = score;
        }
    }

    public static class PotionDropSetting extends DropSetting {
        public PotionDropSetting(String name, Potion potion, int defaultValue) {
            // TODO: add an option to sort potions by amplifier or duration (right now it's being sorted by ampplifier)
            super(name, stack -> InventoryUtil.isPotion(stack, potion), stack -> ItemScores.potion(stack, potion), defaultValue);
        }
    }

}
