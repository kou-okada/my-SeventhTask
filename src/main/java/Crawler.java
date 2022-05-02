import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
    public static void main(String[] args) throws IOException{

            Document document = Jsoup.connect("https://baito.mynavi.jp/tokyo/city-32/ot-76/").get();
            Elements elements = document.select(".appealText");

            String headUrl = "https://baito.mynavi.jp/";

            for (Element element : elements) {
                System.out.println(element.text());
                System.out.println(headUrl + element.attr("href"));
            }

    }
}
