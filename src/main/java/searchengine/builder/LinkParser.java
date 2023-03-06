package searchengine.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkParser extends RecursiveAction {
    private final String rootUrl;
    private final ConcurrentHashMap<String, Integer> mapUrl = new ConcurrentHashMap<>();

    public LinkParser(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    @Override
    protected void compute() {

        List<LinkParser> taskList = new ArrayList<>();

        try {
            Thread.sleep(125);

            Connection connection = connectPath(rootUrl);
            int statusCode = connection.execute().statusCode();

            Document document = connection.get();
            Elements elements = document.select("a[href]");
            for (Element element : elements) {
                String newUrl = element.absUrl("href");
                if (isCorrected(newUrl)) {
                    mapUrl.put(newUrl, statusCode);
                    LinkParser linkParser = new LinkParser(newUrl);
                    linkParser.fork();
                    taskList.add(linkParser);
                }
            }
        } catch (InterruptedException | IOException exception) {
            Thread.currentThread().interrupt();
        }

        for (LinkParser task : taskList) {task.join();}

        //ВРЕМЕННЫЙ КОД
        File file = new File("data/result.txt");
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, Integer> entry : mapUrl.entrySet()) {
            printWriter.write(entry.getKey() + " " + entry.getValue() + "\n");
        }
        printWriter.flush();
        printWriter.close();

    }

    private boolean isCorrected(String url) {
        return !url.isEmpty()
                && !url.equals(rootUrl)
                && url.startsWith(rootUrl)
                && !mapUrl.contains(url)
                && !url.contains("#")
                && !url.matches("(\\S+(\\.(?i)(jpg|png|gif|bmp|pdf|xml|mp4))$)")
                && !url.matches("#([\\w\\-]+)?$")
                && !url.contains("?method=");
    }

    private Connection connectPath(String path) {
        return Jsoup.connect(path)
                .userAgent("SearchBot")
                .referrer("https://www.google.com")
                .ignoreContentType(true)
                .ignoreHttpErrors(true);
    }

}