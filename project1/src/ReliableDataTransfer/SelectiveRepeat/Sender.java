package ReliableDataTransfer.SelectiveRepeat;

import ReliableDataTransfer.Packet;
import ReliableDataTransfer.PacketBuffer;
import ReliableDataTransfer.SegmentationAndReassembly.SegmentAndReassemble;
import UDPConnection.Exception.UDPException;
import UDPConnection.UDPConnection;
import WebApplication.HTTPConnection;

import java.util.ArrayList;

public class Sender {
    public Sender(ArrayList<byte[]> packets, UDPConnection udp_connection, int window_Size, int sequence_Modulus) {
        Packet_Buffer = new PacketBuffer(udp_connection);
        Packet_Buffer.addAll(packets, sequence_Modulus);
        Packet_Buffer.add(new Packet(SegmentAndReassemble.constructPacketFooter(Packet_Buffer.size()), udp_connection));
        Sequence_Modulus = sequence_Modulus;
        Window_Size = window_Size;
    }

    public void sendPackets() {
        try {
            while (hasPackets()) {
                sendPacketsInWindow();
                checkPacketsInWindow();
                updateWindow();
            }
            Packet_Buffer.get(Packet_Buffer.size() - 1).Transmit();
        } catch (UDPException e) {
            e.printStackTrace();
        }
    }

    private void sendPacketsInWindow() throws UDPException {
        for (int i = Window_Position; i < Window_Size && i < Packet_Buffer.size() - 1; ++i) {
            if (Packet_Buffer.get(i).getAcknowledgementCode() != HTTPConnection.AcknowledgementCode.Acknowledged) {
                Packet_Buffer.get(i).Transmit();
            }
        }
    }

    private void checkPacketsInWindow() throws UDPException {
        for (int i = 0; i < Window_Size && i < Packet_Buffer.size() - 1; ++i) {
            Packet packet = Packet_Buffer.receivePacket();

            int sequence = packet.getSequenceNumber() % Sequence_Modulus;
            HTTPConnection.AcknowledgementCode code = packet.getAcknowledgementCode();

            updatePacketInWindow(sequence, code);
        }
    }

    private void updatePacketInWindow(int sequence, HTTPConnection.AcknowledgementCode code) {
        for (int i = Window_Position; i < Window_Size && i < Packet_Buffer.size() - 1; ++i) {
            if (sequence == Packet_Buffer.get(i).getSequenceNumber() % Sequence_Modulus) {
                Packet_Buffer.get(i).updateAcknowledgementCode(code);
            }
        }
    }

    private void updateWindow() throws UDPException {
        while (Packet_Buffer.get(Window_Position).getAcknowledgementCode() == HTTPConnection.AcknowledgementCode.Acknowledged) {
            ++Window_Position;

            if (Window_Position == Packet_Buffer.size()) {
                break;
            }
        }
    }

    private boolean hasPackets() {
        return Window_Position < Packet_Buffer.size() && !Packet_Buffer.isEmpty();
    }

    private int Window_Position = 0;
    private int Window_Size = 0;
    private int Sequence_Modulus = 0;
    private volatile PacketBuffer Packet_Buffer;
}
