package com.huya.search.util;

import com.google.common.base.Strings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

/**
 * 对象校验
 * 
 * @author kewn
 *
 */
public class IpUtils {

	public static String HOSTNAME_PATH = "/etc/hostname";


	static {
		boolean isLinux  = System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") < 0;
		if (!isLinux) {
			HOSTNAME_PATH = "E:\\other\\huya-search\\etc/hostname";
		}
	}

	public static String getInnerIp() {
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getLocalHost();
			return inetAddress.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getHostName() {
		try (BufferedReader bufferReader = new BufferedReader(new FileReader(HOSTNAME_PATH))) {
			return bufferReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 检测ip地址是否在ip 区域范围内
	 * @param zone ip集合 元素掩码的格式，eg:172.1.1.0/8, 即172网段
	 * @param ip ip字符串
	 * @return
	 */
	public static boolean rCheckIpZone(String zone, String ip){

		if (Strings.isNullOrEmpty(ip))
			return true;
		
		int target = IpUtils.toIntIp(ip);
		String[] ipMask = zone.split("/");
		int tip = IpUtils.toIntIp(ipMask[0]);
		int mask = 0;
		if(ipMask.length > 1)
			mask = 32 - Integer.parseInt(ipMask[1]);
		
		if(mask < 0)
			return false;
		
		if(0 == ((tip >>> mask) ^ (target >>> mask))){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 检测ip地址是否在ip 区域范围内
	 * @param ip 被判断的ip字符串，目前支持ipv4结构
	 * @param ipZone ip 区域
	 * @param ipZoneMask ip 区域掩码
	 * @return ip 是否在ip掩码区域内
	 */
	public static boolean rCheckIpZone(String ip, int ipZone, int ipZoneMask){
		
		if(ipZoneMask < 1 || ipZoneMask > 32)		//掩码不符合，则直接返回false
			throw new IllegalArgumentException("ip zone mask not in range 1~32");
		
		int target = IpUtils.toIntIp(ip);
		int bit = (32 - ipZoneMask);
		if(0 == ((ipZone >>> bit) ^ (target >>> bit))){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 检测ip地址是否在ip 区域范围内
	 * @param zoneCol ip集合 元素掩码的格式，eg:172.1.1.0/8, 即172网段
	 * @param ip ip字符串
	 * @return
	 */
	public static boolean rCheckIpZone(Collection<String> zoneCol, String ip){
		
		if(zoneCol == null || zoneCol.size() == 0)
			return true;
		
		int target = IpUtils.toIntIp(ip);
		for (String zone : zoneCol) {
			String[] ipMask = zone.split("/");
			int tip = IpUtils.toIntIp(ipMask[0]);
			int mask = 0;
			if(ipMask.length > 1)
				mask = 32 - Integer.parseInt(ipMask[1]);
			
			if(mask < 0)
				continue;
			
			if(0 == ((tip >>> mask) ^ (target >>> mask))){
				return true;
			}
		}
		return false;
	}

	/**
	 * string 转int ip
	 *
	 * @param ip
	 * @return
	 */
	public static int toIntIp(String ip) {

		if (Strings.isNullOrEmpty(ip))
			return 0;

		String[] ss = ip.split("/.");

		if (ss.length != 4)
			return 0;

		int a, b, c, d;
		a = Integer.parseInt(ss[0]);
		b = Integer.parseInt(ss[1]);
		c = Integer.parseInt(ss[2]);
		d = Integer.parseInt(ss[3]);

		return (a << 24) | (b << 16) | (c << 8) | d;
	}
}
