package controllers;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.NumberFormat;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpUtils;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;


public final class NTPTime {

    private static final NumberFormat numberFormat = new java.text.DecimalFormat("0.00");

    public static TimeStamp processResponse(TimeInfo info)
    {
        NtpV3Packet message = info.getMessage();
        int stratum = message.getStratum();
        String refType;
        if (stratum <= 0) {
            refType = "(Unspecified or Unavailable)";
        } else if (stratum == 1) {
            refType = "(Primary Reference; e.g., GPS)"; // GPS, radio clock, etc.
        } else {
            refType = "(Secondary Reference; e.g. via NTP or SNTP)";
        }
        int version = message.getVersion();
       
        int refId = message.getReferenceId();
        String refAddr = NtpUtils.getHostAddress(refId);
        String refName = null;
        if (refId != 0) {
            if (refAddr.equals("127.127.1.0")) {
                refName = "LOCAL"; 
            } else if (stratum >= 2) {
                if (!refAddr.startsWith("127.127")) {
                    try {
                        InetAddress addr = InetAddress.getByName(refAddr);
                        String name = addr.getHostName();
                        if (name != null && !name.equals(refAddr)) {
                            refName = name;
                        }
                    } catch (UnknownHostException e) {
                        refName = NtpUtils.getReferenceClock(message);
                    }
                }
            } else if (version >= 3 && (stratum == 0 || stratum == 1)) {
                refName = NtpUtils.getReferenceClock(message);
            }

        }
        if (refName != null && refName.length() > 1) {
            refAddr += " (" + refName + ")";
        }
        TimeStamp xmitNtpTime = message.getTransmitTimeStamp();
        System.out.println(" Transmit Timestamp:\t" + xmitNtpTime + "  " + xmitNtpTime.toDateString());
        return xmitNtpTime;
    }

    public static TimeStamp[] runNTP(String[] args)
    {
        if (args.length == 0) {
            System.exit(1);
        }
        TimeStamp[] times = new TimeStamp[args.length];
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(5000);
        try {
            client.open();
            int x = 0;
            for (String arg : args)
            {
                try {
                    InetAddress hostAddr = InetAddress.getByName(arg);
                    TimeInfo info = client.getTime(hostAddr);
                    times[x] = processResponse(info);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                x++;
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        client.close();
        return times;
    }

}
