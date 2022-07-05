package util;

public class Timer {
    private long startTime;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop(String activity) {
        long endTime = System.currentTimeMillis();
        System.out.println(activity + " took " + (endTime - startTime) + " ms.");
    }

    public void stopStart(String activity) {
        stop(activity);
        start();
    }
}
