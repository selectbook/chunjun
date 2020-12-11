package com.iiot.util;

import java.util.Set;

import com.google.common.collect.HashMultimap;

/**
 * 双向MAP，KEY和VALUE都可以重复
 * @param <K>
 * @param <V>
 */
public class MultiBidiMap<K, V> {

	private final HashMultimap<K, V> normalMap; // 正MAP
	private final HashMultimap<V, K> reverseMap; // 反MAP

	public MultiBidiMap() {
		super();
		this.normalMap = createNormalMap();
		this.reverseMap = createReverseMap();
	}

	public MultiBidiMap(HashMultimap<K, V> normalMap, HashMultimap<V, K> reverseMap) {
		super();
		this.normalMap = normalMap;
		this.reverseMap = reverseMap;
	}

	public HashMultimap<K, V> getNormalMap() {
		return normalMap;
	}

	public HashMultimap<V, K> getReverseMap() {
		return reverseMap;
	}

	/**
	 * 创建正向HashMultimap实例
	 * @return
	 */
	private HashMultimap<K, V> createNormalMap() {
		return HashMultimap.create();
	}

	/**
	 * 创建反向HashMultimap实例
	 * @return
	 */
	private HashMultimap<V, K> createReverseMap() {
		return HashMultimap.create();
	}

	/**
	 * 缓存数据
	 * @param key
	 * @param value
	 */
	public void put(K key, V value) {
		normalMap.put(key, value);
		reverseMap.put(value, key);
	}

	/**
	 * 获取值
	 * @param key
	 * @return
	 */
	public Set<V> get(K key) {
		return normalMap.get(key);
	}

	/**
	 * 获取KEY
	 * @param value
	 * @return
	 */
	public Set<K> getKey(V value) {
		return reverseMap.get(value);
	}

	/**
	 * 根据KEY删除ENTRY
	 * @param key
	 * @return 
	 */
	public Set<V> remove(K key) {
		Set<V> valueSet = normalMap.removeAll(key);
		for (V v : valueSet) {
			reverseMap.remove(v, key);
		}
		return valueSet;
	}

	/**
	 * 根据VALUE删除ENTRY
	 * @param value
	 * @return 
	 * @return 
	 */
	public Set<K> removeValue(V value) {
		Set<K> keySet = reverseMap.removeAll(value);
		for (K k : keySet) {
			normalMap.remove(k, value);
		}
		return keySet;
	}

	/**
	 * 根据KEY和VALUE删除正向MAP中的数据
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean removeSingleFromNormalMap(K key, V value) {
		boolean flag = normalMap.remove(key, value);
		return flag;
	}
	
	/**
	 * 根据KEY和VALUE删除反向MAP中的数据
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean removeSingleFromReverseMap(K key, V value) {
		boolean flag = reverseMap.remove(value, key);
		return flag;
	}

}
