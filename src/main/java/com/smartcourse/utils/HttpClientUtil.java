package com.smartcourse.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Http工具类
 */
@Slf4j
public class HttpClientUtil {

    static final int TIMEOUT_MSEC = 5 * 1000;

    /**
     * 发送GET方式请求
     * @param url 请求url
     * @param paramMap 参数列表
     * @return 请求获取到的数据
     */
    public static String doGet(String url, Map<String,String> paramMap) {
        // 创建httpClient对象
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder builder = new URIBuilder(url);
            String result;

            // 添加url参数
            if (paramMap != null && !paramMap.isEmpty()) {
                for (String key : paramMap.keySet()) {
                    builder.addParameter(key, paramMap.get(key));
                }
            }
            URI uri = builder.build();

            // 创建GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 发送请求
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    result = EntityUtils.toString(entity, "UTF-8");
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("执行GET请求时发生异常：{}", e.getMessage());
        }

        return null;
    }


    /**
     * 发送POST方式请求
     * @param url 请求路径
     * @param paramMap 参数
     * @return 响应结果
     */
    public static String doPost(String url, Map<String, String> paramMap) {
        // 创建httpClient对象
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建POST请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(builderRequestConfig());

            // 构建参数列表
            if (paramMap != null && !paramMap.isEmpty()) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }

                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }

            // 执行请求
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    return EntityUtils.toString(entity, "UTF-8");
                }
            }
        } catch (Exception e){
            log.error("执行POST请求时发生异常：{}", e.getMessage());
        }

        return null;
    }

    /**
     * 发送POST方式请求
     * @param url 请求路径
     * @param paramMap 请求参数
     * @return 响应数据
     */
    public static String doPost4Json(String url, Map<String, String> paramMap) {
        // 创建httpClient对象
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建httpPost对象
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(builderRequestConfig());

            if (paramMap != null && !paramMap.isEmpty()) {
                // 构造json格式数据
                JSONObject jsonObject = new JSONObject();
                jsonObject.putAll(paramMap);

                StringEntity entity = new StringEntity(jsonObject.toString(),"utf-8");

                //设置请求编码
                entity.setContentEncoding("utf-8");
                //设置数据类型
                entity.setContentType("application/json");
                httpPost.setEntity(entity);

                // 执行请求
                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        return EntityUtils.toString(response.getEntity(), "utf-8");
                    }
                }
            }
        } catch (Exception e) {
            log.error("执行POST-JSON格式请求时发生异常：{}", e.getMessage());
        }

        return null;
    }

    private static RequestConfig builderRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_MSEC)
                .setConnectionRequestTimeout(TIMEOUT_MSEC)
                .setSocketTimeout(TIMEOUT_MSEC).build();
    }
}
