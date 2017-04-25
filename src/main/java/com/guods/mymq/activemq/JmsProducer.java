package com.guods.mymq.activemq;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

public class JmsProducer {

	private static ConnectionPool connectionPool = new ConnectionPoolImpl();
	private static JmsAction jmsAction = new JmsAction(connectionPool);

	public static void main(String[] args) throws JMSException {
		topicProduce();
	}

	private static void p2pProduce() throws JMSException {
		// 创建队列
		Queue queue = jmsAction.createQueue("MyQueue");
		// 生产消息
		for (int i = 0; i < 5; i++) {
			String message = "第" + i + "条消息！";
			jmsAction.produceP2pMessage(queue, message);
			System.out.println("生产消息：" + message);
		}
	}
	
	private static void topicProduce() throws JMSException{
		Topic topic = jmsAction.createTopic("MyTopic");
		for (int i = 0; i < 5; i++) {
			String message = "第" + i + "条主题！";
			jmsAction.produceTopicMessage(topic, message);
			System.out.println("生产主题：" + message);
		}
	}
}
