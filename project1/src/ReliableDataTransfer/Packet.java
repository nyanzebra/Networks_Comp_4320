package ReliableDataTransfer;

import ReliableDataTransfer.ErrorDetection.ErrorDetection;
import ReliableDataTransfer.SegmentationAndReassembly.SegmentAndReassemble;
import UDPConnection.Exception.UDPException;
import UDPConnection.UDPConnection;
import WebApplication.HTTPConnection;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Packet {
    private byte[] ACK(HTTPConnection.AcknowledgementCode code, int sequence_number) {
        byte[] ack = new byte[6];

        ack[0] = (byte) code.ordinal();
        ack[1] = 0;
        ack[2] = 0;
        ack[3] = 0;
        ack[4] = 0;
        ack[5] = (byte) sequence_number; //checksum will be sequence number in ACK

        return ack;
    }

    public Packet(byte[] packet, UDPConnection connection) {
        Packet = packet;
        Data = Arrays.copyOfRange(packet, 6, packet.length);
        Header = Arrays.copyOfRange(packet, 0, 6);
        Connection = connection;
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

    public void Transmit() throws UDPException {
        java.util.Timer timer = new Timer();
        Connection.send(Packet);
        timer.schedule(new Transmission(Connection, Packet), 40);
    }

    public HTTPConnection.AcknowledgementCode getAcknowledgementCode() {
        return HTTPConnection.AcknowledgementCode.values()[Header[0]];
    }

    public void updateAcknowledgementCode(HTTPConnection.AcknowledgementCode code) {
        Header[0] = (byte) code.ordinal();
    }

    public int getSequenceNumber() {
        return SegmentAndReassemble.orderOfPacketInMessage(Arrays.copyOfRange(Header, 1, 5));
    }

    public void Acknowledgement() throws UDPException {
        if (ErrorDetection.isCorrupted(Data, Header[5])) {
            Connection.send(ACK(HTTPConnection.AcknowledgementCode.Not_Acknowledged, getSequenceNumber()));
        } else {
            Connection.send(ACK(HTTPConnection.AcknowledgementCode.Acknowledged, getSequenceNumber()));
        }
    }

    public byte[] getData() {
        return Data;
    }

    public class Transmission extends TimerTask {
        public Transmission(UDPConnection connection, byte[] packet) {
            Connection = connection;
            Packet = packet;
        }

        @Override
        public void run() {
            try {
                Connection.send(Packet);
            } catch (UDPException e) {
                e.printStackTrace();
            }
        }

        private UDPConnection Connection;
        private byte[] Packet;
    }

    private byte[] Packet;
    private byte[] Data;
    private byte[] Header;
    private UDPConnection Connection;
}
