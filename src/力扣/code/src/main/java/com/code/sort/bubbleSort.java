package com.code.sort;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/16 16:49
 * 冒泡排序的实现
 */
public class bubbleSort {

    public static void main(String[] args) {

        int[] a={1,2,5,3,8,};
        int[] ints = bubbSort(a);


    }


    public static int[] bubbSort(int[] a){
        int length = a.length;
        for(int i=0;i<length;i++){
            //冒泡退出的标记位置
            boolean flag=false;

            for (int j=i+1;j<length;j++){
                if (a[i]>a[j]){
                    int temp=a[i];
                    a[i]=a[j];
                    a[j]=temp;
                }

            }
        }


        return a;
    }
}
