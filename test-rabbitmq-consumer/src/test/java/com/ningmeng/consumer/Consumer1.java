package com.ningmeng.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer1 {
    //队列名称 queue_inform_email
    private static final String QUEUE = "helloworld";
    public static void main(String[] args) {
        try {
            //创建连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("127.0.0.1");
            //浏览器管理页面使用端口：15672，后台使用端口：5672
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/");//rabbitmq默认虚拟机名称为“/”，虚拟机相当于一个独立的mq服务

            //创建连接
            Connection connection = factory.newConnection();
            //生产者和Broker建立通道。（信道）
            //每个连接可以创建多个通道，每个通道代表一个会话任务
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE,true,false,false,null);

            //消费消息方法
            Consumer consumer = new DefaultConsumer(channel){
            /**
             * @param consumerTag   消费者的标签，在channel.basicConsume()去指定
             * @param envelope  消息包的内容，可从中获取消息id，消息routingkey，交换机，消息和重传标志 (收到消息失败后是否需要重新发送)
             * @param properties    属性参数
             * @param body  消息主体
             * @throws IOException
             */
                public void handleDelivery(String consumerTag,Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    //交换机
                    String exchange=envelope.getExchange();
                    String rouKey = envelope.getRoutingKey();
                    //消息id
                    long deliveryTag = envelope.getDeliveryTag();
                    //消息内容
                    String msg = new String(body,"utf-8");
                    System.out.println("receive message.." + msg);
                }
            };
            //监听队列
            /**
             * 监听队列String queue, boolean autoAck,Consumer callback
             * 参数明细
             * 1、队列名称
             * 2、是否自动回复，设置为true为表示消息接收到自动向mq回复接收到了，mq接收到回复会删除消息，
             设置 为false则需要手动回复
             * 3、消费消息的方法，消费者接收到消息后调用此方法
             */
            channel.basicConsume(QUEUE,true,consumer);

        }catch (Exception e){

        }
    }
}
