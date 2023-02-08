package ac.artemis.core.v4.data.utils;

import ac.artemis.anticheat.api.alert.Severity;
import lombok.Getter;

/**
 * @author Ghast
 * @since 12-Oct-19
 * Ghast CC © 2019
 */
public class StaffEnums {
    public enum StaffAlerts {
        EXPERIMENTAL_VERBOSE(4),
        VERBOSE_SELF(3),
        VERBOSE(2),
        ALERTS(1),
        NONE(0);

        @Getter
        private int importance;

        StaffAlerts(int importance) {
            this.importance = importance;
        }

        public boolean isHighEnough(int i) {
            return i <= importance;
        }

        public boolean isHighEnough(StaffAlerts i) {
            return i.getImportance() <= importance;
        }

        public boolean isHighEnough(Severity i) {
            StaffAlerts i2 = StaffAlerts.EXPERIMENTAL_VERBOSE;

            switch (i) {
                case NONE: i2 = StaffAlerts.NONE; break;
                case VERBOSE: i2 = StaffAlerts.VERBOSE; break;
                case VIOLATION: i2 = StaffAlerts.ALERTS; break;
            }

            return i2.getImportance() <= importance;
        }
    }
}
