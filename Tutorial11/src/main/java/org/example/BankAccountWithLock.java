package org.example;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

public class BankAccountWithLock {
    private double balance;
    private final ReentrantReadWriteLock lock =  new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public BankAccountWithLock(double initialBalance){
        this.balance = initialBalance;
    }

    //Read balance (shared lock)
    public double getBalance(){
        readLock.lock();
        try{
            System.out.println(Thread.currentThread().getName() + " reads balance: " + balance);
            return balance;
        }finally{
            readLock.unlock();
        }
    }

    //Deposit money (exclusive lock)
    public void deposit(double amount){
        writeLock.lock();

        try{
            System.out.println(Thread.currentThread().getName() + " deposits: " + amount);
            balance += amount;
        }finally{
            writeLock.unlock();
        }
    }

    //Withdraw money (exclusive lock)
    public void withdraw(double amount){
        writeLock.lock();

        try{
            if(balance >=amount){
                System.out.println(Thread.currentThread().getName() + " withdraws: " + amount);
                balance -= amount;
            }else{
                System.out.println(Thread.currentThread().getName() + " insufficient funds for: " + amount);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        BankAccountWithLock account = new BankAccountWithLock(1000);

        // Create multiple readers
        for (int i = 1; i <= 3; i++) { // 3 reader threads
            int readerId = i;
            Thread reader = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    account.getBalance();
                    sleep(1000);
                }
            }, "Reader-" + readerId);
            reader.start();
        }

        // Single depositor
        Thread depositor = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                account.deposit(100);
                sleep(1500);
            }
        }, "Depositor");
        depositor.start();

        // Single withdrawer
        Thread withdrawer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                account.withdraw(50);
                sleep(2000);
            }
        }, "Withdrawer");
        withdrawer.start();
    }

    // Helper method to sleep safely
    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}

