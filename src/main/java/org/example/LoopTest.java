package org.example;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoopTest {
    public static void main(String[] args) {
        List<Integer> numList = Stream.of(1,1,2,2,3,3,4,4,5,5).collect(Collectors.toList());
        method2(numList);
    }

    // 删除
   // 普通for循环
    public static void method1(List<Integer> numList){
        for (int i = 0; i < numList.size(); i++) {
            if (numList.get(i)<=5){
                numList.remove(i);
                //i--;
            }
        }
        numList.forEach(System.out::println);
    }
    // 增强for循环
    public static void method2(List<Integer> numList){
        for (Integer item:numList){
            if (item<=5){
                numList.remove(item);
            }

        }
    }

    // Iterator
    public static void  method3 (List<Integer> numList){
        Iterator<Integer> iterator = numList.iterator();
        while(iterator.hasNext()){
            Integer item = iterator.next();
            if (item<=5){
                iterator.remove();
            }
        }
        numList.forEach(System.out::println);
    }
    // 增加
    // 普通for循环
    public static void method4(List<Integer> numList){
        for (int i = 0; i < numList.size(); i++) {
            if (numList.get(i)<=5){
                numList.add(i+5);
                //i--;
            }
        }
        numList.forEach(System.out::println);
    }
    // 增强for循环
    public static void method5(List<Integer> numList){
        for (Integer item:numList){
            if (item<=5){
                numList.add(item+5);
            }

        }
    }
}
