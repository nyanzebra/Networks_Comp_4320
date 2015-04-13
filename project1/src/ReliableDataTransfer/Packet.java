package ReliableDataTransfer;

import ReliableDataTransfer.ErrorDetection.ErrorDetection;
import ReliableDataTransfer.SegmentationAndReassembly.SegmentAndReassemble;
import WebApplication.HTTPConnection;

import java.util.Arrays;

public class Packet {

    public Packet(byte[] packet) {
        Packet = packet;
        Data = Arrays.copyOfRange(packet, 6, packet.length);
        Header = Arrays.copyOfRange(packet, 0, 6);
        if (ErrorDetection.isCorrupted(Data, Header[5])) {
            updateAcknowledgementCode(HTTPConnection.AcknowledgementCode.Not_Acknowledged);
        } else {
            updateAcknowledgementCode(HTTPConnection.AcknowledgementCode.Acknowledged);
        }
    }

    public byte[] getHeader() {
        return Header;
    }

    public boolean isLastPacket() {
        return Packet[6] == 0;
    }

    public byte[] toBytes() {
        return Packet;
    }

    public HTTPConnection.AcknowledgementCode getAcknowledgementCode() {
        return HTTPConnection.AcknowledgementCode.values()[Header[0]];
    }

    public void updateAcknowledgementCode(HTTPConnection.AcknowledgementCode code) {
        Header[0] = (byte) code.ordinal();
        Packet[0] = (byte) code.ordinal();
    }

    public int getSequenceNumber() {
        return SegmentAndReassemble.orderOfPacketInMessage(Arrays.copyOfRange(Header, 1, 5));
    }

    public byte[] getData() {
        return Data;
    }

    private byte[] Packet;
    private byte[] Data;
    private byte[] Header;
}
