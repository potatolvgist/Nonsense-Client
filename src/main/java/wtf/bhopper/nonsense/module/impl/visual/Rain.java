package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3i;
import wtf.bhopper.nonsense.module.Module;

import java.util.ArrayList;
import java.util.List;

public class Rain extends Module {

    private final List<Vec3i> nodes = new ArrayList<>();

    public Rain() {
        super("Rain", "lol", Category.VISUAL);
    }

    enum Texture {
        DOXBIN("nonsense/funny/doxbin.png"),
        RHUNE("nonsense/funny/rhune.png");

        public final ResourceLocation location;

        Texture(String location) {
            this.location = new ResourceLocation(location);
        }
    }

}
