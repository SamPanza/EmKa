package su.ptx.emka.junit.alien;

final class U {
    private U() {
    }

    static void startThreadAndWaitFor(Runnable task, long millis) throws InterruptedException {
        var t = new Thread(task);
        t.start();
        t.join(millis);
    }
}
