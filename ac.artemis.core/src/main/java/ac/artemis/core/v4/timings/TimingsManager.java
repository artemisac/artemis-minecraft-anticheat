package ac.artemis.core.v4.timings;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v5.threading.Threading;
import lombok.SneakyThrows;

import java.util.concurrent.ExecutorService;

/**
 * @author Ghast
 * @since 24-Apr-20
 */
public class TimingsManager extends Manager {
    private ArtemisTiming joinTiming;
    private ExecutorService service;
    private long[] oldTicks;

    public TimingsManager(final Artemis plugin) {
        super(plugin, "Timings [Manager]");
    }

    @Override
    public void init(final InitializeAction initializeAction) {
        this.joinTiming = new ArtemisTiming();
        this.service = Threading.getOrStartService("artemis-timings-service");
    }

    @Override
    public void disinit(final ShutdownAction shutdownAction) {
        this.joinTiming = null;
        this.service.shutdown();
        this.service = null;
    }

    
    public double getAverageTimePacket() {
        return this.getAverageTimePacketExecutor();
    }

    
    public double getAverageTimeHandler() {
        return this.getAverageTimeHandlerExecutor();
    }

    /**
     * Laggy as fuck
     * @return Returns the Average packet execution time
     */
    private double getAverageTimePacketExecutor() {
        //List<Long> times = new ArrayList<>();
        final double[] sum = {0};
        final int[] quant = {0};
        plugin.getApi().getPlayerDataManager().getPlayerDataMap().forEach((x, data) -> {
            sum[0] += data.getX().timing.packetTiming.getAverage();
            quant[0]++;
        });

        final double average = sum[0] / (double) quant[0];
        //
        return average;
    }

    private double getAverageTimeHandlerExecutor() {
        //List<Long> times = new ArrayList<>();
        final double[] sum = {0};
        final int[] quant = {0};
        plugin.getApi().getPlayerDataManager().getPlayerDataMap().forEach((x, data) -> {
            //times.addAll(processor.getX().timing.packetTiming.getTimes());
            sum[0] += data.getX().timing.handlerPreTiming.getAverage();
            quant[0]++;
        });

        final double average = sum[0] / (double) quant[0];
        return average;
    }

    public ArtemisTiming getJoinTiming() {
        return joinTiming;
    }
}
