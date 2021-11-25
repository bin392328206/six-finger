hi ，大家好，我是三天打鱼，两天晒网的小六六

## 前言

>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**

最近小六六业余时间都会刷点算法，至于原因当然是小六六太菜了，哈哈，是真的菜，不过刷了一些题后，发现自己的脑子不那么傻了，看来刷力扣还是有助于思维的提升，建议大家业余的时候刷刷，当然算法思维对于我们写的代码的性能也是有帮助的呢？刷力扣的过程中，经常会用到Arrays.sort这个方法，今天小六六就给大家分享分享这个方法，看看Java的JDK是怎么去做排序的



![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fcc51a6b45274f60a45c8c064d1af7a7~tplv-k3u1fbpfcp-watermark.image?)


## 常见的排序算法
要了解Arrays.sort的底层原理，我们先来看看我们耳熟能详的排序算法吧，这边只是大概的提提这些算法，我们一般在开发的过程中，会碰到以下的排序算法

### **算法一：插入排序**

插入排序示意图

![g7pk0bpfgb.gif](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/686a2abaaf93455f98a4bda385d5212e~tplv-k3u1fbpfcp-watermark.image?)

插入排序是一种最简单直观的排序算法，它的工作原理是通过构建有序序列，对于未排序数据，在已排序序列中从后向前扫描，找到相应位置并插入。

**算法步骤：**

1）将第一待排序序列第一个元素看做一个有序序列，把第二个元素到最后一个元素当成是未排序序列。

2）从头到尾依次扫描未排序序列，将扫描到的每个元素插入有序序列的适当位置。（如果待插入的元素与有序序列中的某个元素相等，则将待插入元素插入到相等元素的后面。）


### **算法二：选择排序**

选择排序示意图

![c3kk4nrkcz.gif](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/63f1e6e34abb465180abdea8598984cc~tplv-k3u1fbpfcp-watermark.image?)

**选择排序**
(Selection sort)也是一种简单直观的排序算法。

**算法步骤：**

1）首先在未排序序列中找到最小（大）元素，存放到排序序列的起始位置

2）再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾。

3）重复第二步，直到所有元素均排序完毕。

### **算法三：冒泡排序**

冒泡排序示意图

![j6bz6vne4q.gif](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/66f9090650ca481cafff2ba27a52c5e4~tplv-k3u1fbpfcp-watermark.image?)

**冒泡排序**（Bubble Sort）也是一种简单直观的排序算法。它重复地走访过要排序的数列，一次比较两个元素，如果他们的顺序错误就把他们交换过来。走访数列的工作是重复地进行直到没有再需要交换，也就是说该数列已经排序完成。这个算法的名字由来是因为越小的元素会经由交换慢慢“浮”到数列的顶端。

**算法步骤：**

1）比较相邻的元素。如果第一个比第二个大，就交换他们两个。

2）对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。这步做完后，最后的元素会是最大的数。

3）针对所有的元素重复以上的步骤，除了最后一个。

4）持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。


### **算法四：归并排序**

归并排序示意图

![s79zjg4atb.gif](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b978c92511e4403aa103bba120d103d4~tplv-k3u1fbpfcp-watermark.image?)

**归并排序**（Merge sort）是建立在归并操作上的一种有效的排序算法。该算法是采用分治法（Divide and Conquer）的一个非常典型的应用。

**算法步骤：**

1.  申请空间，使其大小为两个已经排序序列之和，该空间用来存放合并后的序列
1.  设定两个指针，最初位置分别为两个已经排序序列的起始位置
1.  比较两个指针所指向的元素，选择相对小的元素放入到合并空间，并移动指针到下一位置
1.  重复步骤3直到某一指针达到序列尾
1.  将另一序列剩下的所有元素直接复制到合并序列尾


### **算法五：快速排序**

快速排序示意图

![mwh79s0eqx.gif](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cce7a145204d45789a5247f0f4f4035f~tplv-k3u1fbpfcp-watermark.image?)

**快速排序**是由东尼·霍尔所发展的一种排序算法。在平均状况下，排序 n 个项目要Ο(n log n)次比较。在最坏状况下则需要Ο(n2)次比较，但这种状况并不常见。事实上，快速排序通常明显比其他Ο(n log n) 算法更快，因为它的内部循环（inner loop）可以在大部分的架构上很有效率地被实现出来。

快速排序使用分治法（Divide and conquer）策略来把一个串行（list）分为两个子串行（sub-lists）。

**算法步骤：**

1 从数列中挑出一个元素，称为 “基准”（pivot），

2 重新排序数列，所有元素比基准值小的摆放在基准前面，所有元素比基准值大的摆在基准的后面（相同的数可以到任一边）。在这个分区退出之后，该基准就处于数列的中间位置。这个称为分区（partition）操作。

3 递归地（recursive）把小于基准值元素的子数列和大于基准值元素的子数列排序。

递归的最底部情形，是数列的大小是零或一，也就是永远都已经被排序好了。虽然一直递归下去，但是这个算法总会退出，因为在每次的迭代（iteration）中，它至少会把一个元素摆到它最后的位置去。


> 当然还有其他的几种排序算法，大家也去了解下，但是我们常用的就上面5种了


## 来看看Arrays.sort和Collections.sort实现原理解析
1.  事实上Collections.sort方法底层就是调用的array.sort方法，而且不论是Collections.sort或者是Arrays.sort方法，
1.  跟踪下源代码吧，首先我们写个demo



```

Arrays.sort(nums1);
Arrays.sort(nums2);
int length1 = nums1.length, length2 = nums2.length;
int[] intersection = new int[Math.min(length1, length2)];
int index1 = 0, index2 = 0, index = 0;
while (index1 < length1 && index2 < length2) {
    if (nums1[index1] < nums2[index2]) {
        index1++;
    } else if (nums1[index1] > nums2[index2]) {
        index2++;
    } else {
        intersection[index] = nums1[index1];
        index1++;
        index2++;
        index++;
    }
}
return Arrays.copyOfRange(intersection, 0, index);
```

2. 点进去看看
   上面的英文注释解释：
   将指定的数组按升序数字排序。
   实现说明:排序算法是由Vladimir Yaroslavskiy, Jon Bentley和Joshua Bloch设计的双枢轴快速排序。该算法在许多数据集上提供了O(n log(n))的性能，这导致其他快速排序的性能下降到二次的性能，而且通常比传统的(单轴)快速排序实现更快。

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/82dfc8a5051042218afdd77833dcc016~tplv-k3u1fbpfcp-watermark.image?)

- 快速排序使用的是分治思想，将原问题分成若干个子问题进行递归解决。选择一个元素作为轴(pivot)，通过一趟排序将要排序的数据分割成独立的两部分，其中一部分的所有数据都比轴元素小，另外一部分的所有数据都比轴元素大，然后再按此方法对这两部分数据分别进行快速排序，整个排序过程可以递归进行，以此达到整个数据变成有序序列。
- 双轴快排(DualPivotQuicksort)，顾名思义有两个轴元素pivot1，pivot2，且pivot ≤pivot2，将序列分成三段：x < pivot1、pivot1 ≤ x ≤ pivot2、x >pivot2，然后分别对三段进行递归。这个算法通常会比传统的快排效率更高，也因此被作为Arrays.java中给基本类型的数据排序的具体实现。


3.  接着看我们重要的sort方法


-  判断数组的长度是否大于286，大于则使用归并排序(merge sort)，否则执行下一步

```
// Use Quicksort on small arrays
if (right - left < QUICKSORT_THRESHOLD)
{
       //QUICKSORT_THRESHOLD = 286
        sort(a, left, right, true);
        return;
 }
```

- 小过这个阀值的进入Quicksort （快速排序），其实并不全是，点进去sort(a, left, right, true);方法：

```
// Use insertion sort on tiny arrays
if (length < INSERTION_SORT_THRESHOLD)
{
    if (leftmost)
    {
        ......
```
- 点进去后我们看到第二个阀值INSERTION_SORT_THRESHOLD（47），如果元素少于47这个阀值，就用插入排序，往下看确实如此：


```
/*
 * Traditional (without sentinel) insertion sort,
 * optimized for server VM, is used in case of
 * the leftmost part.
 */
for (int i = left, j = i; i < right; j = ++i)
{
     int ai = a[i + 1];
     while (ai < a[j])
     {
          a[j + 1] = a[j];
          if (j-- == left)
          {
               break;
           }
      }
      a[j + 1] = ai;
```

- 这是少于阀值QUICKSORT_THRESHOLD（286）的两种情况，至于大于286的，它会进入归并排序（Merge Sort），但在此之前，它有个小动作：



```

// Check if the array is nearly sorted
    for (int k = left; k < right; run[count] = k) {        if (a[k] < a[k + 1]) { // ascending
            while (++k <= right && a[k - 1] <= a[k]);
        } else if (a[k] > a[k + 1]) { // descending
            while (++k <= right && a[k - 1] >= a[k]);            for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {                int t = a[lo]; a[lo] = a[hi]; a[hi] = t;
            }
        } else { // equal
            for (int m = MAX_RUN_LENGTH; ++k <= right && a[k - 1] == a[k]; ) {                if (--m == 0) {
                    sort(a, left, right, true);                    return;
                }
            }
        }        /*
         * The array is not highly structured,
         * use Quicksort instead of merge sort.
         */
        if (++count == MAX_RUN_COUNT) {
            sort(a, left, right, true);            return;
        }
    }
```


## 总结
我们来看看Arrays.sort整一个流程图吧

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f59232b6c0674afe96a10880ff96195a~tplv-k3u1fbpfcp-watermark.image?)

O(nlogn)只代表增长量级，同一个量级前面的常数也可以不一样，不同数量下面的实际运算时间也可以不一样。

数量非常小的情况下（就像上面说到的，少于47的），插入排序等可能会比快速排序更快。 所以数组少于47的会进入插入排序。

快排数据越无序越快（加入随机化后基本不会退化），平均常数最小，不需要额外空间，不稳定排序。

归排速度稳定，常数比快排略大，需要额外空间，稳定排序。

所以大于或等于47或少于286会进入快排，而在大于或等于286后，会有个小动作：“// Check if the array is nearly sorted”。这里第一个作用是先梳理一下数据方便后续的双枢轴归并排序，第二个作用就是即便大于286，但在降序组太多的时候（被判断为没有结构的数据，The array is not highly structured,use Quicksort instead of merge sort.），要转回快速排序。


## 参考
- [排序算法详解](https://blog.csdn.net/hguisu/article/details/7776068)

