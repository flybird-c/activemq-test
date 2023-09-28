import lombok.SneakyThrows;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : lzp
 * @date : 2023/9/12 13:58
 * @apiNote : TODO
 */
public class LogTest {
    public static void main(String[] args) {
        analyzeLogs(LogTest.class.getClassLoader().getResourceAsStream("185-dcms.log"), LogTest.class.getClassLoader().getResourceAsStream("185-upms.log"));
    }

    @SneakyThrows
    public static void analyzeLogs(InputStream logFile1, InputStream logFile2) {
        try (BufferedReader br1 = new BufferedReader(new InputStreamReader(logFile1));
             BufferedReader br2 = new BufferedReader(new InputStreamReader(logFile2))) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            String line1, line2;
            Date prevTime1 = null, prevTime2 = null;
            String prevLine1,PrevLine2;

            while ((line1 = br1.readLine()) != null && (line2 = br2.readLine()) != null) {

                try {
                    Date time1 = sdf.parse(line1.substring(0, 23));
                    Date time2 = sdf.parse(line2.substring(0, 23));

                    if (prevTime1 != null && prevTime2 != null) {
                        long diff1 = time1.getTime() - prevTime1.getTime();
                        long diff2 = time2.getTime() - prevTime2.getTime();

                        System.out.println("日志1间隔：" + diff1 + "毫秒");
                        System.out.println("日志2间隔：" + diff2 + "毫秒");

                        if (Math.abs(diff1 - diff2) > 5000) {
                            System.out.println("日志1:"+line1);
                            System.out.println("日志2:"+line2);
                            System.out.println("性能瓶颈点：日志1和日志2的间隔差异超过5秒");
                        }
                    }

                    prevTime1 = time1;
                    prevTime2 = time2;

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
