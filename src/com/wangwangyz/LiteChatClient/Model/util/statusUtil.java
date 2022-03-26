package com.wangwangyz.LiteChatClient.Model.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class statusUtil {
      private static Boolean network = Boolean.TRUE;
      private static String chinaz = "https://ip.chinaz.com/";
      private static StringBuilder readline = new StringBuilder("");
      private static BufferedReader bufferedReader;
      /**
       * @Func static全局静态方法,加载网页流
       * @Description 检测是否联网,打开ip查询网址并提供全局网页流
       * @Pram
       * @Return
      */
      static {
            URL url = null;
            try {
                  url = new URL(chinaz);
                  HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                  bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            }catch (UnknownHostException e){
                  network = Boolean.FALSE;
            }catch (MalformedURLException e) {
                  e.printStackTrace();
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }
      public static boolean isNetworking(){
            return network.booleanValue();
      }
      public static String getIpV4() throws UnknownHostException {
            if (isNetworking()==false){
                  return "未联网，请稍后再试";
            }
            String ipv4 = "";// TODO:优化IP数值，使用int类型替代String类型
            try {
                  String read = "";
                  while ((read = bufferedReader.readLine())!=null) {
                        readline.append(read);
                  }
            } catch (IOException e) {
                  e.printStackTrace();
            }
            Pattern ipRegex = Pattern.compile("fz24\">(.*?)<");
            Matcher ip = ipRegex.matcher(readline.toString());
            if(ip.find()){
                  ipv4 =ip.group(1);
            }else{
                  ipv4="未查询到当前主机的ip";
            }
            return ipv4;
      }
      public static String getLocation(){
            if(isNetworking()==false){
                  return "未联网,请稍后再试";
            }
            String location = "";
            try {
                  String read = "";
                  while ((read = bufferedReader.readLine())!=null) {
                        readline.append(read);
                  }
            } catch (IOException e) {
                  e.printStackTrace();
            }
            Pattern locationRegex = Pattern.compile("来自</dt>\\s+<dd>(.*?)<");
            Matcher locationAddress = locationRegex.matcher(readline.toString());
            if(locationAddress.find()){
                  location =locationAddress.group(1);
            }else{
                  location="未查询到当前主机的所在地";
            }
            return location;
      }
}
