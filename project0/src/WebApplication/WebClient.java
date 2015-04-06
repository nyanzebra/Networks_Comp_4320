package WebApplication;


import UDPConnection.Exception.UDPException;
import UDPConnection.UDPClient;
import WebApplication.Exception.WebException;

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
public class WebClient extends HTTPConnection {
    public WebClient(String ip) {
        try {
            UDP_Connection = new UDPClient(InetAddress.getByName(ip));
        } catch (UDPException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void request(String command, String file) throws UDPException, IOException, WebException {
        if (Objects.equals(command.toLowerCase(), "get")) {
            String request = "GET " + file + " HTTP/1.0";
            UDP_Connection.send(request.getBytes());
            byte[] header = UDP_Connection.receive();
            headerInfo(header);
            Data = new byte[File_Size];
            constructFile();
        }
    }

    public void printFile() {
        String s = new String(Data);
        System.out.println(s);
    }

    private void constructFile() throws UDPException, IOException, WebException {
        byte[] packet = UDP_Connection.receive();
        do {
            int order = orderOfPacketInMessage(Arrays.copyOfRange(packet, 1, 5));
            byte parity = packet[0];
            if (parity != 'A') {
                throw new WebException("ERROR:  packet has been corrupted");
            }
            //if (packet[6] != calculateChecksum(Arrays.copyOfRange(packet,5,packet.length))) {
            //    throw new WebException("ERROR:  packet has been corrupted");
            //}

            byte[] data = Arrays.copyOfRange(packet, 6, packet.length);
            if (data.length > Data.length) {
                System.arraycopy(data, 0, Data, order * data.length, Data.length);
            } else {
                System.arraycopy(data, 0, Data, order * data.length, data.length);
            }
            packet = UDP_Connection.receive();
        } while (packet[7] != '\0');

        FileOutputStream html = new FileOutputStream(File_Name);
        html.write(Data);
        html.close();
    }

    private int orderOfPacketInMessage(byte[] array) {
        return (array[0] << 24)| ((array[1] << 24) >> 8) | ((array[2] << 24) >> 16) | ((array[3] << 24) >> 24);
    }

    private void headerInfo(byte[] header) {
        String info = new String(header).replaceAll("\0", "");
        info = info.substring(56, info.length());
        String size = info.substring(info.indexOf(' ') + 1, info.indexOf('\r'));
        File_Size = Integer.parseInt(size);
        File_Name = info.substring(info.lastIndexOf('\n'), info.length()).replaceAll("\n", "");
    }

    private String File_Name;
    private byte[] Data;
    private int File_Size;
}
