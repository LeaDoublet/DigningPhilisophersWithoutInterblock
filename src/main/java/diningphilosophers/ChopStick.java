package diningphilosophers;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChopStick {
    private final Lock verrou = new ReentrantLock();
    private final Condition pasPrise = verrou.newCondition();
    private static int stickCount = 0;
    private boolean iAmFree = true;
    private final int myNumber;

    public ChopStick() {
        myNumber = ++stickCount;
    }

    public boolean tryTake() throws InterruptedException {
        verrou.lock();
        try {
            while (!iAmFree) {
                pasPrise.await();
            }
            iAmFree = false;
            return true;
        } finally {
            verrou.unlock();
        }
    }

    public void release() {
        verrou.lock();
        try {
            iAmFree = true;
            pasPrise.signalAll();
            System.out.println("Stick " + myNumber + " Released");
        } finally {
            verrou.unlock();
        }
    }

    @Override
    public String toString() {
        return "Stick#" + myNumber;
    }
}