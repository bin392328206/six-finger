package com.code.thread;

/**
 * 上面方法就是采用一个synchronized wait notifyAll  这些来实现的，但是吧，还需要一个while去自旋，还得靠一个信号量，感觉有点low。
 */

class Foo {

    private  int flag=0;
    private Object lock=new Object();
    public Foo() {

    }

    public void first(Runnable printFirst) throws InterruptedException {
        synchronized(lock){
            while (flag!=0){
                lock.wait();
            }
            // printFirst.run() outputs "first". Do not change or remove this line.
            printFirst.run();
            flag=1;
            lock.notifyAll();
        }

    }

    public void second(Runnable printSecond) throws InterruptedException {
        synchronized (lock) {
            while (flag != 1) {
                lock.wait();
            }
            // printSecond.run() outputs "second". Do not change or remove this line.
            printSecond.run();
            flag = 2;
            lock.notifyAll();
        }
    }

    public void third(Runnable printThird) throws InterruptedException {
        synchronized(lock) {
            while (flag != 2) {
                lock.wait();
            }
            // printThird.run() outputs "third". Do not change or remove this line.
            printThird.run();
            lock.notifyAll();
        }
    }

    public static void main(String[] args) throws Exception {
        final Foo foo = new Foo();

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
