package UDPConnection;

import UDPConnection.Exception.UDPException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class UDPConnection {

    public void send(byte[] data) throws UDPException {
        if (Send_Size == -1) {
            throw new UDPException("UDPException:	packet size must be defined");
        }
        if (data == null) {
            throw new UDPException("UDPException:	sent data must not be null");
        }
        if (data.length > Send_Size) {
            throw new UDPException("UDPException:   data must be within specified size");
        }
        if (Receive_Size < Send_Size) {
            throw new UDPException("UDPException:   packet receive size must be at least the size of packet send size");
        }
        try {
            byte[] buffer = Arrays.copyOfRange(data, 0, Send_Size);
            Socket.send(new DatagramPacket(buffer, Send_Size, IP, Port));
            System.out.println("Confirmed:  sent packet");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPort(int port) throws UDPException {
        if (port > 9999 || port < 0) {
            throw new UDPException("UDPException:	Port must be within valid range: 0 - 9999");
        }
        Port = port;
    }

    public byte[] receive() throws  UDPException {
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

    public void close() {
        Socket.close();
    }

    public void setSendPacketSize(int size) throws UDPException {
        if (size > 0) {
            Send_Size = size;
        } else {
            throw new UDPException("UDPException:   size must be larger than 0");
        }
    }

    public void setReceivePacketSize(int size) throws UDPException {
        if (size > 0) {
            Receive_Size = size;
        } else {
            throw new UDPException("UDPException:   size must be larger than 0");
        }
    }

    public int getSendPacketSize() {
        return Send_Size;
    }

    public int getReceivePacketSize() {
        return Receive_Size;
    }

    public int getPort() {
        return Port;
    }

    protected int Port = -1;
    protected int Send_Size = -1;
    protected int Receive_Size = -1;
    protected DatagramSocket Socket = null;
    protected InetAddress IP = null;
}
