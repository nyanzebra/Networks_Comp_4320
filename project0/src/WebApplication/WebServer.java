package WebApplication;

import UDPConnection.Exception.UDPException;
import UDPConnection.UDPServer;
import WebApplication.Exception.WebException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class WebServer extends HTTPConnection {
    public WebServer(int port) {
        try {
            UDP_Connection = new UDPServer(port);
        } catch (UDPException e) {
            e.printStackTrace();
        }
    }

    public void listen() throws UDPException, IOException, WebException {
        while (true) {
            Message = UDP_Connection.receive();
            if (isRequest()) {
                respond();
            }
        }
    }

    private void respond() throws IOException, UDPException, WebException {
        String file_location = findFileLocation();
        sendHTTPObjectHeader(file_location);

        byte[] message = new byte[(int) fileToSend(file_location).length()];

        fileInputStream(file_location).read(message);
        /*if (message.length < 1024 * 10) {
            //exception
            throw new WebException("WebException:   file must be greater than 10kb in size");
        }*/
        sendPackets(message);

        UDP_Connection.send(constructPacketFooter());
    }

    private File fileToSend(String file_location) {
        return new File(Root_Directory + file_location);
    }

    private FileInputStream fileInputStream(String file_location) throws FileNotFoundException {
        return new FileInputStream(Root_Directory + file_location);
    }

    private void sendHTTPObjectHeader(String file_location) throws UDPException {
        int file_length = (int) fileToSend(file_location).length();
        String file_name = fileToSend(file_location).getName();
        String http_object_header = constructHTTPObjectHeader(file_name, file_length);
        UDP_Connection.send(http_object_header.getBytes());
    }

    private String findFileLocation() {
        String file_location = new String(Message);
        return file_location.substring(file_location.indexOf(' ') + 1, file_location.lastIndexOf(' '));
    }

    private byte[] constructPacketFooter() {
        byte[] footer = new byte[7];
        System.arraycopy(constructPacketHeader(0), 0, footer, 0, 6); // this is to meet requirement of null terminated message
        return footer;
    }

    private void sendPackets(byte[] message) throws UDPException {
        int send_size = UDP_Connection.getSendPacketSize();
        int data_size = send_size - Header_Size;
        for (int i = 0; i < message.length - 1; i += send_size) {
            byte[] packet_message = Arrays.copyOfRange(message, i, i + data_size);
            UDP_Connection.send(constructPacketMessage(packet_message, i / send_size));
        }
    }

    private byte[] constructPacketMessage(byte[] message, int order) {
        byte[] header = constructPacketHeader(order);
        byte[] packet_message = new byte[message.length + header.length];
        System.arraycopy(header, 0, packet_message, 0, header.length);
        System.arraycopy(message, 0, packet_message, header.length, message.length);
        return packet_message;
    }

    private String constructHTTPObjectHeader(String name, int length) {
        return "HTTP/1.0 200 Document Follows\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + Integer.toString(length) + "\r\n" +
                "\r\n" +
                name;
    }

    private byte[] constructPacketHeader(int order) {
        byte[] header = new byte[6];
        header[0] = (Parity);
        header[1] = (byte) (order >> 24);
        header[2] = (byte) ((order << 8) >> 24);
        header[3] = (byte) ((order << 16) >> 24);
        header[4] = (byte) ((order << 24) >> 24);
        header[5] = (byte) (0); //this can be boolean values for whatever...
        return header;
    }

    private boolean isRequest() {
        if (Message == null) {
            return false;
        }
        String request = new String(Message);
        return request.contains("GET");
    }

    private static final byte Parity = 'A'; //1010
    private byte[] Message;
    private static final int Header_Size = 6;
}
