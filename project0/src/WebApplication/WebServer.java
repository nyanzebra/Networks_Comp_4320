package WebApplication;

import UDPConnection.Exception.UDPException;
import UDPConnection.Exception.WebException;
import UDPConnection.UDPServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class WebServer {
    public WebServer(int port, int receive_size, int send_size) {
        try {
            m_Server = new UDPServer(port);
            m_Server.setReceivePacketSize(receive_size);
            m_Server.setSendPacketSize(send_size);
        } catch (UDPException e) {
            e.printStackTrace();
        }
    }

    public void listen() throws UDPException, IOException, WebException {
        while (true) {
            m_Message = m_Server.receive();
            if (isRequest()) {
                respond();
            }
        }
    }

    public void setRootDirectory(String directory) {
        m_Root_Directory = directory;
    }
    //400 = badrequest
    private void respond() throws IOException, UDPException, WebException {
        String file_location = new String(m_Message);
        file_location = file_location.substring(file_location.indexOf(' ') + 1, file_location.lastIndexOf(' '));
        FileInputStream html = new FileInputStream(m_Root_Directory + file_location);
        File file = new File(m_Root_Directory + file_location);
        m_Server.send(objectHeader(file.getName(), (int) file.length()).getBytes());
        byte[] message = new byte[(int) file.length()];
        html.read(message);
        /*if (message.length < 1024 * 10) {
            //exception
            throw new WebException("WebException:   file must be greater than 10kb in size");
        }*/

        for (int i = 0; i < message.length - 1; i += 256) {
            byte[] packet_message = Arrays.copyOfRange(message, i, i + 250);
            m_Server.send(packetMessage(packet_message, i / 256));
        }
        byte[] footer = new byte[7];
        System.arraycopy(packetHeader(0), 0, footer, 0, 6);
        m_Server.send(footer);
    }

    private byte[] packetMessage(byte[] message, int order) {
        byte[] header = packetHeader(order);
        byte[] packet_message = new byte[message.length + header.length];
        System.arraycopy(header, 0, packet_message, 0, header.length);
        System.arraycopy(message, 0, packet_message, header.length, message.length);
        return packet_message;
    }

    private String objectHeader(String name, int length) {
        return "HTTP/1.0 200 Document Follows\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + Integer.toString(length) + "\r\n" +
                "\r\n" +
                name;
    }

    private byte[] packetHeader(int order) {
        byte[] header = new byte[6];
        header[0] = (m_Parity);
        header[1] = (byte) (order >> 24);
        header[2] = (byte) ((order << 8) >> 24);
        header[3] = (byte) ((order << 16) >> 24);
        header[4] = (byte) ((order << 24) >> 24);
        header[5] = (byte) (0); //this can be boolean values for whatever...
        return header;
    }

    private boolean isRequest() {
        if (m_Message == null) {
            return false;
        }
        String request = new String(m_Message);
        return request.contains("GET");
    }

    private static final byte m_Parity = 'A'; //1010
    private byte[] m_Message;
    private UDPServer m_Server;
    private String m_Root_Directory = "";
}
