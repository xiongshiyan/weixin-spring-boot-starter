package top.jfunc.weixin.utils;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiongshiyan at 2018/9/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SortUtilTest {
    @Test
    public void testSort(){
        List<Object> list = new ArrayList<>();
        list.add(new Bean1());
        list.add(new Bean2());
        list.add(new Bean3());
        list.add(new Bean4());
        list.add(new Bean5());
        SortUtil.sort(list);
        Assert.assertTrue(list.get(0) instanceof Bean3);
        Assert.assertTrue(list.get(1) instanceof Bean4);
        Assert.assertTrue(list.get(2) instanceof Bean1);
        Assert.assertTrue(list.get(3) instanceof Bean2);
        Assert.assertTrue(list.get(4) instanceof Bean5);
    }

    private static class Bean1 implements Ordered {
        @Override
        public int getOrder() {
            return 5;
        }
    }
    private static class Bean2 implements Ordered{
        @Override
        public int getOrder() {
            return 6;
        }
    }
    @Order(3)
    private static class Bean3{

    }
    @Order(4)
    private static class Bean4{

    }
    private static class Bean5{

    }
}
