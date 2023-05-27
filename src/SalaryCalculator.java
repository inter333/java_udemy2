import java.math.RoundingMode;
import java.sql.Time;
import java.math.BigDecimal;

public class SalaryCalculator {
    public static void main(String[] args) {
        // 計算用の数値を定数で用意
        final long ONE_MIN_BY_MILLI_SEC  = 1000 * 60;      // 1分のミリ秒換算

        // バイトの開始時間と終了時間をコマンドライン引数から受け取る
        Time startTime  = Time.valueOf(args[0]);
        Time finishTime = Time.valueOf(args[1]);

        // getTimeメソッドを使って労働時間をミリ秒（0.001秒単位）で取得する
        // ※getTime()メソッドの戻り値はlong型であることに注意
        long workingTime = finishTime.getTime() - startTime.getTime();
        int workingMin  = (int)( workingTime / ONE_MIN_BY_MILLI_SEC ) ; // 分に換算

        int actualWorkingMin = actualWorkingHoursCalculation(workingMin);

        BigDecimal totalSalary = payrollCalculation(actualWorkingMin);

        // 出力
        System.out.println("本日の給与は" + totalSalary +  "です。");
    }

    static int actualWorkingHoursCalculation(int workingMin){
        final int REGULAR_WORK_HOURS = 480;
        final int SHORT_WORK_HOURS = 360;
        final int MINIMUM_REST_TIME = 45;
        final int MAX_REST_TIME = 60;


        if (workingMin > REGULAR_WORK_HOURS){
            return workingMin - MAX_REST_TIME ;
        } else if (workingMin > SHORT_WORK_HOURS) {
            return workingMin - MINIMUM_REST_TIME;
        } else {
            return workingMin;
        }
    }

    static BigDecimal payrollCalculation(int actualWorkingMin){
        final int REGULAR_WORK_HOURS = 480;
        final int ONE_HOUR = 60;
        BigDecimal hourlyWage = new BigDecimal("900");
        BigDecimal overtimePayRate = new BigDecimal("1.25");

        if (actualWorkingMin > REGULAR_WORK_HOURS){
            BigDecimal normalWorkingHours = new BigDecimal(String.valueOf((REGULAR_WORK_HOURS)/ONE_HOUR));
            BigDecimal overtimeHours = new BigDecimal(String.valueOf((double) (actualWorkingMin - REGULAR_WORK_HOURS)/ONE_HOUR));

            BigDecimal nourmalSalary = (normalWorkingHours.multiply(hourlyWage));
            BigDecimal overtimeWorkSalary = ((overtimeHours.multiply(hourlyWage)).multiply(overtimePayRate)).setScale(0,RoundingMode.DOWN);
            BigDecimal salary = (nourmalSalary.add(overtimeWorkSalary));
            return salary;
        } else {
            BigDecimal workingHours = new BigDecimal(String.valueOf((double) (actualWorkingMin)/ONE_HOUR));
            BigDecimal salary = (workingHours.multiply(hourlyWage)).setScale(0,RoundingMode.DOWN);
            return salary;
        }

    }
}