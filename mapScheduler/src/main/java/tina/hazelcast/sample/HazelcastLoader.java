package tina.hazelcast.sample;

import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastLoader {
	private static final Logger log = LoggerFactory.getLogger(HazelcastLoader.class);
	
	public static HazelcastInstance load() {
		Config config = new Config();

		HazelcastInstance ret = Hazelcast.newHazelcastInstance(config);
		return ret;
	}
}
