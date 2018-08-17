package syamwu.logtranslate.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import syamwu.logtranslate.service.ResourcesService;
import syamwu.logtranslate.service.impl.ResourcesServiceImpl;

/**
 * 接口服务配置类
 * 
 * @author: syam_wu
 * @date: 2018
 */
@Configuration
public class ApiConfig {
    
    @Value("${api.config}")
    private String apiConfig;
    
    @Bean
    public ResourcesService resourcesService() throws IOException{
        return new ResourcesServiceImpl(apiConfig);
    }
    
}
