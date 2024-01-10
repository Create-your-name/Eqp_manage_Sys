package com.csmc.pms.webapp.util;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.ofbiz.base.util.Debug;

public class CacheUtil {
	public static final String module = CacheUtil.class.getName();

	private static final CacheUtil cacheUtil = new CacheUtil();

	private CacheUtil() {}
	//CacheName
	public static final String JOB_CACHE = "job";
	public static final String JOB_DEFINE_CACHE = "jobDefine";

	//Cache Default Parameter
	private int maxElementsInMemory = 1000;
	private boolean overflowToDisk = false;
	private boolean eternal = false;
	private int timeToLive = 1800;
	private int timeToIdle = 300;

	public synchronized static CacheUtil getInstance() {
		return cacheUtil;
	}

	public boolean isEternal() {
		return eternal;
	}

	public void setEternal(boolean eternal) {
		this.eternal = eternal;
	}

	public int getMaxElementsInMemory() {
		return maxElementsInMemory;
	}

	public void setMaxElementsInMemory(int maxElementsInMemory) {
		this.maxElementsInMemory = maxElementsInMemory;
	}

	public boolean isOverflowToDisk() {
		return overflowToDisk;
	}

	public void setOverflowToDisk(boolean overflowToDisk) {
		this.overflowToDisk = overflowToDisk;
	}

	public int getTimeToIdle() {
		return timeToIdle;
	}

	public void setTimeToIdle(int timeToIdle) {
		this.timeToIdle = timeToIdle;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	/**
	 * 从Cache中得到Object
	 * @param key
	 * @return
	 */
	public Serializable getObjectFromCache(String key, String cacheName) {
		try {
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache(cacheName);
			if(cache != null) {
				return checkObjectFromCache(cache, key);
			} else {
				initCache(cacheManager, cacheName);
			}
		} catch(CacheException e) {
			Debug.logError("cache error:" + e.getMessage(), module);
		}
		return null;
	}

	/**
	 * 检查Cache中含有该Object
	 * @param jobCache
	 * @param key
	 * @return
	 * @throws CacheException
	 */
	public Serializable checkObjectFromCache(Cache cache, String key) throws CacheException {
		Element element = cache.get(key);
		if(element != null) {
			Serializable obj = (Serializable)element.getValue();
			return obj;
		}
		return null;
	}

	/**
	 * 初始化Cache
	 * @param cacheManager
	 * @throws CacheException
	 */
	public void initCache(CacheManager cacheManager, String cacheName) throws CacheException {
		Cache cache = new Cache(cacheName, maxElementsInMemory, overflowToDisk, eternal, timeToLive, timeToIdle);
		cacheManager.addCache(cache);
	}

	/**
	 * 保存Object到Cache
	 * @param job
	 * @param jobIndex
	 * @param sessionId
	 */
	public void setObjectToCache(String key, String cacheName, Serializable obj, boolean recover) {
		try {
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache(cacheName);
			if(cache == null) {
				initCache(cacheManager, cacheName);
				cache = cacheManager.getCache(cacheName);
			}
			//String key = getCacheElementKey(jobRelationIndex, sessionId);
			if(recover || checkObjectFromCache(cache, key) == null) {
				Element element = new Element(key, obj);
				cache.put(element);
			}
		} catch(CacheException e) {
			Debug.logError("set Object to cache error:" + e.getMessage(), module);
		}
	}
}
