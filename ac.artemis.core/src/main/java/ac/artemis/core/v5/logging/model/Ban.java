package ac.artemis.core.v5.logging.model;

import ac.artemis.anticheat.api.alert.Punishment;
import ac.artemis.core.v5.utils.rand.RandomString;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.UUID;

@Getter
@Setter
public class Ban implements Punishment {
    @SerializedName("username")
    private String username;

    @SerializedName("uuid")
    private UUID uuid;

    @SerializedName("id")
    private String banId;

    @SerializedName("timestamp")
    private long timestamp;

    private transient boolean cancelled;

    public Ban(final String username, final UUID uuid, final long timestamp) {
        this.username = username;
        this.uuid = uuid;
        this.banId = randomBanId();
        this.timestamp = timestamp;
        this.cancelled = false;
    }

    // Format of XXXX-XXXXXX-XXX
    public static String randomBanId() {
        final StringBuilder builder = new StringBuilder();

        builder.append(Calendar.getInstance().getTime().getYear());
        builder.append("-");
        builder.append(new RandomString(6).nextString());
        builder.append("-");
        builder.append(new RandomString(3).nextString());

        return builder.toString();
    }
}
