package ac.artemis.core.v4.check.naming;

import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.utils.chat.Chat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

/**
 * @author Ghast
 * @since 21/10/2020
 * Artemis © 2020
 */

@AllArgsConstructor
@Getter
public enum NamingScheme {
    DEFAULT(check -> check.getType().name() + " " + check.getVar()),
    ANTIVIRUS(check -> Chat.firstCap(check.getType().getCategory().name()) + ":" + check.getType().name() + "/"
            + check.getVar() + "." + check.getMaxVb() + "!" + Chat.firstCap(check.getStage().name()))
    ;

    private final Function<CheckInformation, String> naming;
}
