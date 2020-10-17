package com.code.sort;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/16 17:51
 * 插入排序 https://blog.csdn.net/qq_42857603/article/details/81605124
 */
public class insertionSort {

    public static void main(String[] args) {

        int[] a = {1, 2, 5, 3, 8,};
        int[] ints = insertion(a);


    }

    private static int[] insertion(int[] a) {
//        int i,j,temp;
//        for(i=1;i<array.length;i++) {
//            temp=array[i];
//            for(j=i-1;j>=0;j--) {
//                if(temp>array[j]) {
//                    break;
//                }else {
//                    array[j+1]=array[j];
//                }
//            }
//            array[j+1]=temp;
//        }
//        return array;
        for (int i=1;i<a.length;i++){
            int temp=a[i];
            for (int j=j=i-1;j>=0;j--){
                if (temp>a[j]){
                    break;
                }
                else {
                    a[j+1]=a[j];
                }
            a[j+1]=temp;
            }
        }
        return a;
    }
}
