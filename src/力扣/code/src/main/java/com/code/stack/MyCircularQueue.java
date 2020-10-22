package com.code.stack;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/22 9:46
 */
public class MyCircularQueue {
    //定义数组
    private int[] arr;
    //大小
    private int size;
    //前缀
    private int front;
    //后缀
    private int last;

    /**
     * Initialize your data structure here. Set the size of the queue to be k.
     */
    public MyCircularQueue(int k) {
        if (k < 0) {
            throw new IllegalArgumentException("Queue size is less than 0!");
        }
        this.arr = new int[k];
        this.size = 0;
        this.front = 0;
        this.last = 0;
    }

    /**
     * Insert an element into the circular queue. Return true if the operation is successful.
     */
    public boolean enQueue(int value) {
        if (size == arr.length) {
            return false;
        }
        arr[last] = value;
        size++;
        last = last == arr.length - 1 ? 0 : last + 1;
        return true;
    }

    /**
     * Delete an element from the circular queue. Return true if the operation is successful.
     */
    public boolean deQueue() {
        if (size == 0) {
            return false;
        }

        front = front == arr.length - 1 ? 0 : front + 1;
        size--;
        return true;
    }

    /**
     * Get the front item from the queue.
     */
    public int Front() {
        if (size==0){
            return -1;
        }

        return arr[front];
    }

    /**
     * Get the last item from the queue.
     */
    public int Rear() {
        if (size==0){
            return -1;
        }
        return last==0? arr[arr.length-1]:arr[last-1];
    }

    /**
     * Checks whether the circular queue is empty or not.
     */
    public boolean isEmpty() {
            return size==0;
    }

    /**
     * Checks whether the circular queue is full or not.
     */
    public boolean isFull() {
        return size==arr.length;
    }
}
