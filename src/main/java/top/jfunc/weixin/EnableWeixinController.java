package top.jfunc.weixin;

import org.springframework.context.annotation.Import;
import top.jfunc.weixin.controller.WxDevelopFinalController;

import java.lang.annotation.*;

/**
 * 使用的时候使用该注解
 * @author xiongshiyan at 2018/9/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(WxDevelopFinalController.class)
public @interface EnableWeixinController {
}
