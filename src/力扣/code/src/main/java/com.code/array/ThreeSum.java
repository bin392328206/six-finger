package com.code.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/20 16:49
 *
 * 给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有满足条件且不重复的三元组。
 *
 * 注意：答案中不可以包含重复的三元组。
 *
 * 给定数组 nums = [-1, 0, 1, 2, -1, -4]，
 *
 * 满足要求的三元组集合为：
 * [
 *   [-1, 0, 1],
 *   [-1, -1, 2]
 * ]
 *
 */
public class ThreeSum {

    public List<List<Integer>>  treeSum(int[] nums){
        int n=nums.length;
        Arrays.sort(nums);
        List<List<Integer>> ans =new ArrayList<List<Integer>>();
        for (int fist=0;fist<n;++fist){
            //需要和上一次枚举的a的数值不同
            if (fist>0&&nums[fist]==nums[fist-1]){
                continue;
            }

            //c对应的指针 初始化指向数组的最右端
            int third=n-1;
            //这个是b+c需要等于的结果
            int target=-nums[fist];

            // 枚举b
            for (int second=fist+1;second<n;++second){
                //需要和上一次枚举的值不一样
                if (second>fist+1&&nums[second]==nums[second-1]){
                    continue;
                }

                //需要保证 b的指针在c的指针的左边

                while (second<third&&nums[second]+nums[third]>target){
                    --third;
                }
                //如果指针重合，随着b增加也不会有满足条件了，可以退出循环了
                if (second==third){
                    break;
                }

                if (nums[second]+nums[third]==target){
                    List<Integer> list=new ArrayList<>();
                    list.add(nums[fist]);
                    list.add(nums[second]);
                    list.add(nums[third]);
                    ans.add(list);
                }

            }

        }
        return ans;

    }


}
