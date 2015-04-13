package UDPConnection;

import ReliableDataTransfer.Packet;

import java.net.DatagramPacket;
import java.util.Random;

/**
 * Created by jonathanhart on 2/24/15.
 */
public class Gremlin {

    private static double damageProbability = 0;
    private static double dropProbability = 0;

    public static byte[] corruptPacket(DatagramPacket packet) {
        // Get data from packet
        byte[] data = packet.getData();

        // Generate number between 0 and 1
        double randomNum = Math.random();
        if (randomNum < damageProbability) {
            // If random() is in probability range, damage packet.
            System.out.println("Gremlin:    Packet selected to be damaged. (Probability: " + damageProbability + ")");
            return corruptBytes(data);
        }

        return data;
    }

    public static boolean dropPacket() {
        // Generate number between 0 and 1
        double randomNum = Math.random();
        if (randomNum < dropProbability) {
            // If random() is in probability range, damage packet.
            System.out.println("Gremlin:    Packet selected to be dropped. (Probability: " + dropProbability + ")");
            //return fillBytes(data);
            return true;
        }

        return false;
    }

    public static void setDamageProbability(double newProbability) { damageProbability = newProbability;}
    public static void setDropProbability(double newProbability) {
        dropProbability = newProbability;
    }

    private static byte[] corruptBytes(byte[] data) {

        // Determine how many time to damage the packet data
        double timesProbability = Math.random();
        int timesToDamage;

        if (timesProbability <= 0.5) {
            timesToDamage = 1;
        } else if (timesProbability > 0.5 && timesProbability <= 0.8) {
            timesToDamage = 2;
        } else {
            timesToDamage = 3;
        }

        Packet packet = new Packet(data);

        System.out.println("Gremlin:    Packet " + packet.getSequenceNumber() + ": " + " damaged " + timesToDamage + " time(s)");

        // Damage the packet at a random index in the data
        Random random = new Random();
        int randomIndex;
        int[] prevIndexes = {-1, -1, -1};

        for (int i = 0; i <= timesToDamage - 1; i++) {
            // Ensure the same index is not damaged twice.
            do {
                randomIndex = random.nextInt(packet.getData().length);
            } while (randomIndex == prevIndexes[0] || randomIndex == prevIndexes[1] || randomIndex == prevIndexes[2]);

            prevIndexes[i] = randomIndex;

            // Fill data at selected index with damaged byte
            packet.getData()[randomIndex] = generateRandomByte();
        }
        return packet.toBytes();
    }

    private static byte[] fillBytes(byte[] data) {
        byte [] ret = new byte[data.length];
        return ret;
    }

    private static byte generateRandomByte() {
        Random randomNum = new Random();
        return (byte)randomNum.nextInt();
    }

}
