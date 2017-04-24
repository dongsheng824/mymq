package com.guods.mymq.activemq;

import java.util.LinkedList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * jms连接池实现
 * @author guods
 *
 */
public class ConnectionPoolImpl implements ConnectionPool {

	//连接用户名
    private static final String USERNAME = "admin";
    //连接密码
    private static final String PASSWORD = "admin";
    //连接地址
    private static final String BROKEURL = "tcp://localhost:61616";
    //初始连接数
    private static final int INIT_SIZE = 2;
    //最大连接数
    private static final int MAX_SIZE = 5;
    //当前存在的连接总数，包括：正在使用的连接 + connections列表中的连接
    private int poolSize = 0;
    
    //创建连接工厂
    private ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEURL);
	//存放空闲connection的链表
    private LinkedList<Connection> connections = new LinkedList<Connection>();

	public ConnectionPoolImpl() {
		initConnections();
	}
	/**
	 * 初始化连接，生成初始连接数
	 */
	private void initConnections() {
		for (int i = 0; i < INIT_SIZE; i++) {
			try {
				connections.add(createConnection());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 添加连接，每增加一个connection，poolSize加1
	 * @throws JMSException
	 */
	private synchronized Connection createConnection() throws JMSException {
		Connection newConnection = connectionFactory.createConnection();
		newConnection.start();
		poolSize++;
		return newConnection;
	}
	/**
	 * 删除连接，每删除一个connection，poolSize减1
	 * @param connection
	 * @throws JMSException 
	 */
	private synchronized void delConnection() throws JMSException {
		Connection connection = connections.removeFirst();
		if (connection != null) {
			connection.close();
			poolSize--;
		}
	}
	/**
	 * 从连接池获取connection
	 * @throws JMSException 
	 */
	public synchronized Connection getConnection() throws JMSException {
		int i = 0;
		while (true) {
			//3次获取连接失败，抛超时异常
			if (i == 3) {
				throw new JMSException("Get connection timeout !");
			}
			//连接池有连接，直接取一个返回
			if (connections.size() > 0) {
				return connections.removeFirst();
			}
			//连接池空，如果连接数没到最大，创建一个连接返回
			if (poolSize < MAX_SIZE) {
				try {
					return createConnection();
				} catch (JMSException e) {
				}
			}
			//连接池空，并且连接达到最大数，等1秒再试尝试获取连接
			try {
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
			}
		}
	}
	/**
	 * 释放连接，连接用完后放回队列
	 */
	public synchronized void releaseConnection(Connection connection) {
		connections.add(connection);
	}

}
