package ReliableDataTransfer.SegmentationAndReassembly;

import GenericWrappers.Pair;
import ReliableDataTransfer.ErrorDetection.ErrorDetection;
import UDPConnection.Exception.UDPException;
import WebApplication.Exception.WebException;
import WebApplication.HTTPConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Robert
 * @date 05-Apr-15.
 */
public class SegmentAndReassemble {
    private static final int Header_Size = 6;

    public static ArrayList<byte[]> segment(byte[] message, int send_packet_size) {
        ArrayList<byte[]> message_packets = new ArrayList<byte[]>();
        int data_size = send_packet_size - Header_Size;
        for (int i = 0; i < message.length - 1; i += send_packet_size) {
            byte[] packet_message = Arrays.copyOfRange(message, i, i + data_size);
            message_packets.add(constructPacketMessage(packet_message, i / send_packet_size, HTTPConnection.AcknowledgementCode.None));
        }
        return  message_packets;
    }

    public static byte[] constructObjectHeaderAcknowledgement() {
        byte[] ack = new byte[6];
        ack[0] = (byte) HTTPConnection.AcknowledgementCode.Acknowledged.ordinal();
        ack[1] = 0;
        ack[2] = 0;
        ack[3] = 0;
        ack[4] = 0;
        ack[5] = 0;
        return ack;
    }

    protected static byte[] constructPacketHeader(int order, int checksum, HTTPConnection.AcknowledgementCode acknowledgement_code) {
        byte[] header = new byte[6];
        header[0] = (byte) acknowledgement_code.ordinal();
        header[1] = (byte) (order >> 24);
        header[2] = (byte) ((order << 8) >> 24);
        header[3] = (byte) ((order << 16) >> 24);
        header[4] = (byte) ((order << 24) >> 24);
        header[5] = (byte) (checksum);
        return header;
    }

    public static byte[] constructPacketFooter(int order) {
        return constructPacketHeader(order, 0, HTTPConnection.AcknowledgementCode.None);
    }

    public static int orderOfPacketInMessage(byte[] array) {
        return (array[0] << 24)| ((array[1] << 24) >> 8) | ((array[2] << 24) >> 16) | ((array[3] << 24) >> 24);
    }

    private static byte[] constructPacketMessage(byte[] message, int order, HTTPConnection.AcknowledgementCode code) {
        byte[] header = constructPacketHeader(order, ErrorDetection.calculateChecksum(message), code);
        byte[] packet_message = new byte[message.length + header.length];
        System.arraycopy(header, 0, packet_message, 0, header.length);
        System.arraycopy(message, 0, packet_message, header.length, message.length);
        return packet_message;
    }

    public static byte[] reassemble(ArrayList<byte[]> packets, int file_size) throws UDPException, IOException, WebException {
        byte[] data = new byte[file_size];

        for (int i = 0; i < packets.size() - 1; ++i) {
            byte[] packet = removeNullBytes(packets.get(i));

            System.arraycopy(packet, 0, data, i * packet.length, packet.length);
        }

         return data;
    }

    private static byte[] removeNullBytes(byte[] array) {
        ArrayList<Byte> list = new ArrayList<Byte>();

        for (byte b : array) {
            if (b != 0) {
                list.add(b);
            }
        }

        byte[] resultant = new byte[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            resultant[i] = list.get(i);
        }

        return resultant;
    }

    public static Pair<String, Integer> headerInfo(byte[] header) {
        String info = new String(header).replaceAll("\0", "");
        info = info.substring(56, info.length());
        String size = info.substring(info.indexOf(' ') + 1, info.indexOf('\r'));

        int file_size = Integer.parseInt(size);
        String file_name = info.substring(info.lastIndexOf('\n'), info.length()).replaceAll("\n", "");

        return new Pair<String, Integer>(file_name, file_size);
    }
}
