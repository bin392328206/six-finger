package com.code.thread;

import java.util.concurrent.CountDownLatch;

public class Foo1 {
    //定义2个countDownLatch
    private CountDownLatch countDownLatchA=new CountDownLatch(1); //说明只要一个线程调用它就放行 ，它是到0就放行
    private CountDownLatch countDownLatchB=new CountDownLatch(1);
    public Foo1() {

    }

    public void first(Runnable printFirst) throws InterruptedException {
            // printFirst.run() outputs "first". Do not change or remove this line.
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
        final Foo1 foo = new Foo1();

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
                    foo.countDownLatchA.countDown();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    foo.countDownLatchA.await();
                    foo.second(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(2);
                        }
                    });
                    foo.countDownLatchB.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        foo.countDownLatchB.await();
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
