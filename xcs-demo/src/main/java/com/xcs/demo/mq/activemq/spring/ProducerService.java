package com.xcs.demo.mq.activemq.spring;

import javax.jms.Destination;

public interface ProducerService {
	public void sendMessage(Destination destination, String message);
}
