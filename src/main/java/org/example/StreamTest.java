package org.example;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTest {
    public static void main(String[] args) {
        List<Integer> collect = Stream.of(1, 2, 3, 4, 5, 6).collect(Collectors.toList());
        collect.stream().forEach(item->{
            System.out.println(item);
            if (item>3)
                throw new RuntimeException();
        });

    }
}
