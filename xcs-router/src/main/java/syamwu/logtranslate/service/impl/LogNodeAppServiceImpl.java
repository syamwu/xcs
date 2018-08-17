package syamwu.logtranslate.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import syamwu.logtranslate.dao.LogNodeDao;
import syamwu.logtranslate.entity.LogNode;
import syamwu.logtranslate.service.AbstractBaseApiService;

@Service("lognode")
public class LogNodeAppServiceImpl extends AbstractBaseApiService<LogNode> {

    static Logger logger = LoggerFactory.getLogger(LogNodeAppServiceImpl.class);

    @Autowired
    public void setLogNodeDao(LogNodeDao logNodeDao) {
        this.baseDao = logNodeDao;
    }

    @Override
    public LogNode setResourceId(LogNode entity, String id) {
        if (entity == null) {
            entity = new LogNode();
        }
        entity.setId(id);
        return entity;
    }

}