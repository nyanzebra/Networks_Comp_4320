package ReliableDataTransfer;

import GenericWrappers.Pair;
import WebApplication.HTTPConnection;

import java.util.ArrayList;

public class PacketBuffer {
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
        Packets.add(new Pair<Packet, Integer>(packet, packet.getSequenceNumber()));
    }

    private void add(byte[] packet, int sequence_number) {
        Packets.add(new Pair<Packet, Integer>(new Packet(packet), sequence_number));
    }

    public boolean hasPacket(int sequence_number) {
        return sequence_number < Packets.size();
    }

    public void update(Packet packet) {
        Packets.get(packet.getSequenceNumber()).First = packet;
    }

    public int size() {
        return Packets.size();
    }

    public boolean isEmpty() {
        return Packets.isEmpty();
    }

    public void addAll(ArrayList<byte[]> packets) {
        for (int i = 0; i < packets.size(); ++i) {
            add(packets.get(i), i);
            Packets.get(Packets.size() - 1).First.updateAcknowledgementCode(HTTPConnection.AcknowledgementCode.None);
        }
    }

    private ArrayList<Pair<Packet,Integer>> Packets = new ArrayList<Pair<Packet, Integer>>();
}
