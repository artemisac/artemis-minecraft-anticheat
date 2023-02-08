package ac.artemis.anticheat.engine.v1;

import ac.artemis.core.inject.Injectable;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.emulator.Emulator;
import ac.artemis.core.v5.emulator.EmulatorManager;

public class EngineInjector implements Injectable {
    @Override
    public void begin() {
        Chat.sendConsoleMessage("&r[&a✓&r] &aHooking into Predictions 1.0!");
        Artemis.v().getApi().getEmulatorManager().inject(
                new EmulatorManager.EmulatorProvider()
                        .setNames("v1", "version1", "legacy", "original")
                        .setFactory(new EmulatorManager.EmulatorFactory() {
                            @Override
                            public Emulator build() {
                                assert data != null : "PlayerData cannot be null! (0x03-A)";

                                switch (data.getVersion()) {
                                    default:
                                        return new BntityPlayerXYZ(data);
                                    case V1_12:
                                    case V1_12_1:
                                    case V1_12_2:
                                        return new BntityPlayerXYZ_1_12(data);
                                    case V1_13:
                                    case V1_13_1:
                                    case V1_13_2:
                                    case V1_14:
                                    case V1_14_1:
                                    case V1_14_2:
                                    case V1_14_3:
                                    case V1_14_4:
                                    case V1_15:
                                    case V1_15_1:
                                    case V1_15_2:
                                    case V1_16:
                                    case V1_16_1:
                                    case V1_16_2:
                                    case V1_16_3:
                                    case V1_16_5:
                                        return new BntityPlayerXYZ_1_15(data);
                                }
                            }
                        })
        );
    }

    @Override
    public void end() {

    }
}
