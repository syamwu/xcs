package syamwu.logtranslate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {"syamwu.logtranslate"})
@MapperScan(basePackages = {"syamwu.logtranslate.dao"})
@EnableAspectJAutoProxy
public class Main {

    public static void main(String[] args) {
        SpringApplication bootstrap = new SpringApplication(Main.class);
        bootstrap.run(args);
    }
 
}
