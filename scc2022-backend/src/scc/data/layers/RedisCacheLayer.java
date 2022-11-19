package scc.data.layers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class RedisCacheLayer {

	private static final String REDIS_HOSTNAME = System.getenv("REDIS_HOSTNAME");
	private static final String REDIS_KEY = System.getenv("REDIS_KEY");
	private static final Integer REDIS_PORT = Integer.parseInt(System.getenv("REDIS_PORT"));
	private static long DEFAULT_EXPIRATION = 3600;  //  TIME FOR AN OBJECT TO EXPIRE FROM CACHE

	private static final ObjectMapper mapper = new ObjectMapper();

	private static JedisPool instance;
	private static RedisCacheLayer myInstance;

	public synchronized static JedisPool getCachePool() {
		if( instance != null)
			return instance;
		final JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(128);
		poolConfig.setMaxIdle(128);
		poolConfig.setMinIdle(16);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);
		poolConfig.setNumTestsPerEvictionRun(3);
		poolConfig.setBlockWhenExhausted(true);
		instance = new JedisPool(poolConfig, REDIS_HOSTNAME, REDIS_PORT, 1000, REDIS_KEY, true);
		return instance;
	}

	public static RedisCacheLayer getInstance() {
		if (myInstance == null)
			myInstance = new RedisCacheLayer();
		return myInstance;
	}

	public void putOnCacheNoExpire(String key, Object obj) {

	}

	public void putOnCache(String key, Object obj) {
		putOnCache(key, obj, DEFAULT_EXPIRATION);
	}

	public void putOnCache(String key, Object obj, long expirationSeconds){
		try (Jedis jedis = getCachePool().getResource()) {
			jedis.set(key, mapper.writeValueAsString(obj), SetParams.setParams().ex(expirationSeconds));

		}catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T getFromCache(String key, Class<T> typeClass){
		try (Jedis jedis = getCachePool().getResource()) {
			String content = jedis.get(key);
			if(content == null)
				return null;

			return mapper.readValue(content,typeClass);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteFromCache(String key){

		try (Jedis jedis = getCachePool().getResource()) {
			jedis.del(key);
		}
	}

	public void insertInSet(String key, String elem){

		try (Jedis jedis = getCachePool().getResource()) {
			jedis.sadd(key, elem);
		}
	}

	public boolean setContains(String key, String elem) {
		try (Jedis jedis = getCachePool().getResource()) {
			return jedis.sismember(key, elem);
		}
	}

	public Optional<String> popFromSet(String key) {
		try (Jedis jedis = getCachePool().getResource()) {
			return Optional.ofNullable(jedis.spop(key));
		}
	}

	public boolean removeFromSet(String key, String elem) {
		try (Jedis jedis = getCachePool().getResource()) {
			return jedis.srem(key, elem) > 0;
		}
	}
}
