package syamwu.logtranslate.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import syamwu.logtranslate.dao.LogAppDao;
import syamwu.logtranslate.entity.LogApp;
import syamwu.logtranslate.service.AbstractBaseApiService;

@Service("logapp")
public class LogAppServiceImpl extends AbstractBaseApiService<LogApp> {

    static Logger logger = LoggerFactory.getLogger(LogAppServiceImpl.class);

    @Autowired
    public void setLogAppDao(LogAppDao logAppDao) {
        this.baseDao = logAppDao;
    }

    @Override
    public LogApp setResourceId(LogApp entity, String id) {
        if (entity == null) {
            entity = new LogApp();
        }
        entity.setId(id);
        return entity;
    }

}
