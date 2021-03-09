package word.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.StringBuilder;




import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Locale;

import word.game.net.WordMeaningRequest;
import word.game.ui.dialogs.DictionaryDialog;

public class WordMeaningRequest_en implements WordMeaningRequest {

    private  DictionaryDialog.DictionaryCallback callback;
    private String word;

    @Override
    public void request(String word, DictionaryDialog.DictionaryCallback callback) {
        this.word = word;
        this.callback = callback;


        StringBuilder sb = new StringBuilder();
        sb.append("s=" + word);
        sb.append("&o8=1");
        sb.append("&o1=1");
        sb.append("&h=000000000000000");
        sb.append("&sub=Search WordNet");
        sb.append("&o2=");
        sb.append("&o0=");
        sb.append("&o7=");
        sb.append("&o5=");
        sb.append("&o9=");
        sb.append("&o6=");
        sb.append("&o3=");
        sb.append("&o4=");
        sb.append("&c=-1");


        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl("http://wordnetweb.princeton.edu/perl/webwn");
        request.setContent(sb.toString());

        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setHeader("Host", "wordnetweb.princeton.edu");
        request.setHeader("Referer", "http://wordnetweb.princeton.edu/perl/webwn");
        request.setHeader("Cookie", "_ga=GA1.2.1612812547.1582833968");
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
        request.setHeader("Upgrade-Insecure-Requests", "1");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Accept-Language", "en,tr-TR;q=0.9,tr;q=0.8,en-US;q=0.7,es;q=0.6,ru;q=0.5,fr;q=0.4,pt;q=0.3,de;q=0.2,it;q=0.1");
        request.setHeader("Accept-Encoding", "gzip, deflate");
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");


        RequestSender sender = new RequestSender();
        sender.request = request;
        sender.run();

    }


    class RequestSender implements Runnable{

        Net.HttpRequest request;

        @Override
        public void run() {
            Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(final Net.HttpResponse httpResponse) {

                    final String response = parseResponse(httpResponse.getResultAsString());
                    //parseResponse(response);
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            WordMeaningRequest_en.this.callback.onMeaning(WordMeaningRequest_en.this.word, response);
                        }
                    });

                }

                @Override
                public void failed(Throwable t) {
                    final String text = t.getMessage();
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            WordMeaningRequest_en.this.callback.onMeaning(WordMeaningRequest_en.this.word, text);
                        }
                    });
                }

                @Override
                public void cancelled() {

                }
            });
        }




        private String parseResponse(String html){

            if(html.contains("Your search did not return any results")){
                return "Your search did not return any results";
            }


            Document doc = Jsoup.parse(html);

            Elements h3 = doc.select("h3");
            Elements uls = doc.select("ul");

            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < h3.size(); i++){
                if(i > 0) sb.append("\n\n");
                sb.append(h3.get(i).text().toUpperCase(Locale.ENGLISH));



                Element ul = uls.get(i);
                Elements li = ul.select("li");
                for(int j = 0; j < li.size(); j++){
                    //sb.append(j == 0 ? "\n\n" : "\n");
                    String text = li.get(j).text().replaceFirst("S:", "*");
                    sb.append("\n\n" + text);
                    /*if(j < li.size() - 1)*/

                }
            }



            return sb.toString();
        }
    }


}
