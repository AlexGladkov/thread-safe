package com.agladkov.thread_safe.models;


public class Kitchen {
    private final KitchenLog kitchenLog;

    public Kitchen(KitchenLog kitchenLog) {
        this.kitchenLog = kitchenLog;
    }

    public interface KitchenLog {
        void addMessage(String text);
    }

    private int orderCount = 0;
    private void incOrderCount() {
        orderCount++;
    }

    private void decOrderCount() {
        orderCount--;
    }

    public synchronized void addOrder() {
        while (orderCount >= 3) {
            try {
                kitchenLog.addMessage("Waiting for add order");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        incOrderCount();
        kitchenLog.addMessage("Order added to kitchen, now cooking " + orderCount);

        notify();
    }

    public synchronized void getOrder() {
        while (orderCount < 1) {
            try {
                kitchenLog.addMessage("Waiting for get order");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        cook();
        decOrderCount();
        kitchenLog.addMessage("Order finished, now cooking " + orderCount);

        notify();
    }

    private void cook() {
        kitchenLog.addMessage("Order is cooking now...");
    }
}
