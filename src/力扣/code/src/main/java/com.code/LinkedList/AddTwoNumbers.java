package com.code.LinkedList;


/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/22 21:18
 */
public class AddTwoNumbers {

    //这边定义的是链表的Node
    static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {

        //先判断空
        if (null==l1|null==l2) return null;
        //首先我们得定义一个dummyHead
        ListNode dummyHead = new ListNode(0);
        //定义一个需要返回的 ListNode
        ListNode res=dummyHead;
        //用来标记是否进位
        int addOne =0;
        while (l1!=null||l2!=null|addOne!=0){
            // 说明一下 为啥addOne 要！=0 因为你假设最后2个链表是 4+6 那么此时当前为为0 addone 为1 进位了，所以要做addOne 不为0的判断
            //只要传进来的Node的下一个节点还有值，我们就得继续遍历
            int x=l1!=null?l1.val:0;  //如果不为null 就取他的值
            int y=l2!=null?l2.val:0;//同上
            //我们此时来计算 当前加完之前的值
           int  sum=(x+y+addOne);
            res.next=new ListNode((sum)%10);
            //算完之后 把3个链表都往后移动一位
            addOne=sum/10;
            res=res.next;
            //把需要继续计算的链表继续往后移动
            if (l1!=null) l1=l1.next;
            if (l2!=null) l2=l2.next;
        }
        return dummyHead.next;
    }


    //写一个打印的方法看看
    public static String print(ListNode listNode){
            StringBuffer stringBuffer=new StringBuffer();
            while (listNode!=null){
                stringBuffer.append(listNode.val);
                listNode=listNode.next;
            }
            return stringBuffer.toString();
    }



    public static void main(String[] args) {
        ListNode l1=new ListNode(3);
        ListNode l2=new ListNode(5);
        l1.next=new ListNode(7);
        l2.next=new ListNode(6);

        System.out.println(print(addTwoNumbers(l1,l2)));

    }
}
