import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JavaTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(1,1);
        // 创建 SimpleDateFormat 实例，指定目标格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS00");
        // 将毫秒数转换为 Date 对象
        Date date = new Date(System.currentTimeMillis());
        String ret = sdf.format(date);
        System.out.println("ret is " + ret);

        assertEquals(ret.length(), 12);
    }
}
