package com.iohw.knobot.utils;

import java.net.*;
import java.util.*;

public class IpUtil {

    /**
     * 获取本机第一个非回环的 IPv4 地址
     */
    public static String getLocalIp4Address() {
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;

                for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Get IPv4 failed: " + e.getMessage());
        }
        return "127.0.0.1";
    }

    /**
     * 获取本机第一个非回环的 IPv6 地址
     */
    public static String getLocalIp6Address() {
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;

                for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
                    if (addr instanceof Inet6Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Get IPv6 failed: " + e.getMessage());
        }
        return "::1";
    }

    /**
     * 获取所有本机地址（IPv4 + IPv6）
     */
    public static List<String> getAllLocalAddresses() {
        List<String> list = new ArrayList<>();
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;

                for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
                    if (!addr.isLoopbackAddress()) {
                        list.add(addr.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Get all IPs failed: " + e.getMessage());
        }
        return list;
    }


    /**
     * 判断是否是内网 IP（包括 10.x, 192.168.x, 172.16~31.x）
     */
    public static boolean isInternalIp(String ip) {
        if (ip == null) return false;

        try {
            byte[] addr = InetAddress.getByName(ip).getAddress();
            final int b0 = addr[0] & 0xFF;
            final int b1 = addr[1] & 0xFF;

            return b0 == 10 || (b0 == 172 && b1 >= 16 && b1 <= 31) || (b0 == 192 && b1 == 168);
        } catch (Exception e) {
            return false;
        }
    }

}
