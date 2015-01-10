package com.cj.uaap;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;

public class JmsReceiver {
	@Test
	public void test001ReceiveMessageer(){
		// ConnectionFactory ：连接工厂，JMS 用它创建连接
        ConnectionFactory connectionFactory;
        // Connection ：JMS 客户端到JMS Provider 的连接
        Connection connection = null;
        // Session： 一个发送或接收消息的线程
        Session session;
        // Destination ：消息的目的地;消息发送给谁.
        Destination destination;
        // 消费者，消息接收者
        MessageConsumer consumer;
        connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                "tcp://localhost:61616");
        try {
            // 构造从工厂得到连接对象
            connection = connectionFactory.createConnection();
            // 启动
            connection.start();
            // 获取操作连接
            session = connection.createSession(Boolean.FALSE,
                    Session.AUTO_ACKNOWLEDGE);
            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            destination = session.createQueue("test");
            consumer = session.createConsumer(destination);
            while (true) {
                //设置接收者接收消息的时间，为了便于测试，这里谁定为100s
                TextMessage message = (TextMessage) consumer.receive(100000);
                if (null != message) {
                    System.out.println("收到消息" + message.getText());
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection)
                    connection.close();
            } catch (Throwable ignore) {
            }
        }
	}
	@Test
	public  void get(){
		 MessageConsumer consumer=null;
		 Session session =null;
		 Connection connection=null;
		try{
		    String url = "tcp://localhost:61616";
		    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		    // 设置用户名和密码，这个用户名和密码在conf目录下的credentials.properties文件中，也可以在activemq.xml中配置
		    connectionFactory.setUserName("system");
		    connectionFactory.setPassword("manager");
		    // 创建连接
		    connection = connectionFactory.createConnection();
		    connection.start();
		    // 创建Session
		    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		    // 创建目标，就创建主题也可以创建队列
		    Destination destination = session.createQueue("test");
		    // 创建消息消费者
		    consumer = session.createConsumer(destination);
		    // 接收消息，参数：接收消息的超时时间，为0的话则不超时，receive返回下一个消息，但是超时了或者消费者被关闭，返回null
		    while(true){
		    	Message message = consumer.receive(1000);
			    if (message instanceof TextMessage) {
			        TextMessage textMessage = (TextMessage) message;
			        String text = textMessage.getText();
			        System.out.println("Received: " + text);
			    } else if(message instanceof ObjectMessage){
			    	ObjectMessage objectMessage = (ObjectMessage) message;
			    	LoggingEventVO myMessage = (LoggingEventVO)objectMessage.getObject();
			    	System.out.println(myMessage.getMessage());
			    }
		    }
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}finally{
			try {
				consumer.close();
				session.close();
			    connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
