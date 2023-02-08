package ac.artemis.core.v4.data.holders;

import ac.artemis.core.v4.timings.ArtemisTiming;
import ac.artemis.core.v4.data.PlayerData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimingHolder extends AbstractHolder {
    public TimingHolder(PlayerData data) {
        super(data);
    }

    public final ArtemisTiming packetTiming = new ArtemisTiming();
    public final ArtemisTiming handlerPreTiming = new ArtemisTiming();
    public final ArtemisTiming packetConstructTiming = new ArtemisTiming();

    // Individual handlers:
    public final ArtemisTiming
            connectionTiming = new ArtemisTiming(),
            interactTiming = new ArtemisTiming(),
            miscTiming = new ArtemisTiming(),
            movementTiming = new ArtemisTiming();
}
