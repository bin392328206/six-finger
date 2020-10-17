package com.code.sort;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/17 15:08
 * 归并排序 https://blog.csdn.net/baidu_39334407/article/details/89102994?utm_medium=distribute.pc_relevant.none-task-blog-title-2&spm=1001.2101.3001.4242
 */
public class mergeSort {

    public static void main(String[] args) {
        int[] a={2,1,3,6,5,12,7};
        sort(a);
    }



    public static void  sort(int[] a){
        
        int[] a1= meSort(a,0,a.length-1);
    }

    private static int[] meSort(int[] a, int left, int right) {

        if (left==right){
            return a;
        }
        //分治的思想，当然是先分，其实递龟之后呢？最后就只有一个了
        int mid=(left+right)/2;
        meSort(a,left,mid);
        meSort(a,mid+1,right);

        //然后再是合并
        merge(a,left,mid,right);
        return a;
    }

    //合并
    private static void merge(int[] a, int left, int mid, int right) {
        int[] tmp = new int[a.length];
        int r1 = mid + 1;
        int tIndex = left;
        int cIndex=left;
        // 逐个归并
        while(left <=mid && r1 <= right) {
            if (a[left] <= a[r1]){
                tmp[tIndex++] = a[left++];

            }
            else{
                tmp[tIndex++] = a[r1++];
            }
        }
        // 将左边剩余的归并
        while (left <=mid) {
            tmp[tIndex++] = a[left++];
        }
        // 将右边剩余的归并
        while ( r1 <= right ) {
            tmp[tIndex++] = a[r1++];
        }
        // TODO Auto-generated method stub
        //从临时数组拷贝到原数组
        while(cIndex<=right){
            a[cIndex]=tmp[cIndex];
            //输出中间归并排序结果
            System.out.print(a[cIndex]+"\t");
            cIndex++;
        }
        System.out.println();
    }

}
