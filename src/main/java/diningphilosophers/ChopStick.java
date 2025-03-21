package diningphilosophers;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChopStick {

    private static int stickCount = 0;

    private final int myNumber;

    private final Lock verrou = new ReentrantLock();

    private final Condition libre = verrou.newCondition();

    private boolean isFree = true;

    public ChopStick() {
        myNumber = ++stickCount;
    }

    public void take() throws InterruptedException {
        verrou.lock();
        try {
            while (!isFree) {
                libre.await();
            }
            isFree = false;
        }finally{
        verrou.unlock();
    }

}

    public void release() {
        verrou.lock();
        try{
            isFree = true;
            libre.signalAll();
        } finally {
            verrou.unlock();
        }
    }

    @Override
    public String toString() {
        return "Stick#" + myNumber;
    }
}
