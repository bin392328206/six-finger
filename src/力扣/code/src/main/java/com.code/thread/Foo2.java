package com.code.thread;

import java.util.concurrent.Semaphore;

/**
 *
 * 基于信号量的解题思路
 * Semaphore 是什么？
 * Semaphore 是一个计数信号量，必须由获取它的线程释放。
 *
 * 常用于限制可以访问某些资源的线程数量，例如通过 Semaphore 限流。
 */
public class Foo2 {

    //初始化Semaphore为0的原因：
    // 如果这个Semaphore为零，如果另一线程调用(acquire)这个Semaphore就会产生阻塞，
    // 便可以控制second和third线程的执行
    private Semaphore spa=new Semaphore(0) ;
    private Semaphore spb=new Semaphore(0);




    public Foo2() {

    }

    public void first(Runnable printFirst) throws InterruptedException {

        printFirst.run();
        spa.release();
    }

    public void second(Runnable printSecond) throws InterruptedException {
        // printSecond.run() outputs "second". Do not change or remove this line.
        spa.acquire();
        printSecond.run();
        spb.release();
    }

    public void third(Runnable printThird) throws InterruptedException {
        // printThird.run() outputs "third". Do not change or remove this line.
        spb.acquire();
        printThird.run();
    }

    public static void main(String[] args) throws Exception {
        final Foo2 foo = new Foo2();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    foo.first(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(1);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    foo.second(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(2);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    foo.third(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(3);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t3.start();
        t2.start();
        t1.start();

    }
}
