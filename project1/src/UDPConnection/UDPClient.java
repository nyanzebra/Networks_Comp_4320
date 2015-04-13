package UDPConnection;

import UDPConnection.Exception.UDPException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class UDPClient extends UDPConnection {

    public UDPClient(InetAddress ip) throws UDPException {
        IP = ip;
        try {
            Socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
	}

    @Override
    public byte[] receive() throws UDPException {
        if (Receive_Size == -1) {
            throw new UDPException("UDPException:	packet size must be defined");
        }
        if (Receive_Size < Send_Size) {
            throw new UDPException("UDPException:   packet receive size must be at least the size of packet send size");
        }

        byte[] buffer = new byte[Receive_Size];
        DatagramPacket packet = new DatagramPacket(buffer, Receive_Size);
        try {
            Socket.receive(packet);

            // Damage packet randomly using Gremlin

            packet.setData(Gremlin.corruptPacket(packet));


            System.out.println("Confirmed:  received packet");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (IP == null) {
            IP = packet.getAddress();
        }
        Port = packet.getPort();

        return packet.getData();
    }
}