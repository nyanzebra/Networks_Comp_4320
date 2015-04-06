package ReliableDataTransfer;

import GenericWrappers.Pair;
import UDPConnection.Exception.UDPException;
import UDPConnection.UDPConnection;
import WebApplication.HTTPConnection;

import java.util.ArrayList;

public class PacketBuffer {
    public PacketBuffer(UDPConnection connection) {
        Connection = connection;
    }

    public Packet get(int position) {
        return Packets.get(position).First;
    }

    public ArrayList<byte[]> toArrayList() {
        ArrayList<byte[]> array_list = new ArrayList<byte[]>();

        for (Pair<Packet, Integer> pair : Packets) {
            array_list.add(pair.First.getData());
        }

        return array_list;
    }

    public void add(Packet packet) {
        int sequence_number = 0;

        if (Last_Received_Packet != null) {
            sequence_number = Last_Received_Packet.getSequenceNumber() + 1;
        }

        Packets.add(new Pair<Packet, Integer>(packet, sequence_number));

        Last_Received_Packet = packet;
    }

    private void add(byte[] packet, int sequence_number) {
        Packets.add(new Pair<Packet, Integer>(new Packet(packet, Connection), sequence_number));
        Packets.get(Packets.size() - 1).First.updateAcknowledgementCode(HTTPConnection.AcknowledgementCode.None);
        Last_Received_Packet = Packets.get(Packets.size() - 1).First;
    }

    public boolean hasPacket(int sequence_number) {
        return sequence_number < Packets.size();
    }

    public Packet receivePacket() throws UDPException {
        return new Packet (Connection.receive(), Connection);
    }

    public void update(Packet packet) {
        Packets.get(packet.getSequenceNumber()).First = packet;
    }

    public int size() {
        return Packets.size();
    }

    public void sendACKForLastReceived() throws UDPException {
        Last_Received_Packet.Acknowledgement();
    }

    public boolean isEmpty() {
        return Packets.isEmpty();
    }

    public void addAll(ArrayList<byte[]> packets, int sequence_modulus) {
        for (int i = 0; i < packets.size(); ++i) {
            add(packets.get(i), i % sequence_modulus);
        }
    }

    private UDPConnection Connection;
    private Packet Last_Received_Packet = null;
    private ArrayList<Pair<Packet,Integer>> Packets = new ArrayList<Pair<Packet, Integer>>();
}
