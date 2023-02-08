package ac.artemis.core.v4.utils.random;

import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.http.HTTPRequest;
import ac.artemis.core.v4.utils.item.CheckRequest;
import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author Ghast
 * @since 15/07/2020
 * Ghast Holdings LLC / Artemis © 2020
 */
public class SafeRandom {
    private final Random random = new Random(System.currentTimeMillis());
    private final List<Integer> randomPast = new ArrayList<>();

    public int generateRandom(int bound){
        Integer var = null;

        while (var == null){
            var = random.nextInt(bound);
            if (randomPast.contains(var)) var = null;
        }

        this.randomPast.add(var);
        return var;
    }

    public void expire(int i){
        randomPast.remove(i);
    }
}
