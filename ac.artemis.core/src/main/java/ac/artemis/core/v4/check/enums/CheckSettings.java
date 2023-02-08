package ac.artemis.core.v4.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
@AllArgsConstructor
public enum CheckSettings {
    BASE_SPEED("baseSpeed"),
    MAX_OVER_MOVE("maxOverMove"),
    ICE_SCALAR("iceModifier"),
    TRAP_DOOR_SCALAR("trapdoorModifier"),
    SPEED_SCALAR("speedEffectModifier"),
    UNDERBLOCK_SCALAR("underBlockModifier"),
    BELOW_BLOCK_SCALAR("belowBlockModifier"),
    TOGGLE_FLY_DELAY("toggleFlyDelay"),
    FLY_TICKS_MULTIPLIER("flyTicksMultiplier"),
    VELOCITY_SCALAR("velocityAddedValue"),
    SPRINT_SCALAR("sprintingModifier"),
    STAIR_SCALAR("stairsModifier"),
    MAX_VB("maxVb"),
    MAX_REACH("maxReach"),
    PING_SCALAR("pingModifier"),
    MAX_STREAK("maxStreak"),
    DIGGING_DELAY_IN_SECONDS("digDelayInSeconds"),
    MILLIS_BETWEEN_CLICK_FOR_RESET_FLYING("timeBetweenClicksForResetInMilliseconds.flyPacket"),
    MILLIS_BETWEEN_CLICK_FOR_RESET_ARM("timeBetweenClicksForResetInMilliseconds.armPacket"),
    DELAY_BETWEEN_SIGNS_SECONDS("delayBetweenSignsInSeconds"),
    DELAY_BETWEEN_SIGNS_MS("delayBetweenSignsInMillis"),
    MAX_STREAK_FOR_VL("maxStreakForVl"),
    USE_FIRST_PRESET("usePresetOne"),
    USE_SECOND_PRESET("usePresetTwo"),
    MAX_CLICK_ABNORMAL("maxTicksAbnormal"),
    MAX_CLICK_EXPERIMENTAL("maxTicksExperimental"),
    MAX_CLICK_IMPOSSIBLE("maxTicksImpossible"),
    USE_THIRD_PRESET("usePresetThree"),
    MAX_PITCH_CHANGE("maxPitchChange"),
    MAX_GCD("maxGcd"),
    MAX_RELATIVE_DELTA_PITCH("maxRelativeDeltaPitch"),
    AVERAGE("maxAverage"),
    MAX_MEAN("mean.maximum"),
    MIN_MEAN("mean.minimum"),

    PACKET_PROCESSING_TIME("processingTimeCompensation"),

    MAX_DELTA("maxDelta"),
    MIN_DELTA("minDelta"),
    MAX_VALUE("max"),
    MIN_VALUE("min"),

    MAX_MAX("largest.max"),
    MAX_MIN("largest.min"),
    MIN_MAX("smallest.max"),
    MIN_MIN("smallest.min"),

    MIN_GROUND_DISTANCE("minimum.ground"),
    FLY_TOTAL_THRESHOLD("fly.total"),

    MIN_LEVEL_THRESHOLD("aura.total"),

    MIN_DELTA_ACCELERATION_YAW("acceleration.yaw"),
    MIN_DELTA_ACCELERATION_PITCH("acceleration.pitch"),


    MAX_DELTA_INVALID("invalid.maxDelta"),
    MAX_VALUE_INVALID("invalid.max"),
    MIN_VALUE_INVALID("invalid.min"),

    MAX_MAX_INVALID("invalid.largest.max"),
    MAX_MIN_INVALID("invalid.largest.min"),
    MIN_MAX_INVALID("invalid.smallest.max"),
    MIN_MIN_INVALID("invalid.smallest.min"),

    CINEMATIC_THRESHOLD("cinematic.threshold"),
    CINEMATIC_RATIO("cinematic.ratio"),

    RATIO_COMBINED_THRESHOLD("ratio.threshold"),

    STAIR_SLAB_Y_LIMIT("limit.stairslab"),
    BASE_MAX_VALUE("baseValue"),

    // AUTOCLICKER B

    FIRST_SAMPLE_FLY_SIZE("sample.fly.first.size"),
    SECOND_SAMPLE_FLY_SIZE("sample.fly.first.size"),
    FIRST_SAMPLE_USE_SIZE("sample.use.first.size"),
    SECOND_SAMPLE_USE_SIZE("sample.use.first.size"),

    TICKS_FOR_PAUSE("pause.ticks"),

