package com.code.randomQuestion;

import java.util.Arrays;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/19 18:09
 * @des题目描述 有一堆石头，每块石头的重量都是正整数。
 * <p>
 * 每一回合，从中选出两块 最重的 石头，然后将它们一起粉碎。假设石头的重量分别为 x 和 y，且 x <= y。那么粉碎的可能结果如下：
 * <p>
 * 如果 x == y，那么两块石头都会被完全粉碎；
 * 如果 x != y，那么重量为 x 的石头将会完全粉碎，而重量为 y 的石头新重量为 y-x。
 * 最后，最多只会剩下一块石头。返回此石头的重量。如果没有石头剩下，就返回 0。
 * <p>
 * <p>
 * <p>
 * 示例：
 * <p>
 * 输入：[2,7,4,1,8,1]
 * 输出：1
 * 解释：
 * 先选出 7 和 8，得到 1，所以数组转换为 [2,4,1,1,1]，
 * 再选出 2 和 4，得到 2，所以数组转换为 [2,1,1,1]，
 * 接着是 2 和 1，得到 1，所以数组转换为 [1,1,1]，
 * 最后选出 1 和 1，得到 0，最终数组转换为 [1]，这就是最后剩下那块石头的重量。
 */
public class question1 {
    public static int lastStoneWeight(int[] stones) {
        //如果倒数第二个元素为0 直接输出最后一个元素
        if (stones[stones.length-2]==0){
            return stones[stones.length-1];
        }
        int index=stones.length-1;
        Arrays.sort(stones);
        while (stones[index-1]!=0){

            int max= stones[index];
            int min=stones[index-1];
            if (max==min){
                stones[index]=stones[index-1]=0;
            }else {
                stones[index]=max-min;
                stones[index-1]=0;
            }
            Arrays.sort(stones);
        }

        return lastStoneWeight(stones);


    }

    public static void main(String[] args) {
        int[] stones = {2, 8 };
        int i = lastStoneWeight(stones);
        System.out.println(i);

    }


}
