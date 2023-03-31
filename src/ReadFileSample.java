import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/*
 *   WorkingResult.csv（一か月分の勤務実績ファイル）を読み込み、各日の労働時間を計算して出力する処理です。
 *   このサンプルではWorkingResult.csvを「C:\WorkSpace」直下に配置して実行する前提になっています。
 *   ディレクトリの名前や配置場所が違う場合は WORKING_RESULT_FILE_PATH の値を適切なものに変更してください。
 */

public class ReadFileSample {
	public static void main(String[] args) {
		
		//  WorkingResult.csvのパス ※「C:\WorkSpace」直下に配置していない場合は適宜変更してください。
		final String WORKING_RESULT_FILE_PATH = "/Users/nakayamakenta/IdeaProjects/Ex_02/src/WorkingResult.csv";
		// コンマ
		final String COMMA = ","; 
		
		// 計算用の数値を定数で用意
		final long ONE_HOUR_BY_MILLI_SEC = 1000 * 60 * 60; // 1時間のミリ秒換算
		final long ONE_MIN_BY_MILLI_SEC  = 1000 * 60;      // 1分のミリ秒換算
		final int  ONE_HOUR_BY_MIN       = 60;             // 1時間の分換算
		
		List<String> workingResults = new ArrayList<String>(); //ファイルから読み込んだデータの格納用
		
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
			
			Date workingDate = Date.valueOf(forSplitRecode[0]); // 出勤日
			Time startTime   = Time.valueOf(forSplitRecode[1]); // 出勤時間
			Time finishTime  = Time.valueOf(forSplitRecode[2]); // 退勤時間
			
			// getTimeメソッドを使って労働時間をミリ秒（0.001秒単位）で取得する
			long workingTime = finishTime.getTime() - startTime.getTime();
			
			// ミリ秒で取得した労働時間を○時間△分の形式に直す
			int workingHour = (int)( workingTime / ONE_HOUR_BY_MILLI_SEC );                      // 時間に換算
			int workingMin  = (int)( ( workingTime / ONE_MIN_BY_MILLI_SEC ) % ONE_HOUR_BY_MIN ); // 分に換算
			
			// 出力
			System.out.println( "【日付】"     + workingDate + " / " +
			                    "【勤務時間】" + startTime + "～" + finishTime + " / " +
			                    "【労働時間】" + workingHour + "時間" + workingMin + "分"
			                  );
		}
	}
}
