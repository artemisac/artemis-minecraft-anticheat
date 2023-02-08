package ac.artemis.core.v5.threading;

public class ThreadedExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        System.err.println("[Artemis Exception Handler] Internal exception on thread " + t.getName()
                + " ("
                + "priority: " + t.getPriority() + " "
                + "status: " + t.getState() + " "
                + "id: " + t.getId() + " "
                + ") "
        );

        e.printStackTrace();
    }
}
