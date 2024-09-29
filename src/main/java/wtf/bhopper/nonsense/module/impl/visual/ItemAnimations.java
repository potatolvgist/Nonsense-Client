package wtf.bhopper.nonsense.module.impl.visual;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;

public class ItemAnimations extends Module {

    public final GroupSetting swordGroup = new GroupSetting("Sword", "Sword animations", this);
    public final EnumSetting<BlockAnimation> blockAnimation = new EnumSetting<>("Block Animation", "Sword block animation", BlockAnimation.DEFAULT);
    public final BooleanSetting oldTransformations = new BooleanSetting("Old Transform", "Use the 1.7 item transformations", false);
    public final BooleanSetting equipAnimation = new BooleanSetting("Equip Animation", "Equip animation (makes the sword go up and down)", true);

    public ItemAnimations() {
        super("Item Animations", "Modify item animations", Category.VISUAL);
        swordGroup.add(blockAnimation, oldTransformations, equipAnimation);
        this.addSettings(swordGroup);
    }

    public enum BlockAnimation {
        DEFAULT,
        EXHIBITION,
        SWING,
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
