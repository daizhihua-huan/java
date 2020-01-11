package com.example.demo.util;

import net.sf.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    private static  String Enter = "\r\n";
    // 设置边界
    private static String BOUNDARY = "----------" + System.currentTimeMillis();

    public static void main(String[] args) throws IOException {
        String setpdfurl = "http://www.daizhihua.cn/ancc-manager-web/invoice/setpdf";
        String getpdfUrl = "http://www.daizhihua.cn/ancc-manager-web/invoice/getpdf";
        String createCard = "http://www.daizhihua.cn/ancc-manager-web/invoice/createcard";
        String insertPdf = "http://www.daizhihua.cn/ancc-manager-web/invoice/insertInvoice";
        String sendMessage = "http://www.daizhihua.cn/ancc-manager-web/wXLoginController/sendmessagegzh";
        //创建会员卡的card
        /*Map<String,String>map = new HashMap<>();
        map.put("title","环宇恩维");
        map.put("logo_url","wwww.daizhihua.cn");
        map.put("type","税普通发票");
        map.put("payee","北京司");
        JSONObject jsonObject = httpUrl(createCard, map);
        System.out.println(jsonObject);*/
        //上传pdf
        /*JSONObject send = send(setpdfurl, "D:\\node\\invoice_1_.pdf", "20191100000000P21141");
        System.out.println(send);*/
        //查询上传的pdf
            /*Map<String,String> map = new HashMap<>();
            map.put("s_media_id","2595810184280998468");
            JSONObject post = httpUrl(getpdfUrl, map);*/



            //插入发票到微信卡包
         /*   Map<String,String>map = new HashMap<>();
            map.put("sn","20191100000000394742");
            map.put("s_pdf_media_id","2522505125679399468");
            map.put("money","4501");
            map.put("firmName","测试公司");
            map.put("billing_no","011001900111");
            map.put("billing_code","75494126");
            map.put("fee_without_tax","560312");
            map.put("tax","5610");
            map.put("check_code","74217645684014614757");
            map.put("branchcode","1105");
            map.put("custtaxno","0");
        JSONObject jsonObject = httpUrl(insertPdf, map);
        System.out.println(jsonObject);*/



        //发送消息

        Map<String,String> map = new HashMap<>();
        map.put("phonenum","18010091126");
        map.put("type","注册");
        map.put("status","通过");
        map.put("createTime","2018-11-12");
        JSONObject jsonObject = httpUrl(sendMessage, map);
        System.out.println(jsonObject);

    }



    public static JSONObject httpUrl(String url,Map<String,String>map) throws IOException {
            HttpURLConnection con =setHttpURLConnection(url,false);
            // 请求正文信息
            // 第一部分：
            StringBuilder sb = new StringBuilder();
            sb.append("--"); // 必须多两道线
            sb.append(BOUNDARY);
            sb.append("\r\n");
            OutputStream out = new DataOutputStream(con.getOutputStream());
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            String str = null;
            for(String param:map.keySet()){
                System.out.println(map.get(param));
                System.out.println(param);
                if(str==null){
                    str =param+"="+URLEncoder.encode(map.get(param),"UTF-8");
                }else{
                    str +="&"+param+"="+URLEncoder.encode(map.get(param),"UTF-8");
                }
                System.out.println(str);
            }
            out.write(str.getBytes());
        return sendResult(con,out,null,false);
    }



    /**
     * 将文件通过url 发送给服务器
     * @param url 服务器的url地 址
     * @param path 文件路径
     * @param sn 档案索引号
     * @return
     * @throws IOException
     */
    public static JSONObject send(String url, String path,String sn) throws IOException {
        File file = new File(path);
        if(file.exists()){
            HttpURLConnection con = setHttpURLConnection(url,true);
            // 请求正文信息
            // 第一部分：
            StringBuilder sb = new StringBuilder();
            sb.append("--"); // 必须多两道线
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"pdf\";filename=\""
                    + file.getName()+ "\"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");
            System.out.println(sb);
            byte[] head = sb.toString().getBytes("utf-8");
            //part 1
            String part1 = Enter
                    + "--" + BOUNDARY + Enter
                    + "Content-Type: text/plain" + Enter
                    + "Content-Disposition: form-data; name=\"sn\"" + Enter + Enter
                    + sn + Enter
                    + "--" + BOUNDARY + "--";
            // 获得输出流
            OutputStream out = new DataOutputStream(con.getOutputStream());
            // 输出表头
            out.write(head);


            // 文件正文部分
            // 把文件已流文件的方式 推入到url中
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            out.write(part1.getBytes());
            in.close();
            // 结尾部分
            JSONObject jsonObject = sendResult(con, out, null,true);
            return jsonObject;
        }


        return null;
    }

    public static HttpURLConnection setHttpURLConnection(String url,boolean flag) throws IOException {
        /**
         * 第一部分
         */
        URL urlObj = new URL(url);
        // 连接
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
        /**
         * 设置关键值
         */
        con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false); // post方式不能使用缓存
        con.setRequestProperty("Authorization","6zMWJNTstP7k7QSeH5rEWg==");
        // 设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        if(flag){
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary="+ BOUNDARY);
        }
        return con;
    }


    public static JSONObject sendResult(HttpURLConnection con,OutputStream out,String result,boolean flag) throws IOException {
        if(flag){
            byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
            out.write(foot);
        }

        // 当有数据需要提交时
        out.flush();
        out.close();
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                buffer.append(line);
            }
            if(result==null){
                result = buffer.toString();
            }
        } catch (IOException e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
            throw new IOException("数据读取异常");
        } finally {
            if(reader!=null){
                reader.close();
            }
        }
        JSONObject jsonObj = JSONObject.fromObject(result);
        return jsonObj;
    }


}
