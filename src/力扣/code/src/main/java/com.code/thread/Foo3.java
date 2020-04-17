package com.code.thread;

import java.util.concurrent.locks.LockSupport;

/**'
 * 归根结底，LockSupport调用的Unsafe中的native代码：
 * public native void unpark(Thread jthread);
 * public native void park(boolean isAbsolute, long time);
 * 两个函数声明清楚地说明了操作对象：park函数是将当前Thread阻塞，而unpark函数则是将另一个Thread唤醒。
 *
 * 与Object类的wait/notify机制相比，park/unpark有两个优点：1. 以thread为操作对象更符合阻塞线程的直观定义；2. 操作更精准，可以准确地唤醒某一个线程（notify随机唤醒一个线程，notifyAll唤醒所有等待的线程），增加了灵活性。
 */
public class Foo3 {
    static Thread t1=null,t2=null,t3=null;

    public Foo3() {

    }

    public void first(Runnable printFirst) throws InterruptedException {

        printFirst.run();
    }

    public void second(Runnable printSecond) throws InterruptedException {
        // printSecond.run() outputs "second". Do not change or remove this line.
        printSecond.run();
    }

    public void third(Runnable printThird) throws InterruptedException {
        // printThird.run() outputs "third". Do not change or remove this line.
        printThird.run();
    }


        public static void main(String[] args) throws Exception {
            final Foo3 foo = new Foo3();

             t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        foo.first(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(1);
                            }
                        });
                        LockSupport.unpark(t2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
             t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    LockSupport.park();
                    try {
                        foo.second(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(2);
                            }
                        });
                        LockSupport.unpark(t3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
             t3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    LockSupport.park();
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