package day16;

import utils.StringConsumer;
import utils.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        String hex = /*"A0016C880162017C3686B18A3D4780";*/ Utils.streamLinesForDay(DAY).collect(Collectors.joining("\n"));
        String bin = hex2bin(hex);
        System.out.println(bin);

        System.out.println(new ParsedPacket(new StringConsumer(bin)));

        System.out.println("Part 1 ANS: " + bin);
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
        public List<ParsedPacket> payload = new ArrayList<>();

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
                String payloadString = sc.consume(payloadLength);
                StringConsumer sc2 = new StringConsumer(payloadString);
                while (sc2.hasNext()) {
                    payload.add(new ParsedPacket(sc2));
                }
            } else {
                packetCount = Integer.parseInt(sc.consume(11), 2);
                String payloadString = sc.consume(sc.dToEnd());
                StringConsumer sc2 = new StringConsumer(payloadString);
                for (int i = 0; i < packetCount; i++) {
                    payload.add(new ParsedPacket(sc2));
                }
                System.out.println("Remaining: '"+sc2+"'");
            }
        }

        public boolean isLiteral() {
            return nonNull(literal);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (isLiteral()) {
                sb.append("ParsedPacket{v").append(version).append(", type=").append(typeId).append(", literal=").append(literal).append("}\n");
                return sb.toString();
            } else {
                sb.append("ParsedPacket{v").append(version).append(", type=").append(typeId).append(", packetCount=").append(packetCount).append(", payload=[");
                if (!payload.isEmpty()) {
                    sb.append("\n\t");
                    StringBuilder payloadBuilder = new StringBuilder();
                    for (ParsedPacket pp : payload) {
                        payloadBuilder.append(pp);
                    }
                    String payloadString = payloadBuilder.toString().replaceAll("\n", "\n\t");
                    payloadString = payloadString.substring(0, payloadString.length()-1);
                    sb.append(payloadString);
                }
                sb.append("]}\n");
                return sb.toString();
            }
        }
    }

}
