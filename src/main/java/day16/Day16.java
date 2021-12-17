package day16;

import utils.StringConsumer;
import utils.Utils;

import java.math.BigDecimal;
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
        String hex = Utils.streamLinesForDay(DAY).collect(Collectors.joining("\n"));
        String bin = hex2bin(hex);
        System.out.println(bin);

        ParsedPacket packet = new ParsedPacket(new StringConsumer(bin));
        System.out.println(packet);

        long ans = sumVersions(packet);
        System.out.println("Part 1 ANS: " + ans);
    }
    private static long sumVersions(ParsedPacket packet) {
        long sum = packet.version;
        for (ParsedPacket sp : packet.payload) {
            sum += sumVersions(sp);
        }
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
        public Integer payloadLength;

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
                payloadLength = Integer.parseInt(sc.consume(15), 2);
                packetCount = null;
                String payloadString = sc.consume(payloadLength);
                StringConsumer sc2 = new StringConsumer(payloadString);
                while (sc2.hasNext()) {
                    payload.add(new ParsedPacket(sc2));
                }
            } else {
                packetCount = Integer.parseInt(sc.consume(11), 2);
                for (int i = 0; i < packetCount; i++) {
                    payload.add(new ParsedPacket(sc));
                }
            }

            // calculate literals
            switch (typeId) {
                case 0 -> literal = payload.stream().map(p -> p.literal).reduce(BigInteger.ZERO, BigInteger::add);
                case 1 -> literal = payload.stream().map(p -> p.literal).reduce(BigInteger.ONE, BigInteger::multiply);
                case 2 -> literal = payload.stream().map(p -> p.literal).reduce(BigInteger.valueOf(Long.MAX_VALUE), BigInteger::min);
                case 3 -> literal = payload.stream().map(p -> p.literal).reduce(BigInteger.valueOf(Long.MIN_VALUE), BigInteger::max);
                case 5 -> literal = BigInteger.valueOf((payload.get(0).literal.compareTo(payload.get(1).literal) > 0) ? 1 : 0);
                case 6 -> literal = BigInteger.valueOf((payload.get(0).literal.compareTo(payload.get(1).literal) < 0) ? 1 : 0);
                case 7 -> literal = BigInteger.valueOf((payload.get(0).literal.compareTo(payload.get(1).literal) == 0) ? 1 : 0);
            }
        }

        public boolean isLiteral() {
            return nonNull(literal);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("ParsedPacket{v").append(version).append(", type=").append(typeId).append(", literal=").append(literal)/*.append(", packetCount=").append(packetCount).append(", payloadLength=").append(payloadLength)*/.append(", payload=[");
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
