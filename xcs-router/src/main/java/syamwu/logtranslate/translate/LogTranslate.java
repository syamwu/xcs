package syamwu.logtranslate.translate;

import syamwu.logtranslate.vo.TranslateRequest;
import syamwu.logtranslate.vo.TranslateResponse;

public interface LogTranslate {

    TranslateResponse translate(TranslateRequest request);
    
}
