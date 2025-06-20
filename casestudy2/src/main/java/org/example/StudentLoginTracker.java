package org.example;

import java.util.concurrent.ConcurrentHashMap;

public class StudentLoginTracker {
    public static void main(String [] args){
        ConcurrentHashMap<String, Integer> loginCounts = new ConcurrentHashMap<>();
        loginCounts.put("student123", 0);
        loginCounts.put("student456", 0);

        Runnable incrementLoginStudent123 = () ->{
            for(int i=0; i< 500; i++){
                loginCounts.compute("student123", (k,v) -> v+1);
            }
        };

        Runnable incrementLoginStudent456 = () ->{
            for(int i=0; i< 700; i++){
                loginCounts.compute("student456", (k,v) -> v+1);
            }
        };

        Thread t1 = new Thread(incrementLoginStudent123);
        Thread t2 = new Thread(incrementLoginStudent123);
        Thread t3 = new Thread(incrementLoginStudent456);
        Thread t4 = new Thread(incrementLoginStudent456);


        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try{
            t1.join();
            t2.join();
            t3.join();
            t4.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final login count for student123: " + loginCounts.get("student123"));
        System.out.println("Final login count for student456: " + loginCounts.get("student456"));
    }
}
