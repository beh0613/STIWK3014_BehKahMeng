package org.example;

public class ProducerConsumerDemo {
    static class SharedData{
        private  boolean dataReady = false;
        private  String data;

        public synchronized void produce(){
            try{
                System.out.println("Producer: Preparing data ...");
                Thread.sleep(1000);
                data = "Hello from producer";
                dataReady = true;
                System.out.println("Producer: Data is ready.");
                notifyAll();
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }

        public synchronized void consume(){
            try{
                while(!dataReady){
                    System.out.println("Consumer: Waiting for data...");
                    wait();
                }
                System.out.println("Consumer: Received ->" + data);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String [] args) {
        SharedData sharedData = new SharedData();

        //Create consumer thread
        Thread consumerThread = new Thread(() -> sharedData.consume());
        Thread producerThread = new Thread(() -> sharedData.produce());

        consumerThread.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        producerThread.start();
        }
    }


