package ac.artemis.core.v5.config.impl;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.config.Configuration;
import ac.artemis.core.v5.config.Config;
import ac.artemis.core.v5.config.ConfigField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredictionConfiguration extends Config {

    @ConfigField("version")
    private String version = "V2";

    @ConfigField("skip-tick")
    private boolean skipTick = true;

    public PredictionConfiguration(final Configuration configuration) {
        super(configuration);
    }

    public PredictionConfiguration(final String name) {
        super(Server.v().getConfig(name));
    }
}
