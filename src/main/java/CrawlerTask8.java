import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class CrawlerTask8 {
    public static void main(String[] args) throws IOException {

        Document document = Jsoup.connect("https://baito.mynavi.jp/tokyo/city-32/ot-76/").data("q", "java").get();

        String headUrl = "https://baito.mynavi.jp";

//      基本情報の要素
        Elements infoElements = document.select(".baseInformationContent");
//      アンカータグの要素
        Elements anchorElements = document.select(".appealText");
//      ショップの名前の要素
        Elements shopNameElements = document.select(".shopName");

//        店舗名,URL,タイトル,給与,雇用形態,勤務地,アクセスを要素にもつArrayList
        List<ArrayList<String>> allInfoArr = new ArrayList<>();

        for (Element shopNameElement : shopNameElements) {
//            ショップの名前がからの時は飛ばす
            if (shopNameElement.text().isEmpty()) {
                continue;
            }

//            allInfoArr内部のArrayList
            ArrayList<String> infoArr = new ArrayList<>();
//            ショップの名前を追加
            infoArr.add(shopNameElement.text());
            allInfoArr.add(infoArr);
        }

//        広告のカウント数
        int wrapNumber = 0;
        for (Element anchorElement : anchorElements) {
//            タイトルの追加
            allInfoArr.get(wrapNumber).add(anchorElement.text());
//            URLの追加
            allInfoArr.get(wrapNumber).add(headUrl + anchorElement.attr("href"));
            wrapNumber++;
        }

        wrapNumber = 0;
//        １記事内の情報
        int infoCount = 1;

        for (Element infoElement : infoElements) {
//            1記事13行あるので13以上になったら１に戻す
            if (infoCount > 13) {
                infoCount = 1;
                wrapNumber++;
            }

//            7行目 給与、８行目 雇用形態、10行目 勤務地、12行目　アクセス　情報があるため追加
            switch (infoCount) {
                case 7, 8, 10, 12 -> allInfoArr.get(wrapNumber).add(infoElement.text());
            }

            infoCount++;

        }


        exportCSV(allInfoArr, "test1.csv");


    }


    /**
     * csvファイルを出力する関数
     * @param allInfoArr 店舗名,URL,タイトル,給与,雇用形態,勤務地,アクセスを要素にもつ多次元ArrayList
     * @param fileName 出力ファイルの名前
     */
    public static void exportCSV(List<ArrayList<String>> allInfoArr, String fileName) {
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, false), StandardCharsets.UTF_8)))) {

            printWriter.print("店舗名,タイトル,給与,雇用形態,勤務地,アクセス,URL\n");


            for (ArrayList<String> infoArr : allInfoArr) {
//                allInfoArrの内部のArrayListの番号
                int idx = 0;
                while (idx < infoArr.size()) {
//                    2番目はURLなので最後につけたいため飛ばす
                    if (idx != 2) {
                        printWriter.print(infoArr.get(idx));
                        printWriter.print(",");
                    }
                    idx++;
                }
//                URLを追加
                printWriter.println(infoArr.get(2));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
