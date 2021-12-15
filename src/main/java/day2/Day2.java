package day2;

import org.apache.commons.lang3.tuple.Pair;
import utils.Utils;

import java.util.concurrent.atomic.AtomicInteger;

public class Day2 {

    //private static final int DAY = Integer.parseInt(Day2.class.getName().replaceAll("^0-9", ""));

    public static void main(String[] args) {
        //part1();
        part2();
    }

    private static void part1() {
        AtomicInteger dv = new AtomicInteger();
        AtomicInteger dh = new AtomicInteger();

        Utils.streamLinesForDay(2)
                .map(l -> l.split(" "))
                .map(a -> Pair.of(a[0], Integer.parseInt(a[1])))
                .forEach(p -> {
                    switch (p.getKey()) {
                        case "up":
                            dv.addAndGet(-p.getValue());
                            break;
                        case "down":
                            dv.addAndGet(p.getValue());
                            break;
                        case "forward":
                            dh.addAndGet(p.getValue());
                            break;
                    }
                });

        System.out.println("dh = " + dh);
        System.out.println("dv = " + dv);

        System.out.println("Part 1 ANS: " + (dh.get() * dv.get()));
    }

    private static void part2() {
        AtomicInteger dv = new AtomicInteger();
        AtomicInteger dh = new AtomicInteger();
        AtomicInteger aim = new AtomicInteger();

        Utils.streamLinesForDay(2)
                .map(l -> l.split(" "))
                .map(a -> Pair.of(a[0], Integer.parseInt(a[1])))
                .forEach(p -> {
                    switch (p.getKey()) {
                        case "down" -> aim.addAndGet(p.getValue());
                        case "up" -> aim.addAndGet(-p.getValue());
                        case "forward" -> {
                            dh.addAndGet(p.getValue());
                            dv.addAndGet(aim.get() * p.getValue());
                        }
                    }
                });

        System.out.println("dh = " + dh);
        System.out.println("dv = " + dv);

        System.out.println("Part 2 ANS: " + (dh.get() * dv.get()));
    }

}
