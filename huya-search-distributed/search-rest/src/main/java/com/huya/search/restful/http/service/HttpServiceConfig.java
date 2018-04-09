/**
 * 
 */
package com.huya.search.restful.http.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.huya.search.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * @author kewn
 *
 */
public class HttpServiceConfig {

	private static final Logger LOG = LoggerFactory.getLogger(HttpServiceConfig.class);

	private static Properties pros = new Properties();
	
	private static String localIp;

	private static boolean isLinux = false;			// 是否linux系统用于netty的native epoll设定

	static {
		try {
			pros = PropertiesUtils.getProperties("http-service.properties");
			
			localIp = loadIp();
			LOG.info("http local ip:{}", localIp);

			initEnv();
		} catch (Exception e) {
			LOG.error("read server-http err", e);
		}
	}

	/**
	 * 加载本机ip,优先取公网ip
	 * 
	 * @return
	 */
	private static String loadIp() {

		List<String> pubList = Lists.newArrayList();	// 公网ip
		List<String> priList = Lists.newArrayList();	// 私有ip

		// 否则从本机多ip中选择一个
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration<InetAddress> ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i instanceof Inet6Address)
						continue;
					if (i.isSiteLocalAddress()) {
						priList.add(i.getHostAddress());
						break;// 每个网卡取一个ip
					} else if (i.isLoopbackAddress() || i.isLinkLocalAddress() || i.isMulticastAddress() || i.isMCGlobal()) {

					} else {
						pubList.add(i.getHostAddress());
						break;// 每个网卡取一个ip
					}
				}
			}
		} catch (SocketException e1) {
			LOG.error("get http local ip err", e1);
			return null;
		}

		if (pubList != null && pubList.size() != 0)
			return pubList.get(0);
		if (priList != null && priList.size() != 0)
			return priList.get(0);

		return null;
	}

	private static void initEnv() {
		String osName = System.getProperty("os.name");
		if (null != osName && osName.toLowerCase().contains("linux"))
			isLinux = true;
		else
			isLinux = false;
	}

	/**
	 * 获取服务版本
	 * 
	 * @return
	 */
	public static String getServerVer() {
		return getByKey("server.version");
	}

	/**
	 * 获取端口配置
	 * 
	 * @return
	 */
	public static int getPort() {
		return Integer.parseInt(getByKey("server.tcp.port"));
	}

	/**
	 * 获取端口配置
	 * 
	 * @return
	 */
	public static String getHost() {
		return getByKey("server.tcp.ip");
	}

	/**
	 * 获取本机ip地址，取值顺序, ：公网ip
	 * 
	 * @return
	 */
	public static String getLocalHost() {

		// 优先取配置的ip
		String ip = getHost();
		if (!Strings.isNullOrEmpty(ip))
			return ip;

		return localIp;
	}

	/**
	 * boss 线程数
	 * 
	 * @return
	 */
	public static int getBoss() {
		return Integer.parseInt(getByKey("server.netty.boss"));
	}

	/**
	 * worker 线程数
	 * 
	 * @return
	 */
	public static int getWorker() {
		Double factor = Double.parseDouble(getByKey("server.netty.worker.cpufactor"));
		return (int) Math.round(Runtime.getRuntime().availableProcessors() * factor);
	}

	/**
	 * 业务执行线程数
	 * 
	 * @return
	 */
	public static int getExecute() {
		Double factor = Double.parseDouble(getByKey("server.netty.execute.cpufactor"));
		return (int) Math.round(Runtime.getRuntime().availableProcessors() * factor);
	}

	/**
	 * get object by key
	 * 
	 * @param key
	 * @return object
	 */
	public static String getByKey(String key) {
		if (null != pros) {
			Object obj = pros.get(key);
			if (null != obj) {
				return obj.toString();
			}
		}
		return null;
	}

	public static boolean isLinux() {
		return isLinux;
	}
}
