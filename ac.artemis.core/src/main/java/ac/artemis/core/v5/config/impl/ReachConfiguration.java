package ac.artemis.core.v5.config.impl;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.config.Configuration;
import ac.artemis.core.v5.config.Config;
import ac.artemis.core.v5.config.ConfigField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReachConfiguration extends Config {

    @ConfigField("version")
    private String version = "V1";

    @ConfigField("v1.death-mode")
    private boolean v1_deathMode = false;

    @ConfigField("v1.precision")
    private int v1_precision = 2;

    @ConfigField("v1.max-branch")
    private int v1_maxBranch = 4000;

    public ReachConfiguration(final Configuration configuration) {
        super(configuration);
    }

    public ReachConfiguration(final String name) {
        super(Server.v().getConfig(name));
    }
}
