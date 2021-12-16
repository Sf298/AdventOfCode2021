package day16;

import org.apache.commons.lang3.tuple.Triple;
import utils.StringConsumer;
import utils.Utils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class Day16 {

    private static final int DAY = Integer.parseInt(Day16.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        String hex = Utils.streamLinesForDay(DAY).collect(Collectors.joining("\n"));
        String bin = hex2bin(hex);
        System.out.println(bin);

        System.out.println(sumPacketVersions(new StringConsumer(bin), ""));

        System.out.println("Part 1 ANS: " + bin);
    }

    private static long sumPacketVersions(StringConsumer sc, String pre) {
        ParsedPacket parentPacket = new ParsedPacket(sc);
        System.out.println(pre + parentPacket);
        if (parentPacket.isLiteral()) {
            return parentPacket.version;
        }

        long sum = parentPacket.version;
        StringConsumer sc2 = new StringConsumer(parentPacket.payload);
        if (nonNull(parentPacket.packetCount)) {
            for (int i = 0; i < parentPacket.packetCount; i++) {
                sum += sumPacketVersions(sc2, pre+"\t");
            }
        } else {
            while (sc2.hasNext()) {
                sum += sumPacketVersions(sc2, pre+"\t");
            }
        }
        System.out.println("remainig: '"+sc2+"'");

        return sum;
    }

    private static void part2() {
        int ans = 0;
        System.out.println("Part 2 ANS: " + ans);
    }

    private static String hex2bin(String hex) {
        Map<Character, String> map = new HashMap<>();
        map.put('0', "0000");
        map.put('1', "0001");
        map.put('2', "0010");
        map.put('3', "0011");
        map.put('4', "0100");
        map.put('5', "0101");
        map.put('6', "0110");
        map.put('7', "0111");
        map.put('8', "1000");
        map.put('9', "1001");
        map.put('A', "1010");
        map.put('B', "1011");
        map.put('C', "1100");
        map.put('D', "1101");
        map.put('E', "1110");
        map.put('F', "1111");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i++) {
            sb.append(map.get(hex.charAt(i)));
        }

        return sb.toString();
    }

    private static class ParsedPacket {
        public int version;
        public int typeId;
        public String lengthTypeId;
        public Integer packetCount;

        public BigInteger literal;
        public String payload;

        public ParsedPacket(StringConsumer sc) {
            version = Integer.parseInt(sc.consume(3),  2);
            typeId = Integer.parseInt(sc.consume(3),  2);

            if (typeId == 4) {
                StringBuilder numberBuilder = new StringBuilder();
                while (sc.consume(1).equals("1")) {
                    numberBuilder.append(sc.consume(4));
                }
                numberBuilder.append(sc.consume(4));
                literal = new BigInteger(numberBuilder.toString(), 2);
                return;
            }

            lengthTypeId = sc.consume(1);
            if (lengthTypeId.equals("0")) {
                int payloadLength = Integer.parseInt(sc.consume(15), 2);
                packetCount = null;
                payload = sc.consume(payloadLength);
            } else {
                packetCount = Integer.parseInt(sc.consume(11), 2);
                payload = sc.consume(sc.dToEnd());
            }
        }

        public boolean isLiteral() {
            return nonNull(literal);
        }

        @Override
        public String toString() {
            if (isLiteral())
                return "ParsedPacket{v" + version + ", type=" + typeId + ", literal=" + literal + '}';
            else
                return "ParsedPacket{v" + version + ", type=" + typeId + ", lengthTypeId=" + lengthTypeId + ", packetCount=" + packetCount + ", payload='" + payload + '\'' + '}';
        }
    }

}
