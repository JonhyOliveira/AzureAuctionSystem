package scc.data.layers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;
import scc.data.models.UserDAO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class RedisCacheLayer {

	private static final String REDIS_HOSTNAME; //é preciso dar nome a isto ------ Joao
	private static final String REDIS_KEY; //é preciso dar nome a isto ------ Joao
	private static final Integer REDIS_PORT;
	private static long DEFAULT_EXPIRATION = 3600;  //  TIME FOR AN OBJECT TO EXPIRE FROM CACHE

	private static final ObjectMapper mapper = new ObjectMapper();

	static // read from properties file
	{

		try {
			InputStream fis = RedisCacheLayer.class.getClassLoader().getResourceAsStream("redis.properties");
			Properties props = new Properties();

			props.load(fis);

			REDIS_HOSTNAME = props.getProperty("URI");
			REDIS_KEY = props.getProperty("PKEY");
			REDIS_PORT = Integer.parseInt(props.getProperty("PORT"));
			if (props.containsKey("DEFAULT_EXPIRATION"))
				DEFAULT_EXPIRATION = Integer.parseUnsignedInt(props.getProperty("DEFAULT_EXPIRATION"));

			System.out.printf("Redis = %s:%s\n", REDIS_HOSTNAME, REDIS_PORT);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

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

	private Jedis jedis = null;

	private void init() {
		if (jedis == null || jedis.isBroken() || jedis.isConnected()) {
			if (jedis != null)
				jedis.close();

			jedis = getCachePool().getResource();
		}
	}

	public void putOnCache(String key, Object obj) {
		putOnCache(key, obj, DEFAULT_EXPIRATION);
	}

	public void putOnCache(String key, Object obj, long expirationSeconds){
		init();
		try{
			jedis.set(key, mapper.writeValueAsString(obj), SetParams.setParams().ex(expirationSeconds));
		}catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T getFromCache(String key, Class<T> typeClass){
		init();

		String content = jedis.get(key);
		if(content == null)
			return null;

		try {
			return mapper.readValue(content,typeClass);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteFromCache(String key){
		init();
		jedis.del(key);
	}

	public void putElemOnList(String key, String elem){
		init();
		jedis.lpush(key,elem);
	}
}
