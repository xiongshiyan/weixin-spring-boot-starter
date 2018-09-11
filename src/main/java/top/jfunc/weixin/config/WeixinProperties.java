package top.jfunc.weixin.config;

import com.jfinal.weixin.sdk.api.ApiConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties("spring.weixin")
public class WeixinProperties {

    /**
     * 拦截的路由，默认：/open/wx/develop
     */
    private String urlPatterns = "/open/wx/develop";
    /**
     * 是否开发模式，默认：false
     */
    private boolean devMode = false;
    /**
     * Spring cache name，需要开启spring cache，默认：weixinCache
     */
    private String accessTokenCache = "weixinCache";
    /**
     * 多公众号url挂参，默认：appId
     */
    private String appIdKey = "appId";
    /**
     * 多公众号配置
     */
    private List<ApiConfig> wxConfigs = new ArrayList<>();
    /**
     * 小程序配置
     */
    private WxaConfig wxaConfig = new WxaConfig();
    /**
     * 小程序消息解析，默认xml，支持json和xml
     */
    private WxaMsg wxaMsgParser = WxaMsg.XML;
    /**
     * json 类型，默认为 boot 的 jackson，可配置成 jfinal，使用jfinal默认规则
     */
    private String jsonType = "jackson";

    public String getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(String urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public String getAccessTokenCache() {
        return accessTokenCache;
    }

    public void setAccessTokenCache(String accessTokenCache) {
        this.accessTokenCache = accessTokenCache;
    }

    public String getAppIdKey() {
        return appIdKey;
    }

    public void setAppIdKey(String appIdKey) {
        this.appIdKey = appIdKey;
    }

    public List<ApiConfig> getWxConfigs() {
        return wxConfigs;
    }

    public void setWxConfigs(List<ApiConfig> wxConfigs) {
        this.wxConfigs = wxConfigs;
    }

    public WxaConfig getWxaConfig() {
        return wxaConfig;
    }

    public void setWxaConfig(WxaConfig wxaConfig) {
        this.wxaConfig = wxaConfig;
    }

    public WxaMsg getWxaMsgParser() {
        return wxaMsgParser;
    }

    public void setWxaMsgParser(WxaMsg wxaMsgParser) {
        this.wxaMsgParser = wxaMsgParser;
    }

    public String getJsonType() {
        return jsonType;
    }

    public void setJsonType(String jsonType) {
        this.jsonType = jsonType;
    }
}