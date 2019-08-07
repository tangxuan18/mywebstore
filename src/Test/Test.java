package Test;

import utils.MailUtils;

import java.util.ArrayList;
import java.util.List;

public class Test {

    @org.junit.Test
    public void test(){
        List list = new ArrayList<>();
        list.add("刘亦菲");
        list.add(18);
        list.add(9.99);
        System.out.println("list = " + list);
        System.out.println(list.toArray());
    }

    @org.junit.Test
    public void testMail(){
        MailUtils.sendMail("912841857@qq.com", "hehe");
    }


}
