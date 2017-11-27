package utils;

/**
 * This timer class allows time measurement.
 */
public class Timer {

    private long timeStart;

    /**
     * Creates a timer object and starts it.
     */
    public Timer(){
        timeStart = System.currentTimeMillis();
    }

    /**
     * Starts the timer.
     */
    public void start(){
        timeStart = System.currentTimeMillis();
    }

    /**
     * Returns the time passed since the start method was called.
     **/
    public double stop() {
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - timeStart;
        return tDelta / 1000.0;
    }
}
