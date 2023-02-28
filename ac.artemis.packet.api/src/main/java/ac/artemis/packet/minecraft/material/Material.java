package ac.artemis.packet.minecraft.material;

import ac.artemis.packet.minecraft.Wrapped;

public interface Material extends Wrapped {
    String name();

    boolean isBlock();

    boolean isOccluding();

    int getMaxDurability();

    boolean isLegacy();

    boolean isAir();

    boolean isEdible();

    boolean isSolid();

    int getId();

    static Material getMaterial(final String name) {
        return instance.getWrapper().getMaterial(name);
    }

    /**
     * Material wrapper for all static calls to the class.
     */
    MaterialInstance instance = new MaterialInstance();
    interface MaterialWrapper {
        Material getMaterial(final String name);
    }

    class MaterialInstance {
        private MaterialWrapper wrapper;

        public MaterialWrapper getWrapper() {
            return wrapper;
        }

        public void setWrapper(MaterialWrapper wrapper) {
            this.wrapper = wrapper;
        }
    }
}
