import cn.hutool.core.util.StrUtil;
import com.joinbe.config.Constants;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;

public class Tests {


    @Test
    public void today() {
        LocalDate today = LocalDate.now();
        System.out.println(today);
        String str = "A,640000016DA85401";

        String[] split = str.split(",");
        System.out.println(split[1]);
    }

    @Test
    public void parseString() {
        // 严格按照ISO yyyy-MM-dd验证，02写成2都不行，当然也有一个重载方法允许自己定义格式
        LocalDate date = LocalDate.parse("2016-02-05");
        System.out.println(date);
    }

    @Test
    public void calculate() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        System.out.println(firstDayOfThisMonth);

        // 取本月第2天：
        LocalDate secondDayOfThisMonth = today.withDayOfMonth(2);
        System.out.println(secondDayOfThisMonth);

        // 取本月最后一天，再也不用计算是28，29，30还是31：
        LocalDate lastDayOfThisMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println(lastDayOfThisMonth);

        // 取下一天：
        LocalDate nextDay = lastDayOfThisMonth.plusDays(1);
        System.out.println(nextDay);

        // 取2015年1月第一个周一，这个计算用Calendar要死掉很多脑细胞：
        LocalDate firstMondayOf2015 = LocalDate.parse("2015-01-01").with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        System.out.println(firstMondayOf2015);
    }

    @Test
    public void getTime() {
        LocalTime now = LocalTime.now();
        System.out.println(now);
    }

    @Test
    public void getTimeWithoutMillis() {
        LocalTime now = LocalTime.now().withNano(0);
        System.out.println(now);
    }

    @Test
    public void parseTime() {
        LocalTime zero = LocalTime.of(0, 0, 0); // 00:00:00
        System.out.println(zero);

        LocalTime mid = LocalTime.parse("12:00:00"); // 12:00:00
        System.out.println(mid);
    }
}
