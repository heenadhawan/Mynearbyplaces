package com.android.mygoogleplaces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUrl {
    public String ReadTheUrl(String placeURL) throws IOException{
        String  Data = "";
        InputStream inputStream = null;
        HttpURLConnection httpurlconnection = null;
        try{
            URL url =new URL(placeURL);
            httpurlconnection = (HttpURLConnection)url.openConnection();
            httpurlconnection.connect();
            inputStream = httpurlconnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line  = "";
            while ((line = bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }
            Data = stringBuffer.toString();
            bufferedReader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
        finally {
            inputStream.close();
            httpurlconnection.disconnect();
        }
        return Data;
    }
}
