package hwp;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by BISHOP on 2018-07-28.
 */
public class SmpaIdGrabber extends Thread{
    private FileDownloader downloader = new FileDownloader();
    private final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
    private final static String URL = "https://www.smpa.go.kr/user/nd54882.do";

    @Override
    public void run() {
        try {
            Auth.setSSL();

            // HTML 가져오기
            Connection conn = Jsoup
                    .connect(URL)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .userAgent(USER_AGENT)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true);

            Document doc = conn.get();
            System.out.println(conn.execute().method());
            Elements elements = doc.select("table").select("tbody").select("tr").select("td").select("a");

            elements.forEach(element -> {
                List<String> strs = new ArrayList<>(Arrays.asList(element.attr("href").split(",")));
                if(strs.size()==0)
                    return;
                String id = strs.get(strs.size()-1).replaceAll("'", "").replaceAll("\\)","").replaceAll(";", "");
                this.downloader.add_SmpaId(id);
            });

        } catch (IOException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}

//private final static String script = "https://www.smpa.go.kr/resource/js/script-process.js";
//private final static String URL_FORMAT = "https://www.smpa.go.kr/user/nd54882.do?View&uQ=&pageST=SUBJECT&pageSV=&imsi=imsi&page=2&pageSC=SORT_ORDER&pageSO=DESC&dmlType=&boardNo=%d&satisfact_score=5&satisfact_score=4&satisfact_score=3&satisfact_score=2&satisfact_score=1&satisMenuCode=www&satisMenuTitle=%EC%98%A4%EB%8A%98%EC%9D%98%20%EC%A7%91%ED%9A%8C/%EC%8B%9C%EC%9C%84&satisMenuId=nd54882&returnUrl=https://www.smpa.go.kr:443/user/nd54882.do";
//private final static String ur = "https://www.smpa.go.kr/user/nd54882.do?View&uQ=&pageST=SUBJECT&pageSV=&imsi=imsi&page=1&pageSC=SORT_ORDER&pageSO=DESC&dmlType=&boardNo=00219064&satisfact_score=5&satisfact_score=4&satisfact_score=3&satisfact_score=2&satisfact_score=1&satisMenuCode=www&satisMenuTitle=오늘의 집회/시위&satisMenuId=nd54882&returnUrl=https://www.smpa.go.kr:443/user/nd54882.do";
// SSL 우회 등록
//            String js = doc.body().text();
//            System.out.println(js);
//
//            //location.href
//            ScriptEngine se = new ScriptEngineManager().getEngineByName("JavaScript");
//            se.eval(js);
//
//            String i1 = "/user/nd54882.do";
//            String i2 = "View";
//            String i3 = "00219064";
//
//
//            Invocable invocable = (Invocable) se;
//            Object result = invocable.invokeFunction("goBoardView", i1, i2, i3);
//            System.out.println(result);