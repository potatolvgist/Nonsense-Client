package wtf.bhopper.nonsense.gui.screens.creative;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.inventory.CreativeCrafting;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import wtf.bhopper.nonsense.Nonsense;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class GuiCustomCreative extends InventoryEffectRenderer {

    /**
     * The location of the creative inventory tabs texture
     */
    private static final ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private static final InventoryBasic inventoryBasic = new InventoryBasic("tmp", true, 45);

    /**
     * Currently selected creative inventory tab index.
     */
    private static int selectedTabIndex = CustomCreativeTabs.tabUnobtainable.getTabIndex();

    /**
     * Amount scrolled in Creative mode inventory (0 = top, 1 = bottom)
     */
    private float currentScroll;

    /**
     * True if the scrollbar is being dragged
     */
    private boolean isScrolling;

    /**
     * True if the left mouse button was held down last time drawScreen was called.
     */
    private boolean wasClicking;
    private GuiTextField searchField;
    private List<Slot> slots;
    private Slot field_147064_C;
    private boolean field_147057_D;
    private CreativeCrafting creativeCrafting;

    public GuiCustomCreative(EntityPlayer player) {
        super(new ContainerCreative(player));
        player.openContainer = this.inventorySlots;
        this.allowUserInput = true;
        this.ySize = 136;
        this.xSize = 195;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        if (!this.mc.playerController.isInCreativeMode()) {
            this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
        }

        this.updateActivePotionEffects();
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        this.field_147057_D = true;
        boolean flag = clickType == 1;
        clickType = slotId == -999 && clickType == 0 ? 4 : clickType;

        if (slotIn == null && selectedTabIndex != CustomCreativeTabs.tabInventory.getTabIndex() && clickType != 5) {
            InventoryPlayer inventoryplayer1 = this.mc.thePlayer.inventory;

            if (inventoryplayer1.getItemStack() != null) {
                if (clickedButton == 0) {
                    this.mc.thePlayer.dropPlayerItemWithRandomChoice(inventoryplayer1.getItemStack(), true);
                    this.mc.playerController.sendPacketDropItem(inventoryplayer1.getItemStack());
                    inventoryplayer1.setItemStack(null);
                }

                if (clickedButton == 1) {
                    ItemStack itemstack5 = inventoryplayer1.getItemStack().splitStack(1);
                    this.mc.thePlayer.dropPlayerItemWithRandomChoice(itemstack5, true);
                    this.mc.playerController.sendPacketDropItem(itemstack5);

                    if (inventoryplayer1.getItemStack().stackSize == 0) {
                        inventoryplayer1.setItemStack(null);
                    }
                }
            }
        } else if (slotIn == this.field_147064_C && flag) {
            for (int j = 0; j < this.mc.thePlayer.inventoryContainer.getInventory().size(); ++j) {
                this.mc.playerController.sendSlotPacket(null, j);
            }
        } else if (selectedTabIndex == CustomCreativeTabs.tabInventory.getTabIndex()) {
            if (slotIn == this.field_147064_C) {
                this.mc.thePlayer.inventory.setItemStack(null);
            } else if (clickType == 4 && slotIn != null && slotIn.getHasStack()) {
                ItemStack itemstack = slotIn.decrStackSize(clickedButton == 0 ? 1 : slotIn.getStack().getMaxStackSize());
                this.mc.thePlayer.dropPlayerItemWithRandomChoice(itemstack, true);
                this.mc.playerController.sendPacketDropItem(itemstack);
            } else if (clickType == 4 && this.mc.thePlayer.inventory.getItemStack() != null) {
                this.mc.thePlayer.dropPlayerItemWithRandomChoice(this.mc.thePlayer.inventory.getItemStack(), true);
                this.mc.playerController.sendPacketDropItem(this.mc.thePlayer.inventory.getItemStack());
                this.mc.thePlayer.inventory.setItemStack(null);
            } else {
                this.mc.thePlayer.inventoryContainer.slotClick(slotIn == null ? slotId : ((CreativeSlot) slotIn).slot.slotNumber, clickedButton, clickType, this.mc.thePlayer);
                this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
            }
        } else if (clickType != 5 && slotIn.inventory == inventoryBasic) {
            InventoryPlayer inventoryplayer = this.mc.thePlayer.inventory;
            ItemStack itemstack1 = inventoryplayer.getItemStack();
            ItemStack itemstack2 = slotIn.getStack();

            if (clickType == 2) {
                if (itemstack2 != null && clickedButton >= 0 && clickedButton < 9) {
                    ItemStack itemstack7 = itemstack2.copy();
                    itemstack7.stackSize = itemstack7.getMaxStackSize();
                    this.mc.thePlayer.inventory.setInventorySlotContents(clickedButton, itemstack7);
                    this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
                }

                return;
            }

            if (clickType == 3) {
                if (inventoryplayer.getItemStack() == null && slotIn.getHasStack()) {
                    ItemStack itemstack6 = slotIn.getStack().copy();
                    itemstack6.stackSize = itemstack6.getMaxStackSize();
                    inventoryplayer.setItemStack(itemstack6);
                }

                return;
            }

            if (clickType == 4) {
                if (itemstack2 != null) {
                    ItemStack itemstack3 = itemstack2.copy();
                    itemstack3.stackSize = clickedButton == 0 ? 1 : itemstack3.getMaxStackSize();
                    this.mc.thePlayer.dropPlayerItemWithRandomChoice(itemstack3, true);
                    this.mc.playerController.sendPacketDropItem(itemstack3);
                }

                return;
            }

            if (itemstack1 != null && itemstack1.isItemEqual(itemstack2)) {
                if (clickedButton == 0) {
                    if (flag) {
                        itemstack1.stackSize = itemstack1.getMaxStackSize();
                    } else if (itemstack1.stackSize < itemstack1.getMaxStackSize()) {
                        ++itemstack1.stackSize;
                    }
                } else if (itemstack1.stackSize <= 1) {
                    inventoryplayer.setItemStack(null);
                } else {
                    --itemstack1.stackSize;
                }
            } else if (itemstack2 != null && itemstack1 == null) {
                inventoryplayer.setItemStack(ItemStack.copyItemStack(itemstack2));
                itemstack1 = inventoryplayer.getItemStack();

                if (flag) {
                    itemstack1.stackSize = itemstack1.getMaxStackSize();
                }
            } else {
                inventoryplayer.setItemStack(null);
            }
        } else {
            this.inventorySlots.slotClick(slotIn == null ? slotId : slotIn.slotNumber, clickedButton, clickType, this.mc.thePlayer);

            if (Container.getDragEvent(clickedButton) == 2) {
                for (int i = 0; i < 9; ++i) {
                    this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + i).getStack(), 36 + i);
                }
            } else if (slotIn != null) {
                ItemStack itemstack4 = this.inventorySlots.getSlot(slotIn.slotNumber).getStack();
                this.mc.playerController.sendSlotPacket(itemstack4, slotIn.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
            }
        }
    }

    protected void updateActivePotionEffects() {
        int i = this.guiLeft;
        super.updateActivePotionEffects();

        if (this.searchField != null && this.guiLeft != i) {
            this.searchField.xPosition = this.guiLeft + 82;
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        if (this.mc.playerController.isInCreativeMode()) {
            super.initGui();
            this.buttonList.clear();
            Keyboard.enableRepeatEvents(true);
            this.searchField = new GuiTextField(0, this.fontRendererObj, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRendererObj.FONT_HEIGHT);
            this.searchField.setMaxStringLength(15);
            this.searchField.setEnableBackgroundDrawing(false);
            this.searchField.setVisible(false);
            this.searchField.setTextColor(0xffffff);
            int i = selectedTabIndex;
            selectedTabIndex = -1;
            this.setCurrentCreativeTab(CustomCreativeTabs.TABS[i]);
            this.creativeCrafting = new CreativeCrafting(this.mc);
            this.mc.thePlayer.inventoryContainer.onCraftGuiOpened(this.creativeCrafting);
            this.buttonList.add(new GuiButton(69, this.guiLeft, this.guiTop - 50, 100, 20, "Tabs: \247c" + Nonsense.NAME));
        } else {
            this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
        super.onGuiClosed();

        if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
            this.mc.thePlayer.inventoryContainer.removeCraftingFromCrafters(this.creativeCrafting);
        }

        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

            if (this.field_147057_D) {
                this.field_147057_D = false;
                this.searchField.setText("");
            }

            if (!this.checkHotbarKeys(keyCode)) {
                if (this.searchField.textboxKeyTyped(typedChar, keyCode)) {
                    this.updateCreativeSearch();
                } else {
                    super.keyTyped(typedChar, keyCode);
                }
            }

    }

    private void updateCreativeSearch() {
        ContainerCreative containerCreative = (ContainerCreative) this.inventorySlots;
        containerCreative.itemList.clear();

        for (Item item : Item.itemRegistry) {
            if (item != null && item.getCreativeTab() != null) {
                item.getSubItems(item, null, containerCreative.itemList);
            }
        }

        for (Enchantment enchantment : Enchantment.enchantmentsBookList) {
            if (enchantment != null && enchantment.type != null) {
                Items.enchanted_book.getAll(enchantment, containerCreative.itemList);
            }
        }

        Iterator<ItemStack> iterator = containerCreative.itemList.iterator();
        String s1 = this.searchField.getText().toLowerCase();

        while (iterator.hasNext()) {
            ItemStack itemstack = iterator.next();
            boolean flag = false;

            for (String s : itemstack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips)) {
                if (EnumChatFormatting.getTextWithoutFormattingCodes(s).toLowerCase().contains(s1)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                iterator.remove();
            }
        }

        this.currentScroll = 0.0F;
        containerCreative.scrollTo(0.0F);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        CreativeTabs creativetabs = CustomCreativeTabs.TABS[selectedTabIndex];

        if (creativetabs.drawInForegroundOfTab()) {
            GlStateManager.disableBlend();
            this.fontRendererObj.drawString(creativetabs.getTabLabel(), 8, 6, 0x404040);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int i = mouseX - this.guiLeft;
            int j = mouseY - this.guiTop;

            for (CreativeTabs creativetabs : CustomCreativeTabs.TABS) {
                if (this.mouseIntersectingWithTab(creativetabs, i, j)) {
                    return;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            int relativeMouseX = mouseX - this.guiLeft;
            int relativeMouseY = mouseY - this.guiTop;

            for (CreativeTabs creativetabs : CustomCreativeTabs.TABS) {
                if (this.mouseIntersectingWithTab(creativetabs, relativeMouseX, relativeMouseY)) {
                    this.setCurrentCreativeTab(creativetabs);
                    return;
                }
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * returns (if you are not on the inventoryTab) and (the flag isn't set) and (you have more than 1 page of items)
     */
    private boolean needsScrollBars() {
        return selectedTabIndex != CustomCreativeTabs.tabInventory.getTabIndex() && CustomCreativeTabs.TABS[selectedTabIndex].shouldHidePlayerInventory() && ((ContainerCreative) this.inventorySlots).func_148328_e();
    }

    private void setCurrentCreativeTab(CreativeTabs creativeTabs) {
        int i = selectedTabIndex;
        selectedTabIndex = creativeTabs.getTabIndex();
        ContainerCreative containerCreative = (ContainerCreative) this.inventorySlots;
        this.dragSplittingSlots.clear();
        containerCreative.itemList.clear();
        creativeTabs.displayAllReleventItems(containerCreative.itemList);

        if (creativeTabs == CustomCreativeTabs.tabInventory) {
            Container container = this.mc.thePlayer.inventoryContainer;

            if (this.slots == null) {
                this.slots = containerCreative.inventorySlots;
            }

            containerCreative.inventorySlots = Lists.newArrayList();

            for (int j = 0; j < container.inventorySlots.size(); ++j) {
                Slot slot = new CreativeSlot(container.inventorySlots.get(j), j);
                containerCreative.inventorySlots.add(slot);

                if (j >= 5 && j < 9) {
                    int j1 = j - 5;
                    int k1 = j1 / 2;
                    int l1 = j1 % 2;
                    slot.xDisplayPosition = 9 + k1 * 54;
                    slot.yDisplayPosition = 6 + l1 * 27;
                } else if (j < 5) {
                    slot.yDisplayPosition = -2000;
                    slot.xDisplayPosition = -2000;
                } else if (j < container.inventorySlots.size()) {
                    int k = j - 9;
                    int l = k % 9;
                    int i1 = k / 9;
                    slot.xDisplayPosition = 9 + l * 18;

                    if (j >= 36) {
                        slot.yDisplayPosition = 112;
                    } else {
                        slot.yDisplayPosition = 54 + i1 * 18;
                    }
                }
            }

            this.field_147064_C = new Slot(inventoryBasic, 0, 173, 112);
            containerCreative.inventorySlots.add(this.field_147064_C);
        } else if (i == CustomCreativeTabs.tabInventory.getTabIndex()) {
            containerCreative.inventorySlots = this.slots;
            this.slots = null;
        }

        if (this.searchField != null) {
            if (creativeTabs == CustomCreativeTabs.tabAllSearch) {
                this.searchField.setVisible(true);
                this.searchField.setCanLoseFocus(false);
                this.searchField.setFocused(true);
                this.searchField.setText("");
                this.updateCreativeSearch();
            } else {
                this.searchField.setVisible(false);
                this.searchField.setCanLoseFocus(true);
                this.searchField.setFocused(false);
            }
        }

        this.currentScroll = 0.0F;
        containerCreative.scrollTo(0.0F);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0 && this.needsScrollBars()) {
            int j = ((ContainerCreative) this.inventorySlots).itemList.size() / 9 - 5;

            if (i > 0) {
                i = 1;
            }

            if (i < 0) {
                i = -1;
            }

            this.currentScroll = (float) ((double) this.currentScroll - (double) i / (double) j);
            this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
            ((ContainerCreative) this.inventorySlots).scrollTo(this.currentScroll);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean flag = Mouse.isButtonDown(0);
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;

        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1) {
            this.isScrolling = this.needsScrollBars();
        }

        if (!flag) {
            this.isScrolling = false;
        }

        this.wasClicking = flag;

        if (this.isScrolling) {
            this.currentScroll = ((float) (mouseY - l) - 7.5F) / ((float) (j1 - l) - 15.0F);
            this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
            ((ContainerCreative) this.inventorySlots).scrollTo(this.currentScroll);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (CreativeTabs creativetabs : CustomCreativeTabs.TABS) {
            if (this.renderCreativeInventoryHoveringText(creativetabs, mouseX, mouseY)) {
                break;
            }
        }

        if (this.field_147064_C != null && selectedTabIndex == CustomCreativeTabs.tabInventory.getTabIndex() && this.isPointInRegion(this.field_147064_C.xDisplayPosition, this.field_147064_C.yDisplayPosition, 16, 16, mouseX, mouseY)) {
            this.drawCreativeTabHoveringText(I18n.format("inventory.binSlot"), mouseX, mouseY);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        CreativeTabs creativetabs = CustomCreativeTabs.TABS[selectedTabIndex];

        for (CreativeTabs creativetabs1 : CustomCreativeTabs.TABS) {

            if (creativetabs1 == null) {
                continue;
            }

            this.mc.getTextureManager().bindTexture(creativeInventoryTabs);

            if (creativetabs1.getTabIndex() != selectedTabIndex) {
                this.func_147051_a(creativetabs1);
            }
        }

        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + creativetabs.getBackgroundImageName()));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.searchField.drawTextBox();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.guiLeft + 175;
        int j = this.guiTop + 18;
        int k = j + 112;
        this.mc.getTextureManager().bindTexture(creativeInventoryTabs);

        if (creativetabs.shouldHidePlayerInventory()) {
            this.drawTexturedModalRect(i, j + (int) ((float) (k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
        }

        this.func_147051_a(creativetabs);

        if (creativetabs == CustomCreativeTabs.tabInventory) {
            GuiInventory.drawEntityOnScreen(this.guiLeft + 43, this.guiTop + 45, 20, (float) (this.guiLeft + 43 - mouseX), (float) (this.guiTop + 45 - 30 - mouseY), this.mc.thePlayer);
        }
    }

    protected boolean mouseIntersectingWithTab(CreativeTabs creativeTabs, int mouseX, int mouseY) {
        int i = creativeTabs.getTabColumn();
        int j = 28 * i;
        int k = 0;

        if (i == 5) {
            j = this.xSize - 28 + 2;
        } else if (i > 0) {
            j += i;
        }

        if (creativeTabs.isTabInFirstRow()) {
            k = k - 32;
        } else {
            k = k + this.ySize;
        }

        return mouseX >= j && mouseX <= j + 28 && mouseY >= k && mouseY <= k + 32;
    }

    /**
     * Renders the creative inventory hovering text if mouse is over it. Returns true if did render or false otherwise.
     * Params: current creative tab to be checked, current mouse x position, current mouse y position.
     */
    protected boolean renderCreativeInventoryHoveringText(CreativeTabs creativeTabs, int mouseX, int mouseY) {
        int column = creativeTabs.getTabColumn();
        int x = 28 * column;
        int y = 0;

        if (column == 5) {
            x = this.xSize - 28 + 2;
        } else if (column > 0) {
            x += column;
        }

        if (creativeTabs.isTabInFirstRow()) {
            y = y - 32;
        } else {
            y = y + this.ySize;
        }

        if (this.isPointInRegion(x + 3, y + 3, 23, 27, mouseX, mouseY)) {
            this.drawCreativeTabHoveringText(creativeTabs.getTabLabel(), mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    protected void func_147051_a(CreativeTabs p_147051_1_) {
        boolean flag = p_147051_1_.getTabIndex() == selectedTabIndex;
        boolean flag1 = p_147051_1_.isTabInFirstRow();
        int i = p_147051_1_.getTabColumn();
        int j = i * 28;
        int k = 0;
        int l = this.guiLeft + 28 * i;
        int i1 = this.guiTop;
        int j1 = 32;

        if (flag) {
            k += 32;
        }

        if (i == 5) {
            l = this.guiLeft + this.xSize - 28;
        } else if (i > 0) {
            l += i;
        }

        if (flag1) {
            i1 = i1 - 28;
        } else {
            k += 64;
            i1 = i1 + (this.ySize - 4);
        }

        GlStateManager.disableLighting();
        this.drawTexturedModalRect(l, i1, j, k, 28, j1);
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        l = l + 6;
        i1 = i1 + 8 + (flag1 ? 1 : -1);
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        ItemStack itemstack = p_147051_1_.getIconItemStack();
        this.itemRender.renderItemAndEffectIntoGUI(itemstack, l, i1);
        this.itemRender.renderItemOverlays(this.fontRendererObj, itemstack, l, i1);
        GlStateManager.disableLighting();
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
        }

        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
        }

        if (button.id == 69) {
            GuiContainerCreative.custom = false;
            this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
        }
    }

    public int getSelectedTabIndex() {
        return selectedTabIndex;
    }

    static class ContainerCreative extends Container {
        public List<ItemStack> itemList = Lists.newArrayList();

        public ContainerCreative(EntityPlayer p_i1086_1_) {
            InventoryPlayer inventoryplayer = p_i1086_1_.inventory;

            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlotToContainer(new Slot(inventoryBasic, i * 9 + j, 9 + j * 18, 18 + i * 18));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(inventoryplayer, k, 9 + k * 18, 112));
            }

            this.scrollTo(0.0F);
        }

        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }

        public void scrollTo(float p_148329_1_) {
            int i = (this.itemList.size() + 9 - 1) / 9 - 5;
            int j = (int) ((double) (p_148329_1_ * (float) i) + 0.5D);

            if (j < 0) {
                j = 0;
            }

            for (int k = 0; k < 5; ++k) {
                for (int l = 0; l < 9; ++l) {
                    int i1 = l + (k + j) * 9;

                    if (i1 >= 0 && i1 < this.itemList.size()) {
                        inventoryBasic.setInventorySlotContents(l + k * 9, this.itemList.get(i1));
                    } else {
                        inventoryBasic.setInventorySlotContents(l + k * 9, null);
                    }
                }
            }
        }

        public boolean func_148328_e() {
            return this.itemList.size() > 45;
        }

        protected void retrySlotClick(int slotId, int clickedButton, boolean mode, EntityPlayer playerIn) {
        }

        public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
            if (index >= this.inventorySlots.size() - 9 && index < this.inventorySlots.size()) {
                Slot slot = this.inventorySlots.get(index);

                if (slot != null && slot.getHasStack()) {
                    slot.putStack(null);
                }
            }

            return null;
        }

        public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
            return slotIn.yDisplayPosition > 90;
        }

        public boolean canDragIntoSlot(Slot p_94531_1_) {
            return p_94531_1_.inventory instanceof InventoryPlayer || p_94531_1_.yDisplayPosition > 90 && p_94531_1_.xDisplayPosition <= 162;
        }
    }

    static class CreativeSlot extends Slot {
        private final Slot slot;

        public CreativeSlot(Slot p_i46313_2_, int p_i46313_3_) {
            super(p_i46313_2_.inventory, p_i46313_3_, 0, 0);
            this.slot = p_i46313_2_;
        }

        public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
            this.slot.onPickupFromSlot(playerIn, stack);
        }

        public boolean isItemValid(ItemStack stack) {
            return this.slot.isItemValid(stack);
        }

        public ItemStack getStack() {
            return this.slot.getStack();
        }

        public boolean getHasStack() {
            return this.slot.getHasStack();
        }

        public void putStack(ItemStack stack) {
            this.slot.putStack(stack);
        }

        public void onSlotChanged() {
            this.slot.onSlotChanged();
        }

        public int getSlotStackLimit() {
            return this.slot.getSlotStackLimit();
        }

        public int getItemStackLimit(ItemStack stack) {
            return this.slot.getItemStackLimit(stack);
        }

        public String getSlotTexture() {
            return this.slot.getSlotTexture();
        }

        public ItemStack decrStackSize(int amount) {
            return this.slot.decrStackSize(amount);
        }

        public boolean isHere(IInventory inv, int slotIn) {
            return this.slot.isHere(inv, slotIn);
        }
    }

}