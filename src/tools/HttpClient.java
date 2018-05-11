package tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType Stream = MediaType.parse("multipart/form-data;");
    private static final Logger log = LoggerFactory.getLogger(Utils.class);


	private OkHttpClient client = new OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .writeTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .build();

	
    private String doGet(String url, Map<String, Object>... params) {
        return doGet(url, null,null, params);
    }

    private String doGet(String url, String cookie, String referer, Map<String, Object>... params) {
        if (null != params && params.length > 0) {
            Map<String, Object> param = params[0];
            Set<String> keys = param.keySet();
            StringBuilder sbuf = new StringBuilder(url);
            if (url.contains("=")) {
                sbuf.append("&");
            } else {
                sbuf.append("?");
            }
            for (String key : keys) {
                sbuf.append(key).append('=').append(param.get(key)).append('&');
            }
            url = sbuf.substring(0, sbuf.length() - 1);
        }
        try {
            Request.Builder requestBuilder = new Request.Builder().url(url);
            if (null != cookie) {
                requestBuilder.addHeader("Cookie", cookie);
            }
            if(null != referer) {
                requestBuilder.addHeader("Referer", referer);
            }

            Request request = requestBuilder.build();
            log.debug("[*] 请求 => {}\n", request);

            Response response = client.newCall(request).execute();
            String body = response.body().string();

            log.debug("[*] 响应 => {}", body);
            return body;
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }
  
    private JsonElement doPost(String url,String cookie,String referer,Object paramBody,Map<String, Object>... headers) {
        String bodyJson = null;
       // FormBody.Builder builder = new FormBody.Builder();
       
        RequestBody requestBody = null;
        if (null != paramBody) {
            bodyJson = Utils.toJson(paramBody);
            System.out.println(bodyJson);
            requestBody = RequestBody.create(JSON, bodyJson);
        }else{
        	requestBody = RequestBody.create(JSON, "");
        }
        Request.Builder requestBuilder = new Request.Builder().url(url).post(requestBody);
        
  
        requestBuilder.addHeader("Content-Type", "application/json; charset=utf-8");
        requestBuilder.addHeader("Content-Length", bodyJson.length()+"");
        if (null != headers && headers.length > 0) {
      	   Map<String, Object> param = headers[0];
             Set<String> keys = param.keySet();
          
             for (String key : keys) {
             	requestBuilder.addHeader(key, param.get(key)+"");
             }
      }
        if (null != cookie) {
            requestBuilder.addHeader("Cookie", cookie);
        }
        if(null != referer){
        	requestBuilder.addHeader("Referer", referer);
        } 	
        Request request = requestBuilder.build();

        log.debug("[*] 请求 => {}\n", request.headers());
        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            if (null != body && body.length() <= 300) {
                log.debug("[*] 响应 => {}", body);
            }
            if(Utils.isBlank(body)){
                return new JsonParser().parse("{}");
            }
            return new JsonParser().parse(body);
        } catch (Exception e) {
            log.error("{}", e);
            return new JsonParser().parse("{}");
        }
    }

   public static void main(String[] args) {
	   HttpClient client= new HttpClient();
	   Map<String,Object> map = new HashMap<String,Object>();
	   map.put("version", "1.0");
	   map.put("s_id", "12222");
	   
	   Map<String,Object> pmap = new HashMap<String,Object>();
	   pmap.put("account", "15066413222");
	   pmap.put("password", "93b26144aa5c955c");
	   JsonElement  json = client.doPost("http://ops.huohetech.com/login", null,null,pmap,map);
	   System.out.println(json);
   }
}
