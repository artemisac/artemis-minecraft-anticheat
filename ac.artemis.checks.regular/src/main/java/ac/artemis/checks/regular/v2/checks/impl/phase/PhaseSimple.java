package ac.artemis.checks.regular.v2.checks.impl.phase;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.NMS;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.SimplePositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.blocks.BlockUtil;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.SimplePosition;
import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.potion.Effect;

import java.util.ArrayList;
import java.util.List;

@Check(type = Type.PHASE, var = "Simple", threshold = 5, bannable = false)
@NMS
public class PhaseSimple extends SimplePositionCheck {

    private int stageMineplex;
    private int stageRewinside;
    private List<Material> vB = new ArrayList<>();

    public PhaseSimple(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(final SimplePosition from, final SimplePosition to) {
        final Player player = data.getPlayer();

        final boolean exempt = this.isExempt(
                ExemptType.JOIN,
                ExemptType.LIQUID,
                ExemptType.GAMEMODE
        );

        if (data.user.isLagging() || exempt) {
            return;
        }

        final double deltaH = ac.artemis.core.v5.utils.MathUtil.hypot(from.getX() - to.getX(), from.getZ() - to.getZ());
        final double length = MathUtil.doubleDecimal(deltaH, 2);

        double speed = 0;
        double speedLenght = 0;

        /*
                   CAN't REACH DESTINATION
         */
        if (to.getX() == from.getX() && to.getZ() == from.getZ()) {
            vB = BlockUtil.getVerticalBlocks(from, to, player.getWorld());
            for (final Material m : vB) {
                if (m.isSolid() && !BlockUtil.isPhaseSimple(NMSMaterial.matchXMaterial(m))) {
                    log("PHASED: " + m.name());
                    break;
                }
            }
        }

        /*
                    BAD MOVEMENT
         */
        if (String.format("%s", length).contains("E-5")) {
            log("D: " + length);
        }

        /*
                    MINEPLEX
         */
        for (final Effect effects : player.getActivePotionEffects()) {
            if (effects.getType() == PotionEffectType.SPEED) {
                speed = effects.getAmplifier() + 1;
                break;
            }
        }
        final double stageMultiplier;
        if (data.getMovement().isSprinting()) {
            stageMultiplier = 0.0255;
            if (speed > 0) {
                speedLenght += 0.0345;
                speed -= 1;
            }
            speedLenght += 0.25 + (stageMultiplier * speed);
        } else if (player.isSneaking()) {
            stageMultiplier = 0.004;
            if (speed > 0) {
                speedLenght += 0.0059;
                speed -= 1;
            }
            speedLenght += 0.06 + (stageMultiplier * speed);
        } else {
            stageMultiplier = 0.0196;
            speedLenght += 0.2 + (stageMultiplier * speed);
        }

        if (stageMineplex == 1 && length == MathUtil.doubleDecimal(speedLenght, 2)) {
            log("M");
            stageMineplex = 0;
        } else {
            stageMineplex = 0;
        }

        if (length == 1.60) {
            stageMineplex++;
        }

        /*
                  REWINSIDE
         */
        if (length == 0.1274 && stageRewinside == 0) {
            stageRewinside++;
        } else if (length == 0.0255 && stageRewinside == 1) {
            stageRewinside++;
        } else if (length == 0.0741 && stageRewinside == 2) {
            stageRewinside = 0;
            log("R");
        } else {
            stageRewinside = 0;
        }
    }
}
