package hwp;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.reader.HWPReader;
import kr.dogfoot.hwplib.textextractor.TextExtractMethod;
import kr.dogfoot.hwplib.textextractor.TextExtractor;

import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by BISHOP on 2018-07-28.
 *
 */
public class FileDownloader extends Thread{
    public static final String FILE_TYPE_HANGUL = ".hwp";
    private static final String DOWNLOAD_URL = "https://www.smpa.go.kr/common/attachfile/attachfileDownload.do?attachNo=%s";
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while(true){
            try {
                String id = queue.take();
                download(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String download(String id) throws Exception {
        Auth.setSSL();
        InputStream in = new URL(String.format(DOWNLOAD_URL, id)).openStream();

        String day = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        FileOutputStream outputStream = new FileOutputStream(new File("E:\\download\\"+ day+"_"+id+".hwp"));

        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = in.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }

        outputStream.flush();
        outputStream.close();;

        // 파일을 읽는다. "E:\\lib\\집회.hwp"
        HWPFile hwpFile = HWPReader.fromFile("E:\\download\\"+ day+"_"+id+".hwp");

        // 파일에서 첫번째 구역을 얻는다.
        Section s = hwpFile.getBodyText().getSectionList().get(0);

        String hwpText = TextExtractor.extract(hwpFile, TextExtractMethod.InsertControlTextBetweenParagraphText);
        // 첫번째 구역에서 첫번째 문단을 얻는다.

        System.out.println(hwpText);

        System.out.println(s.getParagraphCount());
        for(int j = 0; j < s.getParagraphCount(); j++){
            Paragraph p = s.getParagraph(j);
            System.out.println(p.getMemoList());
        }
        return "";
    }

    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException {
        FileDownloader downloader = new FileDownloader();
//        00219257 00219257
//        00219256
//        00219255
//        00219225
//        00219186
//        00219145
//        00219109
//        00219064
//        00219063
//        00219062
        downloader.addSmpaId("00157431");
        downloader.start();


//        try(InputStream in = new URL(String.format(URL, "00219255")).openStream()){
//
//            /*Paragraph p = s.getParagraph(0);
//
//            byte[] str = new byte[p.getText().getCharList().size()];
//            for(HWPChar c : p.getText().getCharList()){
//                str[p.getText().getCharList().indexOf(c)] = (byte) c.getCode();
//                //System.out.println(c.getCode());
//            }
//            System.out.println(new String(str));*/
//            /*
//            File hwp = new File("E:\\lib\\집회.hwp"); // 텍스트를 추출할 HWP 파일
//            Writer writer = new StringWriter(); // 추출된 텍스트를 출력할 버퍼
//            HwpTextExtractor.extract(hwp, writer); // 파일로부터 텍스트 추출
//            String text = writer.toString(); // 추출된 텍스트
//            System.out.println(text);*/
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void addSmpaId(String id){
        this.queue.add(id);
    }
}
