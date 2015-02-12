package WebApplication;


import UDPConnection.Exception.UDPException;
import UDPConnection.UDPClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class WebClient {
    public WebClient(String ip, int port, int receive_size, int send_size) {
        try {
            m_Client = new UDPClient(InetAddress.getByName(ip), port);
            m_Client.setReceivePacketSize(receive_size);
            m_Client.setSendPacketSize(send_size);
        } catch (UDPException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void request(String command, String file) throws UDPException, IOException {
        if (Objects.equals(command.toLowerCase(), "get")) {
            String request = "GET " + file + " HTTP/1.0";
            m_Client.send(request.getBytes());
            byte[] header = m_Client.receive();
            headerInfo(header);
            m_Data = new byte[m_File_Size];
            constructFile();
        }
    }

    public void printFile() {
        String s = new String(m_Data);
        System.out.println(s);
    }

    private void constructFile() throws UDPException, IOException {
        byte[] packet = m_Client.receive();
        do {
            int order = byteArrayPlacement(Arrays.copyOfRange(packet, 1, 5));
            byte parity = packet[0];
            if (parity != 'A') {
                System.out.println("Corrupted!");
            }

            byte[] data = Arrays.copyOfRange(packet, 6, packet.length);
            if (data.length > m_Data.length) {
                System.arraycopy(data, 0, m_Data, order * data.length, m_Data.length);
            } else {
                System.arraycopy(data, 0, m_Data, order * data.length, data.length);
            }
            packet = m_Client.receive();
        } while (packet[7] != '\0');

        FileOutputStream html = new FileOutputStream(m_File_Name);
        html.write(m_Data);
        html.close();
    }

    private int byteArrayPlacement(byte[] array) {
        return (array[0] << 24)| ((array[1] << 24) >> 8) | ((array[2] << 24) >> 16) | ((array[3] << 24) >> 24);
    }

    private void headerInfo(byte[] header) {
        String info = new String(header).replaceAll("\0", "");
        info = info.substring(56, info.length());
        String size = info.substring(info.indexOf(' ') + 1, info.indexOf('\r'));
        m_File_Size = Integer.parseInt(size);
        m_File_Name = info.substring(info.lastIndexOf('\n'), info.length()).replaceAll("\n", "");
    }
    private String m_File_Name;
    private byte[] m_Data;
    private int m_File_Size;
    private UDPClient m_Client;
}
