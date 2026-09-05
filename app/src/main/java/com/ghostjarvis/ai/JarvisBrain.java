package com.ghostjarvis.ai;
import java.io.*;
import java.net.*;

public class JarvisBrain {
    // FREE Groq API - get key from https://console.groq.com/keys
    public static String askGroq(String apiKey, String question){
        if(apiKey==null || apiKey.isEmpty()) return "Add Groq API key for online AI brain";
        try{
            URL url=new URL("https://api.groq.com/openai/v1/chat/completions");
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization","Bearer "+apiKey);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setDoOutput(true);
            String json="{\"model\":\"llama3-8b-8192\",\"messages\":[{\"role\":\"system\",\"content\":\"You are GHOST JARVIS, a helpful AI that controls Android devices. Answer short, in Italian-English mix, funny, max 20 words.\"},{\"role\":\"user\",\"content\":\""+question.replace("\"","")+"\"}]}";
            OutputStream os=conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line, resp="";
            while((line=br.readLine())!=null) resp+=line;
            // Simple parse
            int idx=resp.indexOf("\"content\":\"");
            if(idx!=-1){
                int end=resp.indexOf("\"", idx+11+1);
                // crude but works
                String content=resp.substring(idx+11, resp.indexOf("\",", idx+11));
                content=content.replace("\\n"," ").replace("\\\"","\"");
                return content;
            }
            return "I found: "+resp.substring(0,100);
        }catch(Exception e){
            return "Online brain error: "+e.getMessage();
        }
    }
                  }
