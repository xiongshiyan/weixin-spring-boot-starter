package top.jfunc.weixin.config;

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

/**
 * @author xiongshiyan
 */
@Configuration
@EnableConfigurationProperties(WeixinProperties.class)
public class WeixinAutoConfiguration {
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
}