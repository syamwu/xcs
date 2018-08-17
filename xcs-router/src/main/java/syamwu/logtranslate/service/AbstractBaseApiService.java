package syamwu.logtranslate.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import syamwu.logtranslate.dao.BaseDao;
import syamwu.logtranslate.utils.ServletUtils;
import syamwu.logtranslate.vo.Request;
import syamwu.logtranslate.vo.RequestMethod;
import syamwu.logtranslate.vo.Response;
import syamwu.logtranslate.vo.ResponseEnum;
import syamwu.xchushi.fw.common.Asset;
import syamwu.xchushi.fw.common.util.JsonUtils;
import syamwu.xchushi.fw.common.util.UUIDUtils;

public abstract class AbstractBaseApiService<T> implements BaseApiService {

    static Logger logger = LoggerFactory.getLogger(AbstractBaseApiService.class);

    protected BaseDao<T> baseDao;

    @SuppressWarnings("rawtypes")
    @Override
    public Response invoke(Request request) {
        RequestMethod method = request.getMethod();
        Response response = null;
        try {
            switch (method) {
            case GET:
                response = get(request);
                break;
            case POST:
                response = post(request);
                break;
            case PUT:
                response = put(request);
                break;
            case DELETE:
                response = delete(request);
                break;
            default:
                break;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = ResponseEnum.RES_500.getResponse();
        }
        return response;
    }

    public Response<T> get(Request request) {
        T t = buildResouceEntity(request);
        if (t == null)
            return ResponseEnum.RES_400.getResponse();
        List<T> ts = baseDao.select(t);
        if (ts == null || ts.isEmpty()) {
            return ResponseEnum.RES_404.getResponse();
        } else if (ts.size() > 1) {
            Response<T> response = ResponseEnum.RES_400.getResponse();
            response.setResultMessage("Result more than one");
            return response;
        }
        return new Response<T>(ts.get(0));
    }

    public Response<List<T>> gets(Request request) {
        T t = buildResouceEntity(request);
        if (t == null)
            return ResponseEnum.RES_400.getResponse();
        List<T> ts = baseDao.select(t);
        if (ts == null || ts.isEmpty()) {
            return ResponseEnum.RES_404.getResponse();
        }
        return new Response<List<T>>(ts);
    }

    public Response<T> post(Request request) {
        T t = buildResouceEntity(request);
        if (t == null)
            return ResponseEnum.RES_400.getResponse();
        baseDao.insert(t);
        return ResponseEnum.RES_200.getResponse();
    }

    public Response<T> put(Request request) {
        T t = buildResouceEntity(request);
        if (t == null)
            return ResponseEnum.RES_400.getResponse();
        baseDao.update(t);
        return ResponseEnum.RES_200.getResponse();
    }

    public Response<T> delete(Request request) {
        T t = buildResouceEntity(request);
        if (t == null)
            return ResponseEnum.RES_400.getResponse();
        baseDao.delete(t);
        return ResponseEnum.RES_200.getResponse();
    }

    public T buildResouceEntity(Request request) {
        T entity = null;
        switch (request.getMethod()) {
        case GET:
        case PUT:
        case DELETE:
            Map<String, Object> requestParams = request.getRequestParams();
            if ((requestParams == null || requestParams.isEmpty()) && StringUtils.isEmpty(request.getResourceId())) {
                return Asset.getNull();
            }
            entity = setResourceId(JsonUtils.parseObject(requestParams, getParamterizedType()),
                    request.getResourceId());
            break;
        case POST:
            entity = setResourceId(JsonUtils.parseObject(request.getRequestParams(), getParamterizedType()),
                    UUIDUtils.getUUID32());
            break;
        default:
            break;
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getParamterizedType() {
        Class<T> cls = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        return cls;
    }

    public abstract T setResourceId(T entity, String id);

}
