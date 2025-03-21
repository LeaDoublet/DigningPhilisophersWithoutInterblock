package diningphilosophers;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Philosopher
        extends Thread {

    private static int seed = 1;
    private final Random myRandom = new Random(System.currentTimeMillis() + seed++);
    private final static int DELAY = 1000;
    private final String myName;
    private final ChopStick myLeftStick;
    private final ChopStick myRightStick;
    private boolean jeContinue = true;

    public Philosopher(String name, ChopStick left, ChopStick right) {
        myName = name;
        myLeftStick = left;
        myRightStick = right;
    }

    @Override
    public void run() {
        while (jeContinue) {
            try {
                think();
                // 2-Step locking protocol
                // 1st step : try to get resources
                if (tryTakeStick(myLeftStick)) {
                    if (tryTakeStick(myRightStick)) {
                        // success : process
                        eat();
                        // release resources
                        releaseStick(myLeftStick);
                        releaseStick(myRightStick);
                    } else {
                        // failure : release resources
                        releaseStick(myLeftStick);
                    }
                }
                // try again
            } catch (InterruptedException ex) {
                Logger.getLogger("Table").log(Level.SEVERE, "{0} Interrupted", this.getName());
            }
        }
        System.out.println(myName + " leaves table");

    }

    public void leaveTable() {
        jeContinue = false;
    }

    private boolean tryTakeStick(ChopStick stick) throws InterruptedException {
        boolean result = stick.tryTake();
        if (result) {
            System.out.println(myName + " took " + stick );
        } else {
            System.out.println(myName + " could not take " + stick );
        }
        return result;
    }

    private void releaseStick(ChopStick stick) {
        stick.release();
        System.out.println(myName + " releases " + stick);
    }

    private void think() {
        int delay = myRandom.nextInt(100 + DELAY);
        System.out.println(myName + " Starts Thinking for: " + delay + " ms");
        try {
            sleep(delay);
        } catch (InterruptedException ignored) {
        }
        System.out.println(myName + " Stops Thinking");
    }

    private void eat() {
        int delay = myRandom.nextInt(100 + DELAY);
        System.out.println(myName + " Starts Eating for:" + delay + " ms");
        try {
            sleep(delay);
        } catch (InterruptedException ignored) {
        }
        System.out.println(myName + " Stops Eating");
    }
}