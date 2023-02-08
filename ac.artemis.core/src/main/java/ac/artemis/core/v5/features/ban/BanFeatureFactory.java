package ac.artemis.core.v5.features.ban;

import ac.artemis.core.v5.utils.interf.Factory;

import java.util.HashMap;
import java.util.Map;

public class BanFeatureFactory implements Factory<BanType> {
    private static Map<String, BanType> banTypeMap;

    private String name;

    public BanFeatureFactory setName(String var) {
        this.name = var;
        return this;
    }

    @Override
    public BanType build() {
        if (banTypeMap == null) {
            banTypeMap = new HashMap<>();

            for (BanType value : BanType.values()) {
                for (String alias : value.getAliases()) {
                    banTypeMap.put(alias, value);
                }
            }
        }

        return banTypeMap.get(name);
    }
}
