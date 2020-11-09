package com.code.array;

import java.util.ArrayDeque;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/9 15:07
 * 给定一个数组 nums 和滑动窗口的大小 k，请找出所有滑动窗口里的最大值。
 * 示例:
 * <p>
 * 输入: nums = [1,3,-1,-3,5,3,6,7], 和 k = 3
 * 输出: [3,3,5,5,6,7]
 * <p>
 * 提示：
 * 你可以假设 k 总是有效的，在输入数组不为空的情况下，1 ≤ k ≤ 输入数组的大小。
 * <p>
 * 滑动窗口问题
 */
public class MaxSlidingWindow {

    public static void main(String[] args) {
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        int[] ints = maxSlidingWindow(nums, k);
        for (int anInt : ints) {
            System.out.println(anInt);
        }


    }

    public static int[] maxSlidingWindow(int[] nums, int k) {
        // 非空判断
        if (nums == null || k <= 0) {
            return new int[0];
        }
        // 最终结果数组
        int[] res = new int[nums.length - k + 1];
        // 存储的数据为元素的下标 双端队列
        ArrayDeque<Integer> deque = new ArrayDeque();
        for (int i = 0; i < nums.length; i++) {
            // 1.移除左边超过滑动窗口的下标
            if (i >= k && (i - k) >= deque.peek()) {
                deque.removeFirst();
            }

            // 2.从最后面开始移除小于 nums[i] 的元素
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.removeLast();
            }
            // 3.下标加入队列
            deque.offer(i);

            // 4.将最大值加入数组
            int rindex = i - k + 1;
            if (rindex >= 0) {
                res[rindex] = nums[deque.peek()];
            }
        }
        return res;
    }

    public static int[] maxSlidingWindow1(int[] nums, int k) {
        if (nums.length == 0 || k <= 0) {
            return new int[0];
        }

        int[] res = new int[nums.length - k + 1];

        for (int i = 0; i < nums.length; i++) {
            //先定义最大值
            int max = nums[i];
            for (int j = i + 1; j < (i + k); j++) {
                max = (nums[j] > max) ? nums[j] : max;
            }
            res[i] = max;
        }

        return res;


    }

}
