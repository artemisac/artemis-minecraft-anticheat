package ac.artemis.core.v4.utils.item;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author Ghast
 * @since 25/12/2020
 * Artemis © 2020
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckRequest {
    @SerializedName("sessionId")
    private UUID sessionId;

    @SerializedName("license")
    private String license;
}