    // Kurtosis
    MAX_KURTOSIS_FIRST_SAMPLE_FLY("sample.fly.first.kurtosis.max"),
    MIN_KURTOSIS_FIRST_SAMPLE_FLY("sample.fly.first.kurtosis.min"),
    MAX_KURTOSIS_SECOND_SAMPLE_FLY("sample.fly.second.kurtosis.max"),
    MIN_KURTOSIS_SECOND_SAMPLE_FLY("sample.fly.second.kurtosis.min"),
    MAX_KURTOSIS_FIRST_SAMPLE_USE("sample.use.first.kurtosis.max"),
    MIN_KURTOSIS_FIRST_SAMPLE_USE("sample.use.first.kurtosis.min"),
    MAX_KURTOSIS_SECOND_SAMPLE_USE("sample.use.second.kurtosis.max"),
    MIN_KURTOSIS_SECOND_SAMPLE_USE("sample.use.second.kurtosis.min"),

    // Kurtosis delta
    MAX_KURTOSIS_FIRST_SAMPLE_USE_DELTA("sample.use.first.kurtosis.delta.max"),
    MIN_KURTOSIS_FIRST_SAMPLE_USE_DELTA("sample.use.first.kurtosis.delta.min"),

    // Skewness mean
    MAX_SKEWNESS_MEAN_SAMPLE("samples.skewness.mean.max"),
    MIN_SKEWNESS_MEAN_SAMPLE("samples.skewness.mean.min"),

    // Skewness
    MAX_SKEWNESS_FIRST_SAMPLE_FLY("sample.fly.first.skewness.max"),
    MIN_SKEWNESS_FIRST_SAMPLE_FLY("sample.fly.first.skewness.min"),
    MAX_SKEWNESS_SECOND_SAMPLE_FLY("sample.fly.second.skewness.max"),
    MIN_SKEWNESS_SECOND_SAMPLE_FLY("sample.fly.second.skewness.min"),
    MAX_SKEWNESS_FIRST_SAMPLE_USE("sample.use.first.skewness.max"),
    MIN_SKEWNESS_FIRST_SAMPLE_USE("sample.use.first.skewness.min"),
    MAX_SKEWNESS_SECOND_SAMPLE_USE("sample.use.second.skewness.max"),
    MIN_SKEWNESS_SECOND_SAMPLE_USE("sample.use.second.skewness.min"),

    // Variance
    MAX_VARIANCE_FIRST_SAMPLE_FLY("sample.fly.first.variance.max"),
    MIN_VARIANCE_FIRST_SAMPLE_FLY("sample.fly.first.variance.min"),
    MAX_VARIANCE_SECOND_SAMPLE_FLY("sample.fly.second.variance.max"),
    MIN_VARIANCE_SECOND_SAMPLE_FLY("sample.fly.second.variance.min"),
    MAX_VARIANCE_FIRST_SAMPLE_USE("sample.use.first.variance.max"),
    MIN_VARIANCE_FIRST_SAMPLE_USE("sample.use.first.variance.min"),
    MAX_VARIANCE_SECOND_SAMPLE_USE("sample.use.second.variance.max"),
    MIN_VARIANCE_SECOND_SAMPLE_USE("sample.use.second.variance.min"),

    // Swing Rate
    MAX_SWING_RATE_FIRST_SAMPLE_FLY("sample.fly.first.swingrate.max"),
    MIN_SWING_RATE_FIRST_SAMPLE_FLY("sample.fly.first.swingrate.min"),
    MAX_SWING_RATE_SECOND_SAMPLE_FLY("sample.fly.second.swingrate.max"),
    MIN_SWING_RATE_SECOND_SAMPLE_FLY("sample.fly.second.swingrate.min"),
    MAX_SWING_RATE_FIRST_SAMPLE_USE("sample.use.first.swingrate.max"),
    MIN_SWING_RATE_FIRST_SAMPLE_USE("sample.use.first.swingrate.min"),
    MAX_SWING_RATE_SECOND_SAMPLE_USE("sample.use.second.swingrate.max"),
    MIN_SWING_RATE_SECOND_SAMPLE_USE("sample.use.second.swingrate.min"),

    // Swing Rate Average
    MAX_SWING_RATE_AVERAGE("samples.swingrate.average.max"),
    MIN_SWING_RATE_AVERAGE("samples.swingrate.average.min"),

    // Swing Rate Delta
    MAX_SWING_RATE_DELTA_USE("sample.use.swingrate.delta.max"),
    MIN_SWING_RATE_DELTA_USE("sample.use.swingrate.delta.min"),

    // Range
    MAX_RANGE("range"),

    MAX_OVERFRICTION_COMPENSATION("maxSpeedCompensation");

    @Getter
    private final String friendlyName;
}
