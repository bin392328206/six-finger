package com.code.queue;

import java.util.Random;
import java.util.Scanner;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/19 22:08
 * @des 通过数组实现一个环形队列 不照成空间的浪费
 */
public class CircleQueueArray {
    public static void main(String[] args) {

        //测试一把
        System.out.println("测试数组模拟环形队列的案例~~~");

        // 创建一个环形队列
        CircleArray queue = new CircleArray(4); //说明设置4, 其队列的有效数据最大是3
        char key = ' '; // 接收用户输入
        Scanner scanner = new Scanner(System.in);//
        boolean loop = true;
        // 输出一个菜单
        while (loop) {
            System.out.println("s(show): 显示队列");
            System.out.println("e(exit): 退出程序");
            System.out.println("a(add): 添加数据到队列");
            System.out.println("g(get): 从队列取出数据");
            System.out.println("h(head): 查看队列头的数据");
            key = scanner.next().charAt(0);// 接收一个字符
            switch (key) {
                case 's':
                    queue.showQueue();
                    break;
                case 'a':
                    System.out.println("输出一个数");
                    int value = scanner.nextInt();
                    queue.addQueue(value);
                    break;
                case 'g': // 取出数据
                    try {
                        int res = queue.getQueue();
                        System.out.printf("取出的数据是%d\n", res);
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println(e.getMessage());
                    }
                    break;
                case 'h': // 查看队列头的数据
                    try {
                        int res = queue.headQueue();
                        System.out.printf("队列头的数据是%d\n", res);
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println(e.getMessage());
                    }
                    break;
                case 'e': // 退出
                    scanner.close();
                    loop = false;
                    break;
                default:
                    break;
            }
        }
        System.out.println("程序退出~~");
    }

    static class CircleArray {

        private int maxSize; // 表示数组的最大容量
        //front 变量的含义做一个调整： front 就指向队列的第一个元素, 也就是说 arr[front] 就是队列的第一个元素
        //front 的初始值 = 0
        private int front;
        //rear 变量的含义做一个调整：rear 指向队列的最后一个元素的后一个位置. 因为希望空出一个空间做为约定.
        //rear 的初始值 = 0
        private int rear; // 队列尾
        private int[] arr; // 该数据用于存放数据, 模拟队列

        public CircleArray(int maxSize) {
            this.maxSize = maxSize;
            arr = new int[maxSize];
        }

        // 判断队列是否满
        public Boolean isFull() {
            return (rear + 1) % maxSize == front;
        }

        // 判断队列是否为空
        public Boolean isEmpty() {
            return rear == front;
        }

        //往循环队列里面添加数据 入队列
        public void addQueue(int n) {
            if (isFull()) {
                System.out.println("队列满，不能加入数据~");
                return;
            }
            arr[rear] = n;
            rear = (rear + 1) % maxSize;
        }

        // 获取队列的数据, 出队列
        public int getQueue() {
            if (isEmpty()) {
                // 通过抛出异常
                throw new RuntimeException("队列空，不能取数据");
            }
            int value = arr[front];
            front = (front + 1) % maxSize;
            return value;
        }
        // 显示队列的所有数据
        public void showQueue(){
            // 遍历
            if (isEmpty()) {
                System.out.println("队列空的，没有数据~~");
                return;
            }
            for (int i=0;i<front+size();i++){
                System.out.printf("arr[%d]=%d\n", i % maxSize, arr[i % maxSize]);
            }
        }

        // 求出当前队列有效数据的个数
        public int size(){
           return   (rear+maxSize-front)%maxSize;
        }


        // 显示队列的头数据， 注意不是取出数据
        public int headQueue() {
            // 判断
            if (isEmpty()) {
                throw new RuntimeException("队列空的，没有数据~~");
            }
            return arr[front];
        }
    }
}
