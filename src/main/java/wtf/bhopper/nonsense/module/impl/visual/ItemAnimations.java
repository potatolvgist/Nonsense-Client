package wtf.bhopper.nonsense.module.impl.visual;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.*;

public class ItemAnimations extends Module {

    public final GroupSetting swordGroup = new GroupSetting("Sword", "Sword animations", this);
    public final EnumSetting<BlockAnimation> blockAnimation = new EnumSetting<>("Block Animation", "Sword block animation", BlockAnimation.DEFAULT);
    public final BooleanSetting oldTransformations = new BooleanSetting("Old Transform", "Uses the 1.7 item transformations", false);
    public final BooleanSetting equipAnimation = new BooleanSetting("Equip Animation", "Equip animation (makes the sword go up and down)", true);

    public final GroupSetting transformGroup = new GroupSetting("Transform", "Item transformations", this);
    public final FloatSetting tranformX = new FloatSetting("X", "X offset", -2.0F, 2.0F, 0.0F);
    public final FloatSetting tranformY = new FloatSetting("Y", "X offset", -2.0F, 2.0F, 0.0F);
    public final FloatSetting tranformZ = new FloatSetting("Z", "X offset", -2.0F, 2.0F, 0.0F);
    public final IntSetting swingSpeed = new IntSetting("Swing Speed", "Swing speed", 1, 15, 6);
    public final FloatSetting useX = new FloatSetting("Use X", "X offset", -2.0F, 2.0F, 0.0F);
    public final FloatSetting useY = new FloatSetting("Use Y", "X offset", -2.0F, 2.0F, 0.0F);
    public final FloatSetting useZ = new FloatSetting("Use Z", "X offset", -2.0F, 2.0F, 0.0F);
    public final IntSetting useSpeed = new IntSetting("Use Swing Speed", "Swing speed", 1, 15, 6);

    public ItemAnimations() {
        super("Item Animations", "Modify item animations", Category.VISUAL);
        swordGroup.add(blockAnimation, oldTransformations, equipAnimation);
        this.transformGroup.add(tranformX, tranformY, tranformZ, swingSpeed, useX, useY, useZ, useSpeed);
        this.addSettings(swordGroup);
    }

    public enum BlockAnimation {
        DEFAULT,
        OLD,
        EXHIBITION,
        SWANK,
        SWANG,
        SWONG,
        TAP,
        CHIP,
        SMOOTH,
        PUNCH,
        BUTTER,
        DORT,
        DOWN,
        SLIDE,
        SIGMA,
        LEAKED,
        REACTOR,
        NONE
    }

}
