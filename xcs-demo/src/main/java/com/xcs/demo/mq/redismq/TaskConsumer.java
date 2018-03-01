package com.xcs.demo.mq.redismq;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;

/**
 * 模拟消费者
 * <p>
 * Title: TaskConsumer
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @vesion 1.0
 */
public class TaskConsumer implements Runnable {
	private static Jedis jedis;
	static{
		jedis = new Jedis("120.77.172.23", 6379);
		jedis.auth("123456");
	}

	public void run() {
		Random random = new Random();

		while (true) {

			// 从任务队列"task-queue"中获取一个任务，并将该任务放入暂存队列"tmp-queue"
			String taskid = jedis.rpoplpush("task-queue", "tmp-queue");
			if(StringUtils.isBlank(taskid)){
				try {
					Thread.sleep(1000);
					System.out.println("队列为空");
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// 处理任务----纯属业务逻辑，模拟一下：睡觉
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// 模拟成功和失败的偶然现象
			if (random.nextInt(13) % 7 == 0) {// 模拟失败的情况,概率为2/13
				// 将本次处理失败的任务从暂存队列"tmp-queue"中，弹回任务队列"task-queue"
				jedis.rpoplpush("tmp-queue", "task-queue");
				System.out.println(taskid + "处理失败，被弹回任务队列");

			} else {// 模拟成功的情况

				// 将本次任务从暂存队列"tmp-queue"中清除
				jedis.rpop("tmp-queue");
				jedis.rpop("tmp-queue");
				System.out.println(taskid + "处理成功，被清除");

			}
		}

	}

}