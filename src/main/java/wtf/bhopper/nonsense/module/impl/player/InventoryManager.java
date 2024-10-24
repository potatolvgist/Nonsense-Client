package wtf.bhopper.nonsense.module.impl.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventPreUpdate;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.impl.combat.KillAura;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.module.setting.impl.IntSetting;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.inventory.*;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class InventoryManager extends Module {

    private final GroupSetting slotsGroup = new GroupSetting("Slots", "Item slots", this);
    private final IntSetting swordSlot = newSlotSetting("Sword", 1);
    private final IntSetting bowSlot = newSlotSetting("Bow");
    private final IntSetting rodSlot = newSlotSetting("Fishing Rod");
    private final IntSetting pearlSlot = newSlotSetting("Ender Pearl");
    private final IntSetting gappleSlot = newSlotSetting("Golden Apple", 9);
    private final IntSetting gheadSlot = newSlotSetting("Golden Head");
    private final IntSetting blocksSlot = newSlotSetting("Blocks", 2);
    private final IntSetting pickaxeSlot = newSlotSetting("Pickaxe");
    private final IntSetting axeSlot = newSlotSetting("Axe");
    private final IntSetting shovelSlot = newSlotSetting("Shovel");

    private final GroupSetting armorGroup = new GroupSetting("Auto Armor", "Automatically puts on armor", this);
    private final BooleanSetting autoArmorEnable = new BooleanSetting("Enable", "Enables auto armor", true);

    private final GroupSetting dropsGroup = new GroupSetting("Drop", "Item drops", this);
    private final BooleanSetting dropGarbage = new BooleanSetting("Garbage", "Drop items that are not considered useful", true);
    private final BooleanSetting hotbarDrop = new BooleanSetting("Hotbar", "Drop items out of your hotbar", true);

    private final GroupSetting weaponsGroup = new GroupSetting("Weapons", "Weapons", this);
    private final GroupSetting toolsGroup = new GroupSetting("Tools", "Tools", this);
    private final GroupSetting utilGroup = new GroupSetting("Utilities", "Utilities", this);
    private final GroupSetting potionGroup = new GroupSetting("Potions", "Potions", this);

    private final BooleanSetting openGui = new BooleanSetting("Open Inventory", "Requires your Inventory to be open", false);
    private final EnumSetting<ActionMode> swapMode = new EnumSetting<>("Swap Mode", "Item swapping mode", ActionMode.PRIORITY);
    private final EnumSetting<ActionMode> dropMode = new EnumSetting<>("Drop Mode", "Item dropping mode", ActionMode.PRIORITY);
    private final EnumSetting<ActionMode> sortMode = new EnumSetting<>("Sort Mode", "Item sorting mode", ActionMode.PRIORITY);
    private final EnumSetting<SortMethod> sortMethod = new EnumSetting<>("Sort Method", "Sorting method", SortMethod.CLICK);
    private final DelaySetting swapDelay = new DelaySetting("Swap Delay", 0, 1000, 150, 250);
    private final DelaySetting dropDelay = new DelaySetting("Drop Delay", 0, 1000, 10, 150);
    private final DelaySetting sortDelay = new DelaySetting("Sort Delay", 0, 1000, 100, 200);
    private final IntSetting minHoldTime = new IntSetting("Min Hold Time", "Time an item must be in your inventory before doing actions on it", 0, 1000, 300, "%dms", null);

    private final Map<Integer, ItemSlot> itemSlots = new HashMap<>();

    private final List<ItemTracker> itemTrackers = new ArrayList<>();
    private final List<ItemSwapper> itemSwappers = new ArrayList<>();
    private final List<ItemSorter> itemSorters = new ArrayList<>();

    private final List<Integer> garbageSlots = new ArrayList<>();

    private final Map<Integer, ItemStack> currentTickItems = new HashMap<>();
    private final Map<Integer, ItemStack> prevTickItems = new HashMap<>();
    private final Map<Integer, Integer> itemTimeInSlot = new HashMap<>(); // 5kr411 used ticks to calculate slot times, I'm using milliseconds for more accuracy

    private final List<Integer> dropsToPerform = new ArrayList<>();
    private final List<SwapAction> swapsToPerform = new ArrayList<>();
    private final List<SwapAction> sortsToPerform = new ArrayList<>();

    private final List<InventoryAction> inventoryActions = new ArrayList<>();
    private final List<InventoryAction> hotbarActions = new ArrayList<>();

    private int delay = 0;

    public InventoryManager() {
        super("Inventory Manager", "Manages your inventory, (made with help from 5kr411)", Category.PLAYER);
        this.slotsGroup.add(this.swordSlot, this.bowSlot, this.rodSlot, this.pearlSlot, this.gappleSlot, this.blocksSlot, this.pickaxeSlot, this.axeSlot, this.shovelSlot);
        this.armorGroup.add(this.autoArmorEnable);
        this.dropsGroup.add(this.dropGarbage, this.hotbarDrop);

        this.weaponsGroup.add(
                new DropSetting("Swords", 1, stack -> stack.getItem() instanceof ItemSword, ItemScores::sword, swordSlot::get, (rank, currentSlot) -> InventoryUtil.stackAboveSwapSlot(this.swordSlot.get() + 35, rank, currentSlot)),
                new DropSetting("Bows", 1, stack -> stack.getItem() == Items.bow, ItemScores::bow, bowSlot::get, (rank, currentSlot) -> InventoryUtil.stackAboveSwapSlot(this.bowSlot.get() + 35, rank, currentSlot)),
                new DropSetting("Helmets", 1, stack -> InventoryUtil.isArmor(stack, 0), ItemScores::helmet, null, (rank, currentSlot) -> autoArmorEnable.get() ? InventoryUtil.armorOrOther(5, rank, currentSlot) : -1),
                new DropSetting("Chestplates", 1, stack -> InventoryUtil.isArmor(stack, 1), ItemScores::chestplate, null, (rank, currentSlot) -> autoArmorEnable.get() ? InventoryUtil.armorOrOther(6, rank, currentSlot) : -1),
                new DropSetting("Leggings", 1, stack -> InventoryUtil.isArmor(stack, 2), ItemScores::leggings, null, (rank, currentSlot) -> autoArmorEnable.get() ? InventoryUtil.armorOrOther(7, rank, currentSlot) : -1),
                new DropSetting("Boots", 1, stack -> InventoryUtil.isArmor(stack, 3), ItemScores::boots, null, (rank, currentSlot) -> autoArmorEnable.get() ? InventoryUtil.armorOrOther(8, rank, currentSlot) : -1)
        );

        this.toolsGroup.add(
                new DropSetting("Pickaxes", 1, stack -> stack.getItem() instanceof ItemPickaxe, ItemScores::pickaxe, pickaxeSlot::get, (rank, currentSlot) -> InventoryUtil.stackAboveSwapSlot(this.pickaxeSlot.get() + 35, rank, currentSlot)),
                new DropSetting("Axes", 1, stack -> stack.getItem() instanceof ItemAxe, ItemScores::axe, axeSlot::get, (rank, currentSlot) -> InventoryUtil.stackAboveSwapSlot(this.axeSlot.get() + 35, rank, currentSlot)),
                new DropSetting("Shovels", 1, stack -> stack.getItem() instanceof ItemSpade, ItemScores::shovel,shovelSlot::get, (rank, currentSlot) -> InventoryUtil.stackAboveSwapSlot(this.shovelSlot.get() + 35, rank, currentSlot)),
                new DropSetting("Fishing Rods", 1, stack -> stack.isOfItem(Items.fishing_rod), ItemScores::fishingRod, rodSlot::get, (rank, currentSlot) -> InventoryUtil.stackAboveSwapSlot(this.rodSlot.get() + 35, rank, currentSlot)),
                new DropSetting("Shears", 1, stack -> stack.isOfItem(Items.shears), ItemScores.DURABILITY),
                new DropSetting("Flint and Steel", 1, stack -> stack.isOfItem(Items.flint_and_steel), ItemScores.DURABILITY)
        );

        this.utilGroup.add(
                new DropSetting("Blocks", 3, stack -> stack.getItem() instanceof ItemBlock, ItemScores.STACK_SIZE, blocksSlot::get, (rank, currentSlot) -> InventoryUtil.stackAboveSwapSlot(this.blocksSlot.get() + 35, rank, currentSlot)),
                new DropSetting("Golden Apples", 1, stack -> stack.isOfItem(Items.golden_apple), ItemScores.STACK_SIZE, gappleSlot::get, (rank, currentSlot) -> InventoryUtil.stackAboveSwapSlot(this.gappleSlot.get() + 35, rank, currentSlot)),
                new DropSetting("Golden Head", 1, stack -> stack.isOfItem(Items.skull) && stack.hasDisplayName() && stack.getDisplayName().toLowerCase().contains("golden head"), ItemScores.STACK_SIZE, gheadSlot::get, (rank, currentSlot) -> InventoryUtil.stackAboveSwapSlot(this.gheadSlot.get() + 35, rank, currentSlot)),
                new DropSetting("Ender Pearls", 1, stack -> stack.isOfItem(Items.ender_pearl), ItemScores.STACK_SIZE, pearlSlot::get, (rank, currentSlot) -> InventoryUtil.stackAboveSwapSlot(this.pearlSlot.get() + 35, rank, currentSlot)),
                new DropSetting("Boats", 1, stack -> stack.isOfItem(Items.boat), ItemScores.STACK_SIZE),
                new DropSetting("TNT", 1, stack -> stack.isOfItem(Blocks.tnt), ItemScores.STACK_SIZE),
                new DropSetting("Arrows", 1, stack -> stack.isOfItem(Items.arrow), ItemScores.STACK_SIZE),
                new DropSetting("Eggs Snowballs", 1, stack -> stack.isOfItem(Items.egg) || stack.isOfItem(Items.snowball), ItemScores.STACK_SIZE),
                new DropSetting("Food", 1, stack -> stack.getItem() instanceof ItemFood, ItemScores.STACK_SIZE),
                new DropSetting("Water Buckets", 1, stack -> stack.isOfItem(Items.water_bucket), ItemScores.STACK_SIZE),
                new DropSetting("Lava Buckets", 1, stack -> stack.isOfItem(Items.lava_bucket), ItemScores.STACK_SIZE),
                new DropSetting("Milk Buckets", 1, stack -> stack.isOfItem(Items.milk_bucket), ItemScores.STACK_SIZE),
                new DropSetting("Sand Gravel", 1, stack -> stack.isOfItem(Blocks.sand) || stack.isOfItem(Blocks.gravel), ItemScores.STACK_SIZE),
                new DropSetting("Cobwebs", 1, stack -> stack.isOfItem(Blocks.web), ItemScores.STACK_SIZE)
        );

        this.potionGroup.add(
                new PotionDropSetting("Speed", 3, Potion.moveSpeed, Potion.jump),
                new PotionDropSetting("Strength", 3, Potion.damageBoost),
                new PotionDropSetting("Regeneration", 3, Potion.regeneration),
                new PotionDropSetting("Healing", 3, Potion.heal),
                new PotionDropSetting("Jump Boost", 0, Potion.jump),
                new PotionDropSetting("Fire Resistance", 3, Potion.fireResistance),
                new PotionDropSetting("Resistance", 3, Potion.resistance),
                new PotionDropSetting("Invisibility", 0, Potion.invisibility),
                new PotionDropSetting("Absorption", 1, Potion.absorption),
                new PotionDropSetting("Weakness", 0, Potion.weakness),
                new PotionDropSetting("Slowness", 0, Potion.moveSlowdown),
                new PotionDropSetting("Poison", 0, Potion.poison),
                new PotionDropSetting("Harming", 0, Potion.harm)
        );

        this.addSettings(this.slotsGroup, this.armorGroup, this.dropsGroup, this.weaponsGroup, this.toolsGroup, this.utilGroup, this.potionGroup,
                this.openGui, this.swapMode, this.dropMode, this.sortMode, this.sortMethod);
        this.swapDelay.addSettings();
        this.dropDelay.addSettings();
        this.sortDelay.addSettings();
        this.addSettings(this.minHoldTime);


    }

    /**
     * Queues an item swap
     * @param srcSlot The slot of the item to move (source slot)
     * @param dstSlot The destination slot of the item
     */
    private void queueSwap(int srcSlot, int dstSlot) {

        SortMethod sortMethod = this.sortMethod.get();
        if (srcSlot < InventoryUtil.EXCLUDE_ARMOR_BEGIN || dstSlot < InventoryUtil.EXCLUDE_ARMOR_BEGIN) {
            sortMethod = SortMethod.CLICK; // Swapping won't work with armor slots so force it to use clicking
        }

        switch (sortMethod) {
            case CLICK:
                // Pick up the item
                InventoryUtil.click(srcSlot, 0, false);

                // Place the item in the destination slot
                this.inventoryActions.add(new InventoryAction(() -> InventoryUtil.click(dstSlot, 0, false), this.sortDelay.getDelay()));

                // Place whatever item was in the destination slot in the source slot (if any)
                this.inventoryActions.add(new InventoryAction(() -> InventoryUtil.click(srcSlot, 0, false), this.sortDelay.getDelay()));
                break;

            case SWAP:
                // Swap the item in the source slot with an arbitrary hotbar slot (in this case we're using the 8th
                // hotbar slot)
                InventoryUtil.hotbarSwap(srcSlot, 7);

                // Swap the item in the destination slot with the arbitrary slot which will result in the source item
                // being in the destination slot
                this.inventoryActions.add(new InventoryAction(() -> InventoryUtil.hotbarSwap(dstSlot, 7), this.sortDelay.getDelay()));

                // Swap the source slot and arbitrary slot again so that the destination item is in the source slot and
                // the item that was in the arbitrary slot is back in that slot
                this.inventoryActions.add(new InventoryAction(() -> InventoryUtil.hotbarSwap(srcSlot, 7), this.sortDelay.getDelay()));
                break;

            default:
                throw new IllegalStateException("Sort Method setting returned null... (how?)");
        }
    }

    /**
     * A wrapper that helps withe different cases olf item swapping
     *
     * @param srcSlot The slot of the item to move (source slot)
     * @param dstSlot The destination slot of the item
     */
    private void swap(int srcSlot, int dstSlot) {

        // Swap the items
        if (srcSlot != dstSlot && dstSlot >= InventoryUtil.HOTBAR_BEGIN && dstSlot < InventoryUtil.END) {
            InventoryUtil.hotbarSwap(srcSlot, dstSlot - InventoryUtil.HOTBAR_BEGIN);
        } else if (srcSlot != dstSlot && dstSlot >= InventoryUtil.INCLUDE_ARMOR_BEGIN && dstSlot < InventoryUtil.HOTBAR_BEGIN) {
            this.queueSwap(srcSlot, dstSlot);
        } else {
            ChatUtil.debug("Swap called with unexpected args: %d -> %d", srcSlot, dstSlot);
        }

        // Update the trackers
        ItemSlot srcItemSlot = this.itemSlots.get(srcSlot);
        ItemSlot dstItemSlot = this.itemSlots.get(dstSlot);

        if (srcItemSlot != null) {
            srcItemSlot.setSlot(srcSlot);
        }

        if (dstItemSlot != null) {
            dstItemSlot.setSlot(srcSlot);
        }

        this.itemSlots.put(srcSlot, srcItemSlot);
        this.itemSlots.put(dstSlot, dstItemSlot);

        this.itemTimeInSlot.put(srcSlot, 0);
        this.itemTimeInSlot.put(dstSlot, 0);

    }

    private void drop(int slot, boolean hotbar) {
        if (hotbar) {
            int currentSlot = InventoryUtil.serverItem;
            InventoryUtil.serverItem = slot - 36;
            this.delay = this.dropDelay.getDelay();
            this.hotbarActions.add(new InventoryAction(() -> mc.thePlayer.dropOneItem(true), this.dropDelay.getDelay()));
            this.hotbarActions.add(new InventoryAction(() -> InventoryUtil.serverItem = currentSlot, this.dropDelay.getDelay()));
        } else {
            InventoryUtil.drop(slot);
        }
        this.itemSlots.put(slot, null);
        this.itemTimeInSlot.put(slot, 0);
    }

    private void update() {

        ChatUtil.debug("InvManager: Update");

        // Search through all the items, add them to the relevant trackers and determine whether the item is garbage
        for (int slot = InventoryUtil.INCLUDE_ARMOR_BEGIN; slot < InventoryUtil.END; slot++) {
            ItemStack item = InventoryUtil.getStackInSlot(slot);
            if (item != null) {
                boolean isGarbage = true;

                ItemSlot itemSlot = new ItemSlot(item, slot);
                this.itemSlots.put(slot, itemSlot);
                for (ItemTracker tracker : this.itemTrackers) {
                    if (tracker.isItemType(itemSlot.getItemStack())) {
                        tracker.addItem(itemSlot);
                        isGarbage = false;
                    }
                }

                if (isGarbage) {
                    this.garbageSlots.add(slot);
                }
            }

            this.prevTickItems.put(slot, this.currentTickItems.get(slot));

            if (item != null) {
                this.currentTickItems.put(slot, item);
            } else {
                this.currentTickItems.remove(slot);
            }

            ItemStack prevTickItem = this.prevTickItems.get(slot);
            ItemStack currentTickItem = this.currentTickItems.get(slot);
            if (!this.itemTimeInSlot.containsKey(slot) ||
                    (prevTickItem == null && currentTickItem != null) ||
                    (prevTickItem != null && currentTickItem == null) ||
                    !ItemStack.areItemStacksEqual(prevTickItem, currentTickItem)) {
                this.itemTimeInSlot.put(slot, 0);
            } else {
                this.itemTimeInSlot.put(slot, this.itemTimeInSlot.getOrDefault(slot, 0) + 1);
            }

        }

        this.computeSwaps();
        this.computeDrops();
        this.computeSorts();

        ChatUtil.debug("InvManager: End Update");
    }

    private void computeSwaps() {

        ChatUtil.debug("InvManager: Computing Swaps");
        this.swapsToPerform.clear();

        if (this.swapMode.is(ActionMode.NONE)) {
            return;
        }

        for (ItemSwapper swapper : this.itemSwappers) {
            ItemTracker tracker = swapper.getItemTracker();
            int targetSlot = swapper.getTargetSlot();
            if (targetSlot >= InventoryUtil.HOTBAR_BEGIN && targetSlot < InventoryUtil.END
                    && tracker.getAmountOfItems() > 0 && tracker.getLast().getSlot() != targetSlot) {
                ItemSlot bestItem = tracker.getLast();
                ItemSlot currentItem = this.itemSlots.get(targetSlot);
                int timeInSlot = this.itemTimeInSlot.getOrDefault(targetSlot, -1);
                if (timeInSlot != -1 && timeInSlot >= this.minHoldTime.get() / 50 &&
                        (currentItem == null
                                || !tracker.isItemType(currentItem.getItemStack())
                                || tracker.getItemScore(bestItem.getItemStack()) > tracker.getItemScore(currentItem.getItemStack()))) {
                    this.swapsToPerform.add(new SwapAction(bestItem.getSlot(), targetSlot));
                }
            }
        }

        this.sortActions(this.swapsToPerform, this.swapMode.get(), Comparator.comparingInt(swap -> swap.dstSlot));

        ChatUtil.debug("Swap computing done: %d", this.swapsToPerform.size());

    }

    private boolean doSwaps() {

        ChatUtil.debug("InvManager: swapping items");
        boolean didSwap = false;

        while (!this.swapsToPerform.isEmpty()) {
            SwapAction action = this.swapsToPerform.remove(0);
            this.swap(action.srcSlot, action.dstSlot);
            didSwap = true;

            this.delay = this.swapDelay.getDelay();
            if (this.delay > 0) {
                break;
            }
        }

        if (!didSwap) {
            ChatUtil.debug("InvManager: Nothing to swap!");
        }

        return didSwap;
    }

    private void computeDrops() {

        ChatUtil.debug("InvManager: Computing Drops");
        this.dropsToPerform.clear();

        if (this.dropMode.is(ActionMode.NONE)) {
            return;
        }

        for (ItemTracker tracker : this.itemTrackers) {
            if (tracker.getAmountToKeep() != -1) {
                for (int i = 0; i < tracker.getItems().size() - tracker.getAmountToKeep(); i++) {
                    ItemSlot itemSlot = tracker.getItems().get(i);
                    int timeInSlot = this.itemTimeInSlot.getOrDefault(itemSlot.getSlot(), -1);
                    if (timeInSlot != -1 && timeInSlot >= this.minHoldTime.get() / 50) {
                        this.dropsToPerform.add(itemSlot.getSlot());
                    }
                }
            }
        }

        if (this.dropGarbage.get()) {
            for (int slot : this.garbageSlots) {
                int timeInSlot = this.itemTimeInSlot.getOrDefault(slot, -1);
                if (timeInSlot != -1 && timeInSlot >= this.minHoldTime.get() / 50) {
                    this.dropsToPerform.add(slot);
                }
            }
        }

        this.sortActions(this.dropsToPerform, this.dropMode.get(), Integer::compare);

        ChatUtil.debug("Drop computing done: %d", this.dropsToPerform.size());
    }

    private boolean doDrops() {

        ChatUtil.debug("InvManager: dropping items");
        boolean didDrop = false;

        while (!this.dropsToPerform.isEmpty()) {
            int slot = this.dropsToPerform.remove(0);
            this.drop(slot, false);
            didDrop = true;

            this.delay = this.dropDelay.getDelay();
            if (this.delay > 0) {
                break;
            }

        }

        if (!didDrop) {
            ChatUtil.debug("InvManager: Nothing to drop!");
        }

        return didDrop;
    }

    private void queueHotbarDrops() {
        List<Integer> hotbarDrops = this.dropsToPerform.stream()
                .filter(slot -> slot >= InventoryUtil.HOTBAR_BEGIN && slot < InventoryUtil.END)
                .collect(Collectors.toList());

        for (int slot : hotbarDrops) {
            this.drop(slot, true);
        }
    }

    private void computeSorts() {

        ChatUtil.debug("InvManager: Computing Sorts");
        this.sortsToPerform.clear();

        if (this.sortMode.is(ActionMode.NONE)) {
            return;
        }

        for (ItemSorter sorter : this.itemSorters) {
            ItemTracker tracker = sorter.getItemTracker();
            if (tracker.getAmountOfItems() > 0) {
                List<ItemSlot> itemSlots = tracker.getItems();
                itemSlots.sort(Comparator.comparingDouble(slot -> tracker.getItemScore(slot.getItemStack())));

                for (int rank = 0; rank < itemSlots.size(); rank++) {
                    ItemSlot itemSlot = itemSlots.get(itemSlots.size() - rank - 1);
                    int targetSlot = sorter.getTargetSlot(rank, itemSlot.getSlot());
                    ItemSlot currentItem = this.itemSlots.get(targetSlot);
                    int timeInSlot = this.itemTimeInSlot.getOrDefault(itemSlot.getSlot(), -1);
                    if (timeInSlot != -1 && timeInSlot > this.minHoldTime.get() / 50
                            && targetSlot != -1
                            && itemSlot.getSlot() != targetSlot
                            && (currentItem == null || tracker.getItemScore(itemSlot.getItemStack()) > tracker.getItemScore(currentItem.getItemStack()))) {
                        this.sortsToPerform.add(new SwapAction(itemSlot.getSlot(), targetSlot));
                    }
                }
            }
        }

        this.sortActions(this.sortsToPerform, this.sortMode.get(), Comparator.comparingInt(o -> o.dstSlot));

        ChatUtil.debug("Sort computing done: %d", this.sortsToPerform.size());
    }

    private boolean doSorts() {

        ChatUtil.debug("InvManager: sorting items");
        boolean didSort = false;

        while (!this.sortsToPerform.isEmpty()) {
            SwapAction action = this.sortsToPerform.remove(0);
            this.swap(action.srcSlot, action.dstSlot);
            didSort = true;

            this.delay = this.sortDelay.getDelay();
            if (this.delay > 0) {
                break;
            }
        }

        if (!didSort) {
            ChatUtil.debug("InvManager: Nothing to sort!");
        }

        return didSort;
    }

    private void reset() {
        for (ItemTracker itemTracker : this.itemTrackers) {
            itemTracker.clear();
        }
        this.itemSlots.clear();
        this.garbageSlots.clear();
    }

    private <T> void sortActions(List<T> actions, ActionMode mode, Comparator<T> comparator) {

        switch (mode) {

            case REVERSE_PRIORITY: // Reverse the actions list
                Collections.reverse(actions);
                break;

            case SEQUENTIAL: // Sort the actions list
                actions.sort(comparator);
                break;

            case REVERSE: // Sort and reverse the actions list
                actions.sort(comparator);
                Collections.reverse(actions);
                break;

            case RANDOM: // Shuffle the actions list
                Collections.shuffle(actions);
                break;

            case NONE: // Clear the actions list
                actions.clear();
                break;
        }

    }

    private boolean canUpdate() {
        return this.inventoryActions.isEmpty() && this.hotbarActions.isEmpty() && this.delay <= 0;
    }

    private boolean canInventoryInteract() {
        return (!this.openGui.get() || mc.currentScreen instanceof GuiInventory) || !MoveUtil.isMovementInput() && this.delay <= 0;
    }

    private boolean canHotbarInteract() {
        return this.hotbarDrop.get() && this.delay <= 0 && !Nonsense.module(KillAura.class).hasTarget() && !Nonsense.module(Scaffold.class).isEnabled();
    }

    private boolean doInventoryQueueActions() {
        boolean didAction = false;
        while (!this.inventoryActions.isEmpty()) {
            InventoryAction action = this.inventoryActions.remove(0);
            action.execute();
            didAction = true;
            this.delay = action.getDelay();
            if (this.delay > 0) {
                break;
            }
        }

        return didAction;
    }

    private boolean doHotbarQueueActions() {
        boolean didAction = false;
        while (!this.hotbarActions.isEmpty()) {
            InventoryAction action = this.hotbarActions.remove(0);
            action.execute();
            didAction = true;
            this.delay = action.getDelay();
            if (this.delay > 0) {
                break;
            }
        }

        return didAction;
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {

        if (this.canUpdate()) {

            this.update();

            if (this.canInventoryInteract()) {
                this.doSwaps();
            }

            if (this.canInventoryInteract()) {
                this.doDrops();
            } else if (this.canHotbarInteract()) {
                this.queueHotbarDrops();
            }

            if (this.canInventoryInteract()) {
                this.doSorts();
            }

            this.reset();
        } else if (this.canInventoryInteract()) {
            this.doInventoryQueueActions();
        } else if (this.canHotbarInteract()) {
            this.doHotbarQueueActions();
        }

        this.delay--;
    }

    private static IntSetting newSlotSetting(String name, int defaultValue) {
        return new IntSetting(name, name + " slot", 0, 9, defaultValue);
    }

    private static IntSetting newSlotSetting(String name) {
        return new IntSetting(name, name + " slot", 0, 9, 0);
    }

    public enum ActionMode {
        PRIORITY,
        REVERSE_PRIORITY,
        SEQUENTIAL,
        REVERSE,
        RANDOM,
        NONE
    }

    public enum SortMethod {
        CLICK,
        SWAP
    }

    /**
     * Wrapper class for 2 integer settings to make the delay settings easier
     */
    public class DelaySetting {

        private final IntSetting min;
        private final IntSetting max;

        public DelaySetting(String name, int min, int max, int defaultMin, int defaultMax) {
            this.min = new IntSetting("Min " + name, "Minimum " + name, min, max, defaultMin, "%dms", this::changedMin);
            this.max = new IntSetting("Max " + name, "Maximum " + name, min, max, defaultMax, "%dms", this::changedMax);
        }

        public int getDelay() {
            if (this.min.get().equals(this.max.get())) {
                return this.min.get() / 50;
            }
            return ThreadLocalRandom.current().nextInt(this.min.get(), this.max.get()) / 50;
        }

        private void changedMin(Integer value) {
            if (this.max.get() < value) {
                this.max.set(value);
            }
        }

        private void changedMax(Integer value) {
            if (this.min.get() > value) {
                this.min.set(value);
            }
        }

        public void addSettings() {
            InventoryManager.this.addSettings(this.min, this.max);
        }

    }

    /**
     * A wrapper class for the item amount settings that will automatically register the swappers, sorters and trackers
     */
    private class DropSetting extends IntSetting {

        private final ItemTypeChecker itemTypeChecker;
        private final ItemScoreCalculator itemScoreCalculator;
        private final ItemAmountChecker itemAmountChecker;
        private final ItemSwapper.TargetSlotMethod swapMethod;
        private final ItemSorter.TargetSlotMethod sortMethod;

        public DropSetting(String name,
                           int defaultValue,
                           ItemTypeChecker typeChecker,
                           ItemScoreCalculator scoreCalculator) {
            this(name, defaultValue, typeChecker, scoreCalculator, null, null);
        }

        public DropSetting(String name,
                           int defaultValue,
                           ItemTypeChecker typeChecker,
                           ItemScoreCalculator scoreCalculator,
                           ItemSwapper.TargetSlotMethod swapMethod,
                           ItemSorter.TargetSlotMethod sortMethod) {
            super(name, name + " stacks", 0, 5, defaultValue, "%d stacks", null);
            this.itemTypeChecker = typeChecker;
            this.itemScoreCalculator = scoreCalculator;
            this.itemAmountChecker = this::get;
            this.swapMethod = swapMethod;
            this.sortMethod = sortMethod;
            this.generateTracker();
        }

        private void generateTracker() {
            ItemTracker tracker = new ItemTracker(this.itemTypeChecker, this.itemScoreCalculator, this.itemAmountChecker);
            InventoryManager.this.itemTrackers.add(tracker);
            if (this.swapMethod != null) {
                InventoryManager.this.itemSwappers.add(new ItemSwapper(tracker, this.swapMethod));
            }
            if (this.sortMethod != null) {
                InventoryManager.this.itemSorters.add(new ItemSorter(tracker, this.sortMethod));
            }
        }

    }

    public class PotionDropSetting extends DropSetting {

        public PotionDropSetting(String name, int defaultValue, Potion potion) {
            super(name, defaultValue,
                    stack -> InventoryUtil.isPotion(stack, potion),
                    stack -> ItemScores.potion(stack, potion));
        }

        public PotionDropSetting(String name, int defaultValue, Potion potion, Potion remove) {
            super(name, defaultValue,
                    stack -> InventoryUtil.isPotion(stack, potion) && !InventoryUtil.isPotion(stack, remove),
                    stack -> ItemScores.potion(stack, potion));
        }
    }

    public static class SwapAction {
        public final int srcSlot;
        public final int dstSlot;

        public SwapAction(int srcSlot, int dstSlot) {
            this.srcSlot = srcSlot;
            this.dstSlot = dstSlot;
        }
    }

}
