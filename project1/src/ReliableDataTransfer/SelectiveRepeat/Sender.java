package ReliableDataTransfer.SelectiveRepeat;

import GenericWrappers.Pair;
import ReliableDataTransfer.Packet;
import ReliableDataTransfer.PacketBuffer;
import ReliableDataTransfer.SegmentationAndReassembly.SegmentAndReassemble;
import UDPConnection.Exception.UDPException;
import UDPConnection.UDPConnection;
import WebApplication.HTTPConnection;

import java.util.ArrayList;

public class Sender {
    public Sender(ArrayList<byte[]> packets, UDPConnection udp_connection, int window_Size, int sequence_Modulus) {
        Packet_Buffer = new PacketBuffer();
        Packet_Buffer.addAll(packets);
        Connection = udp_connection;
        Packet_Buffer.add(new Packet(SegmentAndReassemble.constructPacketFooter(Packet_Buffer.size())));
        Sequence_Modulus = sequence_Modulus;
        Window_Size = window_Size;
    }

    public Pair<Integer,Integer> receiveACK() throws UDPException {
        byte[] header = new Packet(Connection.receive()).getHeader();
        if (header[2] != 1) {
            header[0] = 2;
        }
        return new Pair<Integer, Integer>((int) header[0], (int) header[5]);
    }

    public void sendPackets() {
        try {
            while (hasPackets()) {
                sendPacketsInWindow();
                checkPacketsInWindow();
                updateWindow();
            }
            transmit(Packet_Buffer.size() - 1);
        } catch (UDPException e) {
            e.printStackTrace();
        }
    }

    private void sendPacketsInWindow() throws UDPException {
        for (int i = Window_Position; i < Window_Size + Window_Position && i < Packet_Buffer.size() - 1; ++i) {
            if (Packet_Buffer.get(i).getAcknowledgementCode() != HTTPConnection.AcknowledgementCode.Acknowledged) {
                transmit(i);
            }
        }
    }

    private void transmit(int position) throws UDPException {
        Connection.send(Packet_Buffer.get(position).toBytes());
    }

    private void checkPacketsInWindow() throws UDPException {
        for (int i = 0; i < Window_Size && i + Window_Position < Packet_Buffer.size() - 1; ++i) {
            Pair<Integer, Integer> ack = receiveACK();

            int sequence = ack.Second % Sequence_Modulus;
            HTTPConnection.AcknowledgementCode code = HTTPConnection.AcknowledgementCode.values()[ack.First];

            Packet_Buffer.get(sequence).updateAcknowledgementCode(code);
        }
    }

    private void updateWindow() throws UDPException {
        System.out.println("Window:     Update window called.");
        System.out.println("Window:     Position =  " + Window_Position);
        System.out.println("ACK:        Code = " + Packet_Buffer.get(Window_Position).getAcknowledgementCode());
        while (Packet_Buffer.get(Window_Position).getAcknowledgementCode() == HTTPConnection.AcknowledgementCode.Acknowledged) {
            System.out.println("ACK:        Received. Window Pos = " + Window_Position );
            ++Window_Position;

            if (Window_Position == Packet_Buffer.size() - 1) {
                break;
            }
        }
    }

    private boolean hasPackets() {
        return Window_Position < Packet_Buffer.size() - 1 && !Packet_Buffer.isEmpty();
    }

    private int Window_Position = 0;
    private int Window_Size = 0;
    private int Sequence_Modulus = 0;
    private UDPConnection Connection = null;
    private PacketBuffer Packet_Buffer;
}
