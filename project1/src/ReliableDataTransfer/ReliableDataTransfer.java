package ReliableDataTransfer;

import ReliableDataTransfer.SegmentationAndReassembly.SegmentAndReassemble;
import ReliableDataTransfer.SelectiveRepeat.Receiver;
import ReliableDataTransfer.SelectiveRepeat.Sender;
import UDPConnection.Exception.UDPException;
import UDPConnection.UDPClient;
import UDPConnection.UDPConnection;
import UDPConnection.UDPServer;
import WebApplication.Exception.WebException;
import WebApplication.HTTPConnection;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * @author Robert
 * @date 05-Apr-15.
 */
public class ReliableDataTransfer {
    public ReliableDataTransfer(String ip, int port) throws UDPException, UnknownHostException {
        if (ip != null) {
            UDP_Connection = new UDPClient(InetAddress.getByName(ip));
        } else if (port != -1) {
            UDP_Connection = new UDPServer(port);
        }
    }

    public void sendMessage(int window_size, int sequence_modulus) throws InterruptedException, UDPException, IOException {
        FileInputStream file_input_stream = new FileInputStream(File_To_Send.getAbsolutePath());

        byte[] file_data = new byte[(int) File_To_Send.length()];

        file_input_stream.read(file_data);

        ArrayList<byte[]> byte_message = SegmentAndReassemble.segment(file_data, UDP_Connection.getSendPacketSize());
        Sender Send_Side = new Sender(byte_message, UDP_Connection, window_size, sequence_modulus);
        Send_Side.sendPackets();
    }

    public byte[] receiveMessage(int file_size) throws UDPException, WebException, IOException {
        Receiver Receive_Side = new Receiver(UDP_Connection);
        Receive_Side.receivePackets();
        PacketBuffer message_buffer = Receive_Side.getPacketBuffer();
        return SegmentAndReassemble.reassemble(message_buffer.toArrayList(), file_size);
    }

    public void sendRequest(String request) throws UDPException {
        UDP_Connection.send(request.getBytes());
    }

    public void sendObjectHeader() throws UDPException {
        UDP_Connection.send(constructHTTPObjectHeader(File_To_Send.getName(), (int) File_To_Send.length()).getBytes());
    }

    public byte[] receiveObjectHeader() throws UDPException {
        return UDP_Connection.receive();
    }

    public byte[] receiveRequest() throws UDPException {
        return UDP_Connection.receive();
    }

    public void setFile(String file_location, String root_directory) {
        File_To_Send = new File(root_directory + "/" + file_location);
    }

    private String constructHTTPObjectHeader(String name, int length) {
        return "HTTP/1.0 200 Document Follows\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + Integer.toString(length) + "\r\n" +
                "\r\n" +
                name;
    }

    public HTTPConnection.AcknowledgementCode receiveObjectHeaderAcknowledgement() throws UDPException {
        return HTTPConnection.AcknowledgementCode.values()[UDP_Connection.receive()[0]];
    }

    public void sendObjectHeaderAcknowledgement() throws UDPException {
        UDP_Connection.send(SegmentAndReassemble.constructObjectHeaderAcknowledgement());
    }

    public UDPConnection getUDPConnection() {
        return UDP_Connection;
    }

    private UDPConnection UDP_Connection;
    private File File_To_Send;
}
