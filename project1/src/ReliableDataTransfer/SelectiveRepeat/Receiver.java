package ReliableDataTransfer.SelectiveRepeat;

import ReliableDataTransfer.Packet;
import ReliableDataTransfer.PacketBuffer;
import UDPConnection.Exception.UDPException;
import UDPConnection.UDPConnection;
import UDPConnection.Gremlin;
import WebApplication.HTTPConnection;

/**
 * @author Robert
 * @date 05-Apr-15.
 */
public class Receiver {

    public Receiver(UDPConnection udp_connection) {
        Packet_Buffer = new PacketBuffer();
        Connection = udp_connection;
    }

    public void receivePackets() throws UDPException {
        long startTime = System.currentTimeMillis();

        while (!Should_End_Reception) {
            sendAcknowledgement();
            if ((stopTime - startTime) >= timeout) {
                System.out.println("Timeout:    The client has timed out.");
                System.out.println("Timeout:    Set to " + timeout + "ms.");
                System.exit(1);
            }
        }
    }

    public Packet receivePacket() throws UDPException {
        Packet retPacket = null;
        retPacket = new Packet(Connection.receive());

        return retPacket;
    }

    private void sendAcknowledgement() throws UDPException {

        if (Gremlin.dropPacket()) {
            System.out.println("Gremlin:    Packet has been dropped.");
            stopTime = System.currentTimeMillis();
            return;
        } else {
            Packet packet = receivePacket();

            System.out.println("ACK:        Code for received packet #" + packet.getSequenceNumber() + " = " + packet.getAcknowledgementCode());

            if (packet.getAcknowledgementCode() == HTTPConnection.AcknowledgementCode.Not_Acknowledged) {
                sendResponse(packet);
            } else {
                if (packet.isLastPacket()) {
                    Should_End_Reception = true;
                }
                int sequence_number = packet.getSequenceNumber();
                if (Packet_Buffer.hasPacket(sequence_number)) {
                    if (Packet_Buffer.get(sequence_number).getAcknowledgementCode() == HTTPConnection.AcknowledgementCode.Not_Acknowledged) {
                        Packet_Buffer.update(packet);
                        sendResponse(packet);
                    }
                } else {
                    Packet_Buffer.add(packet);
                    sendResponse(packet);
                }
            }
        }

    }

    private void sendResponse(Packet packet) throws UDPException {
        System.out.println("ACK         Seq Number Sent = " + packet.getSequenceNumber());
        sendAcknowledgement(packet);
    }

    private byte[] constructAcknowledgement(HTTPConnection.AcknowledgementCode code, int sequence_number) {
        byte[] ack = new byte[6];

        ack[0] = (byte) code.ordinal();
        ack[1] = 0;
        ack[2] = 1; // make sure is from client
        ack[3] = 0;
        ack[4] = 0;
        ack[5] = (byte) sequence_number; //checksum will be sequence number in ACK

        return ack;
    }

    public PacketBuffer getPacketBuffer() {
        return Packet_Buffer;
    }

    public void sendAcknowledgement(Packet packet) throws UDPException {
        int code = packet.getHeader()[0];
        Connection.send(constructAcknowledgement(HTTPConnection.AcknowledgementCode.values()[code], packet.getSequenceNumber()));
    }
    private long timeout = 40;
    private long stopTime;
    private boolean Should_End_Reception = false;
    private UDPConnection Connection = null;
    private PacketBuffer Packet_Buffer;
}
