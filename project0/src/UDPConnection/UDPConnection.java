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

    //methods
    public void send(byte[] data) throws UDPException {
        if (m_Send_Size == -1) {
            throw new UDPException("UDPException:	packet size must be defined");
        }
        if (data == null) {
            throw new UDPException("UDPException:	sent data must not be null");
        }
        if (data.length > m_Send_Size) {
            throw new UDPException("UDPException:   data must be within specified size");
        }
        try {
            byte[] buffer = Arrays.copyOfRange(data, 0, m_Send_Size);
            m_Socket.send(new DatagramPacket(buffer, m_Send_Size, m_IP, m_Port));
            System.out.println("Confirmed:  sent packet");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receive() throws  UDPException {
        if (m_Receive_Size == -1) {
            throw new UDPException("UDPException:	packet size must be defined");
        }
        byte[] buffer = new byte[m_Receive_Size];
        DatagramPacket packet = new DatagramPacket(buffer, m_Receive_Size);
        try {
            m_Socket.receive(packet);
            System.out.println("Confirmed:  received packet");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (m_IP == null) {
            m_IP = packet.getAddress();
        }
        m_Port = packet.getPort();

        return packet.getData();
    }

    public void close() {
        m_Socket.close();
    }

    //modifiers
    public void setSendPacketSize(int size) throws UDPException {
        if (size > 0) {
            m_Send_Size = size;
        } else {
            throw new UDPException("UDPException:   size must be larger than 0");
        }
    }

    public void setReceivePacketSize(int size) throws UDPException {
        if (size > 0) {
            m_Receive_Size = size;
        } else {
            throw new UDPException("UDPException:   size must be larger than 0");
        }
    }

    protected int m_Port = -1;
    protected int m_Send_Size = -1;
    protected int m_Receive_Size = -1;
    protected DatagramSocket m_Socket = null;
    protected InetAddress m_IP = null;
}
