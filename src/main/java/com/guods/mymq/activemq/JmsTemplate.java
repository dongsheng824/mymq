package com.guods.mymq.activemq;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * 模板模式，创建JMS模板
 * @author guods
 *
 */
public abstract class JmsTemplate {

	private ConnectionPool connectionPool;
	private Connection connection;
	private Session session;
	
	public JmsTemplate(ConnectionPool connectionPool) {
		super();
		this.connectionPool = connectionPool;
	}

	public abstract Object action(Session session) throws JMSException;

	/**
	 * 执行后关闭session
	 * @return
	 * @throws JMSException
	 */
	public Object execute() throws JMSException{
		//获取连接，开启session
		beginSession();
		//处理事务
		Object object = action(session);
		//关闭session，释放连接
		endSession();
		return object;
	}
	
	/**
	 * 不关系session执行
	 * @return
	 * @throws JMSException
	 */
	public Object sessionPersistExecute() throws JMSException{
		//获取连接，开启session
		beginSession();
		//处理事务
		Object object = action(session);
		return object;
	}
	
	/**
	 * 从连接池获取connection，创建session
	 * @throws JMSException
	 */
	private void beginSession() throws JMSException{
		connection = connectionPool.getConnection();
		session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
	}
	
	/**
	 * 提交事务，关闭session，释放connection
	 * @throws JMSException
	 */
	private void endSession() throws JMSException{
		session.commit();
		session.close();
		connectionPool.releaseConnection(connection);
	}
}
