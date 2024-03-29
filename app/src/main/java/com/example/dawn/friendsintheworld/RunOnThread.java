package com.example.dawn.friendsintheworld;


import android.util.Log;

import java.util.LinkedList;

public class RunOnThread {
        private Buffer<Runnable> buffer = new Buffer<Runnable>();
        private Worker worker;

        public void start() {
            if(worker==null) {
                worker = new Worker();
                worker.start();
            }
        }

        public void stop() {
            if(worker!=null) {
                worker.interrupt();
                worker=null;
            }
        }

        public void execute(Runnable runnable) {
            buffer.put(runnable);
        }

        private class Worker extends Thread {
            public void run() {
                Runnable runnable;
                while(worker!=null) {
                    try {
                        runnable = buffer.get();
                        runnable.run();
                    } catch (InterruptedException e) {
                        worker=null;
                    }
                }
            }
        }

        private class Buffer<T> {
            private LinkedList<T> buffer = new LinkedList<T>();

            public synchronized void put(T element) {
                buffer.addLast(element);
                notifyAll();
            }

            public synchronized T get() throws InterruptedException {
                while(buffer.isEmpty()) {
                    wait();
                }
                return buffer.removeFirst();
            }
        }
    }

