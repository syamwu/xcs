package com.xcs.demo.mq.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Sender {
	private static final int SEND_NUMBER = 5;
	
	public static SenderT st1 = new SenderT();
	
	public static SenderT st2 = new SenderT();
	
	
	public static Session session = null;
	
	public static MessageProducer producer = null;

	public static void main(String[] args) {
		ConnectionFactory connectionFactory;
		Connection connection;
		Session session;
		Destination destination;
		MessageProducer producer;
		connectionFactory = new ActiveMQConnectionFactory("admin", "admin", "tcp://120.77.172.23:61616");
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			//第一个参数是是否是事务型消息，设置为true,第二个参数无效
			//第二个参数是
			//Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。异常也会确认消息，应该是在执行之前确认的
			//Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会删除消息。可以在失败的
			//时候不确认消息,不确认的话不会移出队列，一直存在，下次启动继续接受。接收消息的连接不断开，其他的消费者也不会接受（正常情况下队列模式不存在其他消费者）
			//DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。在需要考虑资源使用时，这种模式非常有效。
			//待测试
			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			destination = session.createQueue("queue");
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			//优先级不能影响先进先出。。。那这个用处究竟是什么呢呢呢呢
			MyBean bean = new MyBean();
			bean.setId(13);
			long time = System.currentTimeMillis();
			int index = 0;
			System.out.println("start---------------");
			for(int i=0;i<100000;i++){
				bean.setName("小黄"+i);
				producer.send(session.createObjectMessage(""+i));
				if(System.currentTimeMillis() - time >= 1000){
					time = System.currentTimeMillis();
					System.out.println(i-index+" senders 1s");
					index = i;
				}
			}
			producer.close();
			System.out.println("end---------------");
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public static void sendMessage(Session session, MessageProducer producer) throws Exception {
		
		for (int i = 1; i <= SEND_NUMBER; i++) {
			
			TextMessage message = session.createTextMessage("ActiveMq 发送的消息" + i);
			// 发送消息到目的地方
			System.out.println(Thread.currentThread().getName()+"发送消息：" + "ActiveMq 发送的消息" + i);
			producer.send(message);
			//System.out.println(Thread.currentThread().getName()+"发送消息：" + "ActiveMq 发送的消息" + i);
		}
	}
	
	static class SenderT implements Runnable{

		public void run() {
			try {
				sendMessage(session, producer);
				session.commit();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
