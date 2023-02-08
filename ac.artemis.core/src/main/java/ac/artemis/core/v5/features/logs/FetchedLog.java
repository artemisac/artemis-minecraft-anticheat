package ac.artemis.core.v5.features.logs;

import ac.artemis.anticheat.api.check.CheckInfo;
import lombok.Data;

@Data
public class FetchedLog {
    private final CheckInfo check;
    private final long count;

    public FetchedLog(CheckInfo check, long count) {
        this.check = check;
        this.count = count;
    }
}
