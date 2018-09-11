package top.jfunc.weixin.config;

import com.jfinal.weixin.sdk.api.ApiConfigKit;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 此拦截器的作用是拿到参数 appId 放到 threadlocal , 方便后续工具类的使用
 */
public class MsgInterceptor extends HandlerInterceptorAdapter {
    private WeixinProperties weixinProperties;

    public MsgInterceptor(WeixinProperties weixinProperties) {
        this.weixinProperties = weixinProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 非控制器请求直接跳出
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String appId = request.getParameter(weixinProperties.getAppIdKey());
        if (null != appId) {
            ApiConfigKit.setThreadLocalAppId(appId);
            return true;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        ApiConfigKit.removeThreadLocalAppId();
    }
}
