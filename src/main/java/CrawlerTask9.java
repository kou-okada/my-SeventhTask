import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class CrawlerTask9 {
    public static void main(String[] args) throws  IOException{
        Map<String, List<String>> map = createMap();
        scraping(map);
        System.out.println(map.get("アクセス").size());
        exportCSV(map, "./test01.csv");

    }

    /**
     * 求人情報を取得する関数
     * @param map 値を入れるmap
     */
    private static void scraping(Map<String, List<String>> map) {
//        求人情報の数
        int researchCount = 1;
//        HPのページ数
        int pageNo = 1;
        try {
            while (researchCount > 0) {
                Document document = Jsoup.connect("https://baito.mynavi.jp/tokyo/city-32/ot-76/kd-11-16/").data("pageNo", String.valueOf(pageNo)).get();

//                ページ数が1の時だけに件数を取得
                if (pageNo == 1) {
                    researchCount = Integer.parseInt(document.select(".researchCount span").text());
                }

                Elements baseInfoElements = document.select(".appealText.jobOfferGTM");
                Elements shopNameElements = document.select(".shopName");
                Elements detailsElements = document.select(".baseInformationContent.floatL");

                for (Element shopName : shopNameElements) {
//                    textがからの時は飛ばす
                    if (shopName.text().isEmpty()) {
                        continue;
                    }
//                    店舗名がkeyのArrayListに追加
                    map.get("店舗名").add(shopName.text());
//                    件数を一つ減らす
                    researchCount--;
                }

                for (Element baseInfoElement : baseInfoElements) {
//                    textが空、urlが空の時は飛ばす
                    if (baseInfoElement.text().isEmpty() || baseInfoElement.attr("href").isEmpty()) {
                        continue;
                    }
//                    タイトルがkeyのArrayListに追加
                    map.get("タイトル").add(baseInfoElement.text());

//                    URLがkeyのArrayListに追加
                    map.get("URL").add("https://baito.mynavi.jp" + baseInfoElement.attr("href"));


                    Thread.sleep(1000); // 10秒(1万ミリ秒)間だけ処理を止める
                    String url = "https://baito.mynavi.jp" + baseInfoElement.attr("href");
//                    掲載期間はurlに入らないと取得できないため、接続
                    Document detailDocument = Jsoup.connect(url).get();
                    Elements postingTerm = detailDocument.select(".postingTerm > span");
                    String term = postingTerm.text();
//                    掲載期間がkeyのArrayListに追加
                    map.get("掲載期間").add(term);


                }

//                取得した求人情報が何行目かカウント
                int count = 1;
                for (Element detailElement : detailsElements) {
//                    １求人情報13行あるため超えたら１に戻す
                    if (count > 13) {
                        count = 1;
                    }
//                    Java12以降のため拡張switch
                    switch (count) {
                        case 7 -> map.get("給与").add(detailElement.text());

                        case 8 -> map.get("雇用形態").add(detailElement.text());

                        case 10 -> map.get("勤務地").add(detailElement.text());

                        case 12 -> map.get("アクセス").add(detailElement.text());

                    }

//                    次の行数へ
                    count++;
                }


                Thread.sleep(1000); // 10秒(1万ミリ秒)間だけ処理を止める

                pageNo++;
            }


        } catch (NullPointerException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * "店舗名", "タイトル", "給与", "雇用形態", "勤務地", "アクセス", "URL", "掲載期間"をkeyにもつmapを作成する関数
     * @return "店舗名", "タイトル", "給与", "雇用形態", "勤務地", "アクセス", "URL", "掲載期間"をkeyにArraylistをvalueにもつmap
     */

    private static Map<String, List<String>> createMap() {
        Map<String, List<String>> map = new HashMap<>();
        String[] headerNames = {"店舗名", "タイトル", "給与", "雇用形態", "勤務地", "アクセス", "URL", "掲載期間"};
        for (String headerName : headerNames) {
            List<String> list = new ArrayList<>();
            map.put(headerName, list);
        }
        return map;
    }

    /**
     * csvファイルを出力する関数
     * @param map 値の代入が完了したmap
     * @param filepath　どこにファイルを出力するか
     */
    private static void exportCSV(Map<String, List<String>> map, String filepath) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filepath)), StandardCharsets.UTF_8))) {
            CSVPrinter printer = CSVFormat.EXCEL.withQuoteMode(QuoteMode.ALL).withHeader("店舗名", "タイトル", "給与", "雇用形態", "勤務地", "アクセス", "URL", "掲載期間").print(bw);

            for (int idx = 0; idx < map.get("店舗名").size(); idx++) {
                printer.printRecord(map.get("店舗名").get(idx), map.get("タイトル").get(idx), map.get("給与").get(idx), map.get("雇用形態").get(idx), map.get("勤務地").get(idx), map.get("アクセス").get(idx), map.get("URL").get(idx), map.get("掲載期間").get(idx));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ie){
            ie.printStackTrace();
        }

    }


}
