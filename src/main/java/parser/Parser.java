package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static Document getPage() throws IOException {
        String url = "https://pogoda.spb.ru/";
        return Jsoup.parse(new URL(url), 3000);
    }

    private static final Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

    private static String getDateFromString(String stringDate) throws Exception {
        Matcher matcher = pattern.matcher(stringDate);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new Exception("Не удается извлечь дату из строки");
    }

    private static int printFourValues(Elements values, int index) {
        int iterationCount = 4;
        Element valueLn = values.get(0);
        String text = valueLn.text();
        if (index == 0) {
            if (text.contains("Ночь")) {
                iterationCount = 5;
            } else if (text.contains("День")) {
                iterationCount = 3;
            } else if (text.contains("Вечер")) {
                iterationCount = 2;
            } else if (!text.contains("Ночь") && text.contains("Утро")) {
                iterationCount = 1;
            }
        }

        for (int i = 0; i < iterationCount; i++) {
            Element valueLine = values.get(index + i);
            for (Element td : valueLine.select("td")) {
                System.out.print(td.text() + "    ");
            }
            System.out.println();
        }
        return iterationCount;
    }

    public static void main(String[] args) throws Exception {
        Document page = getPage();
        Element tableWth = page.select("table[class=wt]").first();
        assert tableWth != null;
        Elements names = tableWth.select("tr[class=wth]");
        Elements values = tableWth.select("tr[valign=top]");
        int index = 0;
        for (Element name : names) {
            String dateString = name.select("th[id=dt]").text();
            String date = getDateFromString(dateString);
            System.out.println(date);
            int iterationCount = printFourValues(values, index);
            index = index + iterationCount;
        }
    }
}
