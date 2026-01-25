package org.bxwbb.spigotplugincreatertool.WindowLabel;

import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;

public class GroupBox extends BaseLabel {

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        for (BaseLabel child : this.children) {
            child.setDisplayVisible(visible);
        }
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        return new GroupBox();
    }

    @Override
    public void update() {

    }
}
