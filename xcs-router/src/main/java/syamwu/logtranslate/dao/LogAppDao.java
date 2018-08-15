package syamwu.logtranslate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import syamwu.logtranslate.entity.LogApp;
import syamwu.logtranslate.entity.LogNodeApp;

@Repository
public interface LogAppDao extends BaseDao<LogApp> {

    List<LogNodeApp> selectNodeByAppCodes(@Param("list") List<String> appCodes);

}