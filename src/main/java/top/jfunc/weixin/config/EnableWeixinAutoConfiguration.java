package top.jfunc.weixin.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 使用的时候使用该注解,已经被spring.factories机制代替了,只需要导入jar包即可
 * @author xiongshiyan at 2018/9/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(WeixinAutoConfiguration.class)
public @interface EnableWeixinAutoConfiguration {
}
