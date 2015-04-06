package ReliableDataTransfer.SelectiveRepeat;

import ReliableDataTransfer.Packet;
import ReliableDataTransfer.PacketBuffer;
import UDPConnection.Exception.UDPException;
import UDPConnection.UDPConnection;
import WebApplication.HTTPConnection;

/**
 * @author Robert
 * @date 05-Apr-15.
 */
public class Receiver {

    public Receiver(UDPConnection udp_connection) {
        Packet_Buffer = new PacketBuffer(udp_connection);
    }

    public void ReceivePackets() throws UDPException {
        while (!Should_End_Reception) {
            receivePacket();
        }
    }

    private void receivePacket() throws UDPException {
        Packet packet = Packet_Buffer.receivePacket();
        if (packet.getAcknowledgementCode() != HTTPConnection.AcknowledgementCode.None) {
            if (packet.isLastPacket()) {
                Should_End_Reception = true;
            }
            if (Packet_Buffer.hasPacket(packet.getSequenceNumber())) {
                if (Packet_Buffer.get(packet.getSequenceNumber()).getAcknowledgementCode() == HTTPConnection.AcknowledgementCode.Not_Acknowledged) {
                    Packet_Buffer.update(packet);
                    sendResponse();
                }
            } else {
                Packet_Buffer.add(packet);
                sendResponse();
            }
        }
    }

    private void sendResponse() throws UDPException {
        Packet_Buffer.sendACKForLastReceived();
    }

    public PacketBuffer getPacketBuffer() {
        return Packet_Buffer;
    }

    private boolean Should_End_Reception = false;
    private volatile PacketBuffer Packet_Buffer;
}
