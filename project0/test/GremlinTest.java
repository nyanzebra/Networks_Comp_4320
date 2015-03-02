import junit.framework.TestCase;
import UDPConnection.*;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Random;

public class GremlinTest extends TestCase {

    public void testCorruptPacket() throws Exception {

        // Create packet with random byte[] data
        int Receive_Size = 12;
        byte[] buffer = new byte[Receive_Size];
        new Random().nextBytes(buffer);
        DatagramPacket packet = new DatagramPacket(buffer, Receive_Size);

        // Copy data for test reference
        byte[] oldBuffer = new byte[Receive_Size];
        System.arraycopy(buffer, 0, oldBuffer, 0, buffer.length);

        // Set Gremlin probability to 1 to ensure damage
        Gremlin.setDamageProbability(1);

        // Damage packet
        packet.setData(Gremlin.corruptPacket(packet));

        // Check if packet data is equal to original
        // If so, packet was not damaged
        assertNotSame(oldBuffer, packet.getData());

    }

    public void testGetDamageProbability() throws Exception {
        Gremlin.setDamageProbability(0.6);
        assertEquals(0.6,Gremlin.getDamageProbability());
    }

    public void testSetDamageProbability() throws Exception {
        Gremlin.setDamageProbability(0.3);
        assertEquals(0.3, Gremlin.getDamageProbability());
    }
}