package diningphilosophers;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

// Classe "active", sous-classe de Thread
public class Philosopher
        extends Thread {

    private static int seed = 1;
    // Pour tirer au sort le temps nécessaire pour manger ou penser.
    private final Random myRandom = new Random(System.currentTimeMillis() + seed++);
    private final static int DELAY = 1000;
    // Les deus baguettes du philosopehe
    private final ChopStick myLeftStick;
    private final ChopStick myRightStick;
    private boolean running = true;

    private final Lock tableLock = new ReentrantLock(); //verrou global anti interblockage


    public Philosopher(String name, ChopStick left, ChopStick right) {
        super(name);
        this.myLeftStick = right;
        this.myRightStick = left;
    }

    @Override
    public void run() {
        while (running) {
            try {
                think();
                tableLock.lock();
                try {
                    myLeftStick.take();
                    think(); // Pour augmenter la probabilité d'interblocage
                    myRightStick.take();
                } finally {
                    tableLock.unlock();
                }

                // success : process
                eat();
                // release resources
                myLeftStick.release();
                myRightStick.release();
                // try again
            } catch (InterruptedException ex) {
                Logger.getLogger("Table").log(Level.SEVERE, "{0} Interrupted", this.getName());
            }
        }
    }

    // Permet d'interrompre le philosophe "proprement" :
    // Il relachera ses baguettes avant de s'arrêter
    public void leaveTable() {
        running = false;
    }

    private void think() throws InterruptedException {
        int delay = myRandom.nextInt(500 + DELAY);
        System.out.println(this.getName() + " Starts Thinking for: " + delay + " ms");
        sleep(delay); // Le thread peut être interrompu ici
        System.out.println(this.getName() + " Stops Thinking");
    }

    private void eat() throws InterruptedException {
        int delay = myRandom.nextInt(100 + DELAY);
        System.out.println(this.getName() + " Starts Eating for:" + delay + " ms");
        sleep(delay); // Le thread peut être interrompu ici
        System.out.println(this.getName() + " Stops Eating");
    }
}
