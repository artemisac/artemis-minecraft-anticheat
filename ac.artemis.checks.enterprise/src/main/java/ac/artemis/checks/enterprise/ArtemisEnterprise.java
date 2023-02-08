package ac.artemis.checks.enterprise;

import ac.artemis.checks.enterprise.aim.*;
import ac.artemis.checks.enterprise.aura.AuraDynamic;
import ac.artemis.checks.enterprise.emulator.*;
import ac.artemis.checks.enterprise.heuristics.HeuristicsI;
import ac.artemis.checks.enterprise.heuristics.HeuristicsII;
import ac.artemis.checks.enterprise.heuristics.HeuristicsIII;
import ac.artemis.checks.enterprise.heuristics.HeuristicsIV;
import ac.artemis.checks.enterprise.protocol.*;
import ac.artemis.checks.enterprise.spoof.PingSpoofProtocol;
import ac.artemis.checks.enterprise.timer.TimerBalance;
import ac.artemis.checks.enterprise.timer.TimerGladUrBad;
import ac.artemis.checks.enterprise.timer.TimerReverse;
import ac.artemis.checks.enterprise.velocity.VelocityXYZ;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.manager.AbstractCheckManager;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * @author Ghast
 * @since 19/11/2020
 * Artemis © 2020
 */
public class ArtemisEnterprise extends AbstractCheckManager {
    public ArtemisEnterprise(PlayerData data) {
        super(data);
    }

    /*
       ______                       __               _
     .' ___  |                     [  |             / |_
    / .'   \_|  .--.   _ .--..--.   | |.--.   ,--. `| |-'
    | |       / .'`\ \[ `.-. .-. |  | '/'`\ \`'_\ : | |
    \ `.___.'\| \__. | | | | | | |  |  \__/ |// | |,| |,
     `.____ .' '.__.' [___||__||__][__;.__.' \'-;__/\__/

     */

    /**
     * Aim checks based on heuristics
     */
    private static final CheckInformation AIM_PREDICTION = new CheckInformation(AimGcd.class);
    private static final CheckInformation AIM_INVALID_SENS = new CheckInformation(Aim1337.class);
    private static final CheckInformation AIM_ELEVATED_GAY = new CheckInformation(AimPhoenix.class);
    private static final CheckInformation AIM_ELEVATED_GAY2 = new CheckInformation(AimPenguin.class);
    private static final CheckInformation AIM_STATS = new CheckInformation(AimStats.class);
    private static final CheckInformation AIM_RATE = new CheckInformation(AimRate.class);
    private static final CheckInformation AURA_DYNAMIC = new CheckInformation(AuraDynamic.class);

     /*
     ____    ____                                                    _
    |_   \  /   _|                                                  / |_
      |   \/   |   .--.   _   __  .---.  _ .--..--.  .---.  _ .--. `| |-'
      | |\  /| | / .'`\ \[ \ [  ]/ /__\\[ `.-. .-. |/ /__\\[ `.-. | | |
     _| |_\/_| |_| \__. | \ \/ / | \__., | | | | | || \__., | | | | | |,
    |_____||_____|'.__.'   \__/   '.__.'[___||__||__]'.__.'[___||__]\__/

     */

    /**
     * Prediction checks based on NMS
     */
    //private static final CheckInformation PREDICTION_SPEED = new CheckInformation(PredictionSpeed.class);
    private static final CheckInformation PREDICTION_DOGE = new CheckInformation(PredictionDoge.class);
    private static final CheckInformation PREDICTION_JUMP = new CheckInformation(PredictionJump.class);
    private static final CheckInformation PREDICTION_VOLATILE = new CheckInformation(PredictionVolatile.class);
    private static final CheckInformation PREDICTION_INVENTORY = new CheckInformation(PredictionInventory.class);
    private static final CheckInformation PREDICTION_STRAFE = new CheckInformation(PredictionStrafe.class);
    private static final CheckInformation PREDICTION_BUFFERLESS = new CheckInformation(PredictionBufferless.class);
    public static final CheckInformation PREDICTION_SOLENCE = new CheckInformation(PredictionSolence.class);


    /**
     * Velocity checks based on heuristic and physics approach
     */
    private static final CheckInformation VELOCITY_XYZ = new CheckInformation(VelocityXYZ.class);



     /*
     ____    ____   _
    |_   \  /   _| (_)
      |   \/   |   __   .--.   .---.
      | |\  /| |  [  | ( (`\] / /'`\]
     _| |_\/_| |_  | |  `'.'. | \__.
    |_____||_____|[___][\__) )'.___.'

     */

    /**
     * Timer checks based on statistical analysis
     */
    private static final CheckInformation TIMER_REVERSE = new CheckInformation(TimerReverse.class);
    private static final CheckInformation TIMER_BAD = new CheckInformation(TimerGladUrBad.class);
    private static final CheckInformation TIMER_WARDEN = new CheckInformation(TimerBalance.class);

    /**
     * Bad Packet checks renamed with a much more efficient code flow
     */
    private static final CheckInformation PROTOCOL_A = new CheckInformation(ProtocolA.class);
    private static final CheckInformation PROTOCOL_B = new CheckInformation(ProtocolB.class);
    private static final CheckInformation PROTOCOL_C = new CheckInformation(ProtocolC.class);
    private static final CheckInformation PROTOCOL_D = new CheckInformation(ProtocolD.class);
    private static final CheckInformation PROTOCOL_D3 = new CheckInformation(ProtocolD3.class);

    /**
     * Spoof packet checks
     */
    private static final CheckInformation SPOOF_PROTOCOL = new CheckInformation(PingSpoofProtocol.class);

    /**
     * Various heuristic based checks
     */
    private static final CheckInformation HEURISTICS_I = new CheckInformation(HeuristicsI.class);
    private static final CheckInformation HEURISTICS_II = new CheckInformation(HeuristicsII.class);
    private static final CheckInformation HEURISTICS_III = new CheckInformation(HeuristicsIII.class);
    private static final CheckInformation HEURISTICS_IV = new CheckInformation(HeuristicsIV.class);

    @Override
    public void initChecks() {
        checks = new ImmutableClassToInstanceMap.Builder<ArtemisCheck>()
                // Insert checks here

                /* Combat */
                // -> Aim
                .put(AimGcd.class, new AimGcd(this.getData(), AIM_PREDICTION))
                .put(Aim1337.class, new Aim1337(this.getData(), AIM_INVALID_SENS))
                .put(AimPhoenix.class, new AimPhoenix(this.getData(), AIM_ELEVATED_GAY))
                .put(AimPenguin.class, new AimPenguin(this.getData(), AIM_ELEVATED_GAY2))
                .put(AimStats.class, new AimStats(this.getData(), AIM_STATS))
                .put(AimRate.class, new AimRate(this.getData(), AIM_RATE))

                .put(AuraDynamic.class, new AuraDynamic(this.getData(), AURA_DYNAMIC))


                /* Movement */
                // -> Prediction
                //.put(PredictionSpeed.class, new PredictionSpeed(this.getData(), PREDICTION_SPEED))
                .put(PredictionDoge.class, new PredictionDoge(this.getData(), PREDICTION_DOGE))
                .put(PredictionStrafe.class, new PredictionStrafe(this.getData(), PREDICTION_STRAFE))
                .put(PredictionJump.class, new PredictionJump(this.getData(), PREDICTION_JUMP))
                .put(PredictionVolatile.class, new PredictionVolatile(this.getData(), PREDICTION_VOLATILE))
                .put(PredictionInventory.class, new PredictionInventory(this.getData(), PREDICTION_INVENTORY))
                .put(PredictionBufferless.class, new PredictionBufferless(this.getData(), PREDICTION_BUFFERLESS))
                .put(PredictionSolence.class, new PredictionSolence(this.getData(), PREDICTION_SOLENCE))

                // -> Velocity
                .put(VelocityXYZ.class, new VelocityXYZ(this.getData(), VELOCITY_XYZ))

                // -> Timer
                .put(TimerReverse.class, new TimerReverse(this.getData(), TIMER_REVERSE))
                .put(TimerGladUrBad.class, new TimerGladUrBad(this.getData(), TIMER_BAD))
                .put(TimerBalance.class, new TimerBalance(this.getData(), TIMER_WARDEN))

                .put(ProtocolD.class, new ProtocolD(this.getData(), PROTOCOL_D))
                .put(ProtocolD3.class, new ProtocolD3(this.getData(), PROTOCOL_D3))

                // Spoof
                .put(PingSpoofProtocol.class, new PingSpoofProtocol(this.getData(), SPOOF_PROTOCOL))

                // Heuristic
                .put(HeuristicsI.class, new HeuristicsI(this.getData(), HEURISTICS_I))
                .put(HeuristicsII.class, new HeuristicsII(this.getData(), HEURISTICS_II))
                .put(HeuristicsIII.class, new HeuristicsIII(this.getData(), HEURISTICS_III))
                .put(HeuristicsIV.class, new HeuristicsIV(this.getData(), HEURISTICS_IV))

                // Final build
                .build();
    }
}
