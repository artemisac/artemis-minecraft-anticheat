package ac.artemis.core.v4.data.holders;

import ac.artemis.core.v4.check.data.MouseFilter;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.lists.EvictingArrayList;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;

@Getter
@Setter
public class SensitivityHolder extends AbstractHolder {

    public SensitivityHolder(final PlayerData data) {
        super(data);
    }

    public final EvictingArrayList<Double> samplesYaw = new EvictingArrayList<>(50);
    public final EvictingArrayList<Double> samplesPitch = new EvictingArrayList<>(50);

    public final ArrayDeque<Integer> integerSensitivitySamples = new ArrayDeque<>();

    public final MouseFilter mouseFilterXAxis = new MouseFilter();
    public final MouseFilter mouseFilterYAxis = new MouseFilter();

    public float smoothCamFilterX = 0.0f, smoothCamFilterY = 0.0f;

    public float cinematicYaw = 0.0f, cinematicPitch = 0.0f;
    public float smoothCamYaw = 0.0f, smoothCamPitch = 0.0f;

    public double[] gridYaw = new double[40];
    public double[] gridPitch = new double[40];
    public double[] gridComputed = new double[40];

    public double currentDivisorYaw = 0.0;
    public double currentDivisorPitch = 0.0;
    public double currentDivisorComputed = 0.0;

    public double modeYaw = Double.MIN_VALUE;
    public double modePitch = Double.MIN_VALUE;
    public double modeComputed = Double.MIN_VALUE;

    public double sensitivity = 0.0, sensitivityX = 0.0, sensitivityY = 0.0, sensitivityXY = 0.0;
    public double formatX = 0.0, formatY = 0.0;

    public int inverseYaw = 1, inversePitch = 1;

    public double computedX = 0.0f, computedY = 0.0f;
    public double deltaX = 0.0, deltaY = 0.0;

    public float predictedYaw = 0.0f, predictedPitch = 0.0f;
    public float distanceYaw = 0.0f, distancePitch = 0.0f;

    public float minimumYaw = 0.0f;
    public float minimumPitch = 0.0f;

    public boolean enclosesYaw = false, enclosesPitch = false;

    public int rotations = 0;
    
    public double lastDeltaX = 0.0;
    public double lastDeltaY = 0.0;

    public boolean encloseX;
    public boolean encloseY;

    public float differenceX = 0.0F;
    public float differenceY = 0.0F;

    public double deltaDifferenceX = 0.0F;
    public double deltaDifferenceY = 0.0F;

    public double derivationX;
    public double derivationY;

    public double sensitivityTableValue = -1;

    public float lastPredictedYaw = 0, lastPredictedPitch = 0;

    public long rate;

    public int integerSensitivity = 0;

    public double getGcdFromTable() {
        final float var132 = (float) sensitivityTableValue * 0.6F + 0.2F;
        final float var141 = var132 * var132 * var132 * 8.0F;

        return var141;
    }

    public double getDerivation(final double dxA, final double dxB, final double xa, final double xb) {
        return Math.abs(dxB - dxA) / Math.abs(xb - xa);
    }

    public double getDerivation(final double dx, final double var) {
        return dx / var;
    }
}
