package top.jfunc.weixin.utils;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @author xiongshiyan at 2018/9/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SortUtil {
    /**
     * 按照Ordered接口和Order注解排序
     * @param objects 对象列表
     */
    public static <T> List<T> sort(List<T> objects){
        objects.sort((l1, l2)->{
            Class l1Class = l1.getClass();
            Class l2Class = l2.getClass();
            int x = Integer.MAX_VALUE;
            int y = Integer.MAX_VALUE;
            if(l1Class.isAnnotationPresent(Order.class)){
                x = ((Order)(l1Class.getAnnotation(Order.class))).value();
            }else if(l1 instanceof Ordered){
                x = ((Ordered) l1).getOrder();
            }
            if(l2Class.isAnnotationPresent(Order.class)){
                y = ((Order)(l2Class.getAnnotation(Order.class))).value();
            }else if(l2 instanceof Ordered){
                y = ((Ordered) l2).getOrder();
            }
            return Integer.compare(x,y);
        });
        return objects;
    }
}
