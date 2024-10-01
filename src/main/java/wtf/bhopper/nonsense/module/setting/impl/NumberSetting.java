package wtf.bhopper.nonsense.module.setting.impl;

import wtf.bhopper.nonsense.module.setting.Setting;

public abstract class NumberSetting<T extends Number> extends Setting<T> {

    public NumberSetting(String name, String description) {
        super(name, description);
    }

    public abstract float getF();
    public abstract void setF(float f);
    public abstract float minF();
    public abstract float maxF();


}
