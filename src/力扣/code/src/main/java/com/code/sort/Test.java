package com.code.sort;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/12/22 14:50
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(A.count);
    }

}
class A {
    public static A a=new A();
    public static Long count=5L;
    private A(){
        count++;
    }
}