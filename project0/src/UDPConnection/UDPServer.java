package UDPConnection;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

import UDPConnection.Exception.UDPException;

public class UDPServer {
	//public
	//constructor
	public UDPServer(int port) throws  UDPException {
		if (port > 9999 || port < 0) {
			throw new UDPException("Exception:	Port must be within valid range: 0 - 9999");
		}

		m_Port = port;

        try {
            m_Socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

	//modifiers
	public void setPort(int port) {	m_Port = port; }

	public int getPort() { return m_Port; }

	public void setIP(InetAddress address) { m_IP = address; }

	public InetAddress getIP() { return m_IP; }

	public void setSendPacketSize(int size) {
		if (size > 0) {
			m_SendData = new byte[size];
		}
	}

	public void setReceivePacketSize(int size) {
		if (size > 0) {
			m_ReceiveData = new byte[size];
		}
	}

	//methods
	public void send(byte[] data) throws UDPException {
		if (m_SendData == null) {
			throw new UDPException("Exception:	Buffer must not be null and packet size must be defined");
		}
		if (data == null) {
			throw new UDPException("Exception:	Sent data must not be null");
		}

		if (data.length > m_SendData.length) {
			m_SendData = Arrays.copyOfRange(data, 0, m_SendData.length);
            try {
                m_Socket.send(new DatagramPacket(m_SendData, m_SendData.length, m_IP, m_Port));
            } catch (IOException e) {
                e.printStackTrace();
            }
            send(Arrays.copyOfRange(data, m_SendData.length, data.length));
		} else {
			m_SendData = data;
            try {
                m_Socket.send(new DatagramPacket(m_SendData, m_SendData.length, m_IP, m_Port));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	public byte[] receive()  throws UDPException {
		if (m_ReceiveData == null) {
            throw new UDPException("Exception:	Buffer must not be null and packet size must be defined");
        }
		DatagramPacket packet = new DatagramPacket(m_ReceiveData, m_ReceiveData.length);
        try {
            m_Socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        m_Port = packet.getPort();
		m_IP = packet.getAddress();
		return packet.getData();
	}

	//protected

	//private
	private String m_Host = "";
	private int m_Port = -1;

	private byte[] m_SendData = null;
	private byte[] m_ReceiveData = null;

	private DatagramSocket m_Socket = null;
	private InetAddress m_IP = null;
}