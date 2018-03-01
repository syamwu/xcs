package com.xcs.demo.mq.activemq.spring;

import javax.annotation.Resource;
import javax.jms.Destination;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xcs.demo.mq.activemq.MyBean;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/xcs/demo/mq/activemq/spring/spring-config-jms.xml" })
public class TestProducerConsumer {

	@Autowired
	private ProducerService producerService;

	@Autowired
	@Qualifier("queueDestination")
	private Destination destination;
	
	@Test
	public void testSend() {
		for (int i = 0; i < 20000; i++) {
			producerService.sendMessage(destination, "你好，生产者！这是消息：" + (i + 1));
		}
	}

}
