package com.code.array;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/21 9:51
 * 给定一个大小为 n 的数组，找到其中的多数元素。多数元素是指在数组中出现次数大于 ⌊ n/2 ⌋ 的元素。
 * <p>
 * 你可以假设数组是非空的，并且给定的数组总是存在多数元素。
 * 输入: [3,2,3]
 * 输出: 3
 *  输入: [2,2,1,1,1,2,2]
 * 输出: 2
 */
public class MajorityElement {

    public static void main(String[] args) {

        int[] a = {1, 2, 1};
        majorityElement(a);

    }


    public static int majorityElement(int[] nums) {
        int res=0;
        int a = nums.length / 2;
        Map<Integer, Integer> map = new HashMap();
        for (int num : nums) {
            Integer integer = map.get(num);
            if (!map.containsKey(num)) {
                map.put(num, 1);
            } else {
                map.put(num, integer + 1);
            }

        }

        for (Map.Entry<Integer, Integer> integerIntegerEntry : map.entrySet()) {
            if (integerIntegerEntry.getValue()>a){
                res=integerIntegerEntry.getKey();
            }
        }

        return res;
    }
}
