package ac.artemis.anticheat.engine.v2.jump;

import lombok.AllArgsConstructor;
import lombok.Data;
import ac.artemis.core.v5.emulator.modal.Motion;

@AllArgsConstructor
@Data
public class JumpResult {
    private final Motion motion;
}
