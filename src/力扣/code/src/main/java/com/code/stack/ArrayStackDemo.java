package com.code.stack;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/8/17 15:43
 *
 * 使用数组实现一个stack
 */
public class ArrayStackDemo {


    public static void main(String[] args) {









    }

    //定义一个数组栈
class ArrayStack{
        private int top =-1;//top表示栈顶，初始化为-1

        private int maxSize; //栈的当前大小

        private int[] stack;  //存放栈的数据的容器


        public  ArrayStack( int maxSize ){
            this.maxSize=maxSize;
            stack=new int[maxSize]; //根据栈的最大容量设置栈的大小
        }


        //判断栈是否满了
        public boolean isFull(){
            return top==maxSize-1;
        }

        //判断栈是否为空

        public  boolean isEmpty(){
            return -1==top;
        }

            //数据入栈
        public void  push(int value){
        //先判断是否栈满
            boolean full = this.isFull();
            if (full){
                System.out.println("栈满");
                return;
            }
            top++;
            stack[top]=value;
        }

        //数据出栈,只能从栈顶元素一个个出栈
        public int pop(){
            if (isEmpty()){
                //抛出异常
                throw new RuntimeException("栈空，没有数据~");
            }
            int value=stack[top];
            top--;
            return value;
        }

        //增加一个方法，可以返回当前栈顶的值, 但是不是真正的pop
        public int peek() {
            return stack[top];
        }


    }


}
