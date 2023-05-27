import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
public class MonthlySalaryCalculator {
  public static void main(String[] args){
      //  WorkingResult.csvのパス ※「C:\WorkSpace」直下に配置していない場合は適宜変更してください。
      final String WORKING_RESULT_FILE_PATH = "/Users/nakayamakenta/IdeaProjects/Ex_02/src/WorkingResult.csv";
      // コンマ
      final String COMMA = ",";

      // 計算用の数値を定数で用意
      final long ONE_MIN_BY_MILLI_SEC  = 1000 * 60;      // 1分のミリ秒換算

      int totalSalary = 0;

      List<String> workingResults = new ArrayList<>(); //ファイルから読み込んだデータの格納用

      //  WorkingResult.csvを読み込む
      try {
          // WorkingResult.csvの読み込み準備
          File workingResultFile = new File(WORKING_RESULT_FILE_PATH);
          BufferedReader br = new BufferedReader(new FileReader(workingResultFile));

          // WorkingResult.csvを1行ずつ読み込んでArrayListに格納する
          String recode = br.readLine();
          while (recode != null) {
              workingResults.add(recode);
              recode = br.readLine();
          }
          br.close();
      } catch (IOException e) {
          System.out.println(e);
      }

      // ArrayListから1行ずつ取り出して日付/出勤時間/退勤時間に振り分け
      for (int i = 0; i < workingResults.size() ; i++) {

          String workingRecode    = workingResults.get(i);      // 1行ずつ文字列を取り出す
          String[] forSplitRecode = workingRecode.split(COMMA); // splitメソッドを用いてカンマ区切りで文字列を分解＆配列にそれぞれ格納

          Time startTime   = Time.valueOf(forSplitRecode[1]); // 出勤時間
          Time finishTime  = Time.valueOf(forSplitRecode[2]); // 退勤時間

          // getTimeメソッドを使って労働時間をミリ秒（0.001秒単位）で取得する
          long workingTime = finishTime.getTime() - startTime.getTime();

          // ミリ秒で取得した労働時間を○時間△分の形式に直す
          int workingMin  = (int) ( workingTime / ONE_MIN_BY_MILLI_SEC ); // 分に換算

          int salary = salaryCalculator(workingMin).intValue();

          totalSalary += salary;
      }
      System.out.println(totalSalary);
  }

    static BigDecimal salaryCalculator(int workingMin) {
        // 計算用の数値を定数で用意

        int actualWorkingMin = actualWorkingHoursCalculation(workingMin);

        BigDecimal totalSalary = payrollCalculation(actualWorkingMin);

        // 出力
        System.out.println("本日の給与は" + totalSalary +  "です。");
        return totalSalary;
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
            BigDecimal overtimeWorkSalary = ((overtimeHours.multiply(hourlyWage)).multiply(overtimePayRate)).setScale(0, RoundingMode.DOWN);
            BigDecimal salary = (nourmalSalary.add(overtimeWorkSalary));
            return salary;
        } else {
            BigDecimal workingHours = new BigDecimal(String.valueOf((double) (actualWorkingMin)/ONE_HOUR));
            BigDecimal salary = (workingHours.multiply(hourlyWage)).setScale(0,RoundingMode.DOWN);
            return salary;
        }
    }
}
