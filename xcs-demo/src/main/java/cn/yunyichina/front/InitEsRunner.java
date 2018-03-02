package cn.yunyichina.front;

import java.util.Random;

import javax.annotation.Resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.test.springboot.controller.TestRestController;
import com.xchushi.common.Asset;
import com.xchushi.log.elasticsearch.EsLogger;
import com.xchushi.log.elasticsearch.EsLoggerFactory;
import com.xchushi.transfer.sender.HttpSender;
import com.xcs.utils.StreamUtils;

@Component
public class InitEsRunner implements ApplicationRunner {

    @Resource
    Environment env;

    @Override
    public void run(ApplicationArguments arg0) throws Exception {
        StartUp.start(env);
    }

    static EsLogger eslogger = EsLoggerFactory.getLogger(TestRestController.class);

    static String[] strs = new String[10];

    static {
        for (int i = 0; i < 10; i++) {
            strs[i] = StreamUtils.file2string("D:\\upload\\data" + i + ".txt");
        }
    }

    public static void main(String[] args) {
        try {
            final int count = 10000;
            final long time = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                eslogger.info(strs[new Random().nextInt(10)]);
            }
            new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            Thread.sleep(1);
                            if (HttpSender.okCount.get() == count) {
                                System.out.println("发送:" + count + ",用时:" + (System.currentTimeMillis() - time));
                                return;
                            } else if (HttpSender.okCount.get() > count) {
                                Asset.assetFail(HttpSender.okCount.get() + "");
                                return;
                            } else {
                                System.out.println("发送:" + HttpSender.okCount.get() + ",用时:"
                                        + (System.currentTimeMillis() - time));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            eslogger.error(e.getMessage(), e);
        }

    }

}
