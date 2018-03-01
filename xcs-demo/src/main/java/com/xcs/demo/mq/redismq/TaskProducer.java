package com.xcs.demo.mq.redismq;

import java.util.Random;

import redis.clients.jedis.Jedis;

public class TaskProducer implements Runnable {
	private static Jedis jedis;
	static{
		jedis = new Jedis("120.77.172.23", 6379);
		jedis.auth("123456");
	}
	public void run() {
		Random random = new Random();
		while (true) {
			try {
				Thread.sleep(random.nextInt(600) + 600);
				// 模拟生成一个任务
				//UUID taskid = UUID.randomUUID();
				String taskid = random.nextInt(13) * random.nextInt(13)*random.nextInt(13)+random.nextInt(13)+"";
				// 将任务插入任务队列：task-queue
				jedis.lpush("task-queue", taskid.toString());
				System.out.println("插入了一个新的任务： " + taskid);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
