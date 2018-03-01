package cn.yunyichina.front;

import javax.annotation.Resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import xcs.spring.StartUp;

@Component
public class InitEsRunner implements ApplicationRunner {

    @Resource
    Environment env;

    @Override
    public void run(ApplicationArguments arg0) throws Exception {
        StartUp.start(env);
    }

}
