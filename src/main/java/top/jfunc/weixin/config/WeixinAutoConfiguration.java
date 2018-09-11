package top.jfunc.weixin.config;

import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import top.jfunc.weixin.cache.SpringAccessTokenCache;
import top.jfunc.weixin.controller.WxDevelopFinalController;

import java.util.List;

/**
 * @author xiongshiyan
 */
@Configuration
@EnableConfigurationProperties(WeixinProperties.class)
public class WeixinAutoConfiguration implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(WeixinAutoConfiguration.class);

	@Autowired
	private WeixinProperties weixinProperties;

    @Bean
    @ConditionalOnBean(CacheManager.class)
    public SpringAccessTokenCache springAccessTokenCache(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache(weixinProperties.getAccessTokenCache());
        return new SpringAccessTokenCache(cache);
    }

	@Bean
    @ConditionalOnMissingBean
	public WxDevelopFinalController wxDevelopFinalController(){
		return new WxDevelopFinalController();
	}

	@Bean
	public WebMvcConfigurerAdapter webMvcConfigurerAdapter(){
	    return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                String urlPattern = weixinProperties.getUrlPatterns();
                MsgInterceptor httpCacheInterceptor = new MsgInterceptor(weixinProperties);
                registry.addInterceptor(httpCacheInterceptor)
                        .addPathPatterns(urlPattern);
            }
        };
    }

    @Override
    public void afterPropertiesSet() throws Exception {
	    logger.info(weixinProperties.toString());

        boolean isdev = weixinProperties.isDevMode();
        ApiConfigKit.setDevMode(isdev);
        List<ApiConfig> list = weixinProperties.getWxConfigs();
        for (ApiConfig apiConfig : list) {
            logger.info("ApiConfigKit.putApiConfig : " + apiConfig.getAppId());
            ApiConfigKit.putApiConfig(apiConfig);
        }

        WxaConfig wxaConfig = weixinProperties.getWxaConfig();
        WxaConfigKit.setDevMode(isdev);
        WxaConfigKit.setWxaConfig(wxaConfig);
        if (WxaMsg.JSON == weixinProperties.getWxaMsgParser()) {
            WxaConfigKit.useJsonMsgParser();
        }
        /*if ("jackson".equals(weixinProperties.getJsonType())) {
            JsonUtils.setJsonFactory(JacksonFactory.me());
        }*/
    }
}