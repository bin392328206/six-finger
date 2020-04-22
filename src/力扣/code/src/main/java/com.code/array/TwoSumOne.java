package com.code.array;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/21 10:28
 */
public class TwoSumOne {

    /**
     *  @author: 小六六
     *  @Date: 2020/4/21 10:44
     *  @Description:
     *  一次遍历+map的解法
     */
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map=new HashMap();
        if (nums.length==0){
            throw new RuntimeException("报错了");
        }
        for (int i=0;i<nums.length;i++){
            int key= target-nums[i];
            if (map.containsKey(nums[i])){
                return new int[]{map.get(nums[i]),i};
            }
            map.put(key,i);
        }
        return null;
    }
    /**
     *  @author: 小六六
     *  @Date: 2020/4/21 10:44
     *  @Description:
     *  暴力解法
     */
    public static int[] twoSumOne(int[] nums, int target) {
      for (int i=0;i<nums.length;i++){
          for (int j=i+1;j<nums.length;j++){
              if (nums[i]+nums[j]==target){
                  return new int[]{i,j};
              }
          }
      }
      return null;
    }



    public static void main(String[] args) {
        int [] nums = new int[]{2,7, 11, 15};
        int[] ints = twoSumOne(nums, 9);

        System.out.println(ints[0]);

        System.out.println(ints[1]);
    }
}
