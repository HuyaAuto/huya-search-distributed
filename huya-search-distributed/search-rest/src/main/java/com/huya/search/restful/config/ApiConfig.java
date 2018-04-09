package com.huya.search.restful.config;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.huya.search.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 *
 * @author kewn
 * @date 创建时间：2015年6月15日 上午9:36:27
 */
public class ApiConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiConfig.class);

	private List<String> ipWhiteList;
	private String token;
	private String zookeeperConnect;

	private static ApiConfig instance;

	static {
		try {
			String fileName = "api.properties";
			Properties pro = PropertiesUtils.getProperties(fileName);

			if (null != pro) {
				instance = new ApiConfig();

				instance.token = PropertiesUtils.getByKey(pro, "api.security.token");
				instance.zookeeperConnect = PropertiesUtils.getByKey(pro,"api.zookeeper.connect");
				String tmp = PropertiesUtils.getByKey(pro, "api.security.whiteiplist");
				if (!Strings.isNullOrEmpty(tmp)) {
					instance.ipWhiteList = Lists.newArrayList(tmp.split(","));
				}
			}
		} catch (Exception e) {
			LOG.error("read api.properties err", e);
		}
	}

	/**
	 * 本地配置实例
	 * 
	 * @return
	 */
	public static ApiConfig instance() {
		return instance;
	}

	public List<String> getIpWhiteList() {
		return ipWhiteList;
	}

	public String getToken() {
		return token;
	}

	public String getZookeeperConnect() {
		return zookeeperConnect;
	}
}
