package UDPConnection;
import java.net.DatagramPacket;
import java.util.Random;

/**
 * Created by jonathanhart on 2/24/15.
 */
public class Gremlin {

    public static byte[] corruptPacket(DatagramPacket packet, double damageProbability) {
        // Get data from packet
        byte[] data = packet.getData();

        // Generate number between 0 and 1
        if (Math.random() <= damageProbability) {
            // If random() is in probability range, damage packet.
            System.out.println("Packet selected to be damaged");
            return corruptBytes(data);
        }

        return data;
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

        System.out.println("Packet damaged " + timesToDamage + " time(s)");

        // Damage the packet at a random index in the data
        Random random = new Random();
        int randomIndex;
        int[] prevIndexes = {-1, -1, -1};

        for (int i = 0; i <= timesToDamage - 1; i++) {
            // Ensure the same index is not damaged twice.
            do {
                randomIndex = random.nextInt(256);
            } while (randomIndex == prevIndexes[0] || randomIndex == prevIndexes[1] || randomIndex == prevIndexes[2]);

            prevIndexes[i] = randomIndex;

            // Fill data at selected index with damaged byte
            System.out.println("Clean data byte:" + data[randomIndex]);
            data[randomIndex] = generateRandomByte();
            System.out.println("Corrupted data byte:" + data[randomIndex]);
        }
        return data;
    }

    private static byte generateRandomByte() {
        Random randomNum = new Random();
        return (byte)randomNum.nextInt();
    }
}
