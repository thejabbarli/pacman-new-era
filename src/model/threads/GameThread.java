package model.threads;

public abstract class GameThread extends Thread {
    protected volatile boolean running;
    protected volatile boolean paused;
    protected final Object pauseLock = new Object();

    public GameThread() {
        this.running = true;
        this.paused = false;
    }

    public void stopThread() {
        running = false;
        resumeThread(); // In case thread is paused
    }

    public void pauseThread() {
        paused = true;
    }

    public void resumeThread() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void run() {
        while (running) {
            // Check if thread should be paused
            synchronized (pauseLock) {
                if (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            // If still running, perform the thread-specific action
            if (running) {
                doAction();
            }
        }
    }

    // Abstract method to be implemented by subclasses
    protected abstract void doAction();
}