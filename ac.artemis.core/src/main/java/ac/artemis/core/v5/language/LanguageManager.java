package ac.artemis.core.v5.language;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v5.utils.ClassUtil;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.*;

public class LanguageManager extends Manager {
    public LanguageManager(final Artemis plugin) {
        super(plugin, "Language [Manager]");
    }

    private final Map<String, Field> fieldMap = new HashMap<>();

    @Override
    public void init(final InitializeAction action) {
        final String language = ConfigManager.getSettings().getStringOrDefault("general.language", "en_US");
        try {
            for (final Field declaredField : Lang.class.getDeclaredFields()) {
                declaredField.setAccessible(true);

                if (!declaredField.isAnnotationPresent(LanguageValue.class))
                    continue;

                if (!declaredField.getType().equals(String.class))
                    continue;

                final LanguageValue languageValue = declaredField.getAnnotation(LanguageValue.class);
                fieldMap.put(languageValue.value(), declaredField);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        this.setLanguage(Languages.valueOf(language.toUpperCase(Locale.US)));
    }

    @Override
    public void disinit(final ShutdownAction action) {
        this.fieldMap.clear();
    }

    @SneakyThrows
    public void setLanguage(final Languages language) {
        final Locale locale = language.getLocale();
        final ResourceBundle resourceBundle = new PropertyResourceBundle(ClassUtil.getFileFromLoader(
                this.getClass(), "lang/messages_" + locale.getLanguage() + "_" + locale.getCountry() + ".properties"));

        fieldMap.forEach((s, field) -> {
            try {
                field.set(null, resourceBundle.getString(s));
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

}
