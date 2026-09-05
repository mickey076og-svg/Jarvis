package com.ghostjarvis.ai;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    TextToSpeech tts;
    TextView log;
    EditText apiKeyInput;
    String GROQ_KEY = ""; // Put free key from groq.com here

    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        LinearLayout lay=new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.setPadding(40,60,40,40);
        lay.setBackgroundColor(0xFF0D1117);

        TextView title=new TextView(this);
        title.setText("GHOST JARVIS\nONLINE AI 🇮🇹\nControls Any Device");
        title.setTextSize(24);
        title.setTextColor(0xFF00FF88);
        title.setGravity(1);
        lay.addView(title);

        apiKeyInput=new EditText(this);
        apiKeyInput.setHint("Paste Groq API Key (free from groq.com) - Optional");
        apiKeyInput.setBackgroundColor(0xFFFFFFFF);
        lay.addView(apiKeyInput);

        log=new TextView(this);
        log.setText("Status: Ready\n\nTry saying:\n• Open YouTube\n• Open WhatsApp\n• Volume up\n• Brightness max\n• Search Elon Musk\n• Call 07...\n• Open Ghost Football\n\nI work ONLINE!");
        log.setTextColor(0xFFFFFFFF);
        log.setTextSize(14);
        log.setPadding(0,20,0,20);
        lay.addView(log);

        Button btn=new Button(this);
        btn.setText("🎤 TAP TO SPEAK TO JARVIS");
        btn.setTextSize(18);
        lay.addView(btn);

        Button btn2=new Button(this);
        btn2.setText("⚙️ SET BRIGHTNESS / VOLUME TEST");
        lay.addView(btn2);

        setContentView(lay);

        tts=new TextToSpeech(this,s->{
            if(s==TextToSpeech.SUCCESS){
                tts.setLanguage(new Locale("it","IT"));
                tts.setSpeechRate(1.1f);
                speak("Ciao boss! Sono Ghost Jarvis online, pronto a controllare tutto!");
            }
        });

        btn.setOnClickListener(v-> startListening());
        btn2.setOnClickListener(v->{
            AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
            speak("Systems at maximum boss!");
        });
    }

    void startListening(){
        Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to JARVIS");
        try{ startActivityForResult(i,100); log.setText("🎧 Listening..."); }
        catch(Exception e){ log.setText("Speech not supported on this device"); }
    }

    @Override
    protected void onActivityResult(int req,int res, Intent data){
        super.onActivityResult(req,res,data);
        if(req==100 && res==RESULT_OK && data!=null){
            String cmd=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            log.setText("You said: "+cmd+"\n\nThinking...");
            handleCommand(cmd.toLowerCase());
        }
    }

    void handleCommand(String c){
        try{
            if(c.contains("youtube")){
                openApp("youtube", "https://youtube.com", "YouTube");
            } else if(c.contains("whatsapp")){
                openApp("whatsapp", "https://whatsapp.com", "WhatsApp");
            } else if(c.contains("tiktok")){
                openApp("tiktok", "https://tiktok.com", "TikTok");
            } else if(c.contains("facebook")){
                openApp("facebook", "https://facebook.com", "Facebook");
            } else if(c.contains("instagram")){
                openApp("instagram", "https://instagram.com", "Instagram");
            } else if(c.contains("ghost") && c.contains("football")){
                Intent intent=getPackageManager().getLaunchIntentForPackage("com.ghostfootball.game");
                if(intent!=null){ startActivity(intent); speak("Launching Ghost Football! GOOOOL!"); }
                else{ openApp("ghost", "https://github.com", "Ghost Football"); }
            } else if(c.contains("open")){
                String appName=c.replace("open","").trim();
                boolean opened=openAppByName(appName);
                if(!opened) searchOnline(c);
            } else if(c.contains("volume up")){
                AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);
                am.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                speak("Volume up boss");
            } else if(c.contains("volume down")){
                AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);
                am.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                speak("Volume down");
            } else if(c.contains("brightness")){
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, c.contains("max")?255:150);
                speak("Brightness adjusted");
            } else if(c.contains("call")){
                String num=c.replaceAll("[^0-9+]","");
                if(num.length()>=7){
                    Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+num));
                    startActivity(intent);
                    speak("Calling "+num);
                }
            } else if(c.contains("search") || c.contains("who is") || c.contains("what is")){
                searchOnline(c);
            } else {
                // Online AI Brain fallback
                searchOnline(c);
            }
            log.setText("✅ Done: "+c);
        }catch(Exception e){
            log.setText("❌ Error: "+e.getMessage());
            speak("Sorry boss, I failed");
        }
    }

    boolean openAppByName(String name){
        PackageManager pm=getPackageManager();
        for(ApplicationInfo ai: pm.getInstalledApplications(0)){
            String label=ai.loadLabel(pm).toString().toLowerCase();
            if(label.contains(name)){
                Intent i=pm.getLaunchIntentForPackage(ai.packageName);
                if(i!=null){ startActivity(i); speak("Opening "+label); return true; }
            }
        }
        return false;
    }

    void openApp(String pkgKeyword, String webFallback, String speakName){
        if(openAppByName(pkgKeyword)) return;
        Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(webFallback));
        startActivity(i);
        speak("Opening "+speakName+" online");
    }

    void searchOnline(String query){
        speak("Searching online for "+query);
        Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q="+Uri.encode(query)));
        startActivity(i);
        // If you add Groq key, it will answer with AI voice
        if(!apiKeyInput.getText().toString().isEmpty()){
            GROQ_KEY=apiKeyInput.getText().toString().trim();
            new Thread(()->{
                String ans=JarvisBrain.askGroq(GROQ_KEY, query);
                runOnUiThread(()->{ log.setText("JARVIS AI: "+ans); speak(ans); });
            }).start();
        }
    }

    void speak(String t){
        if(tts!=null) tts.speak(t, TextToSpeech.QUEUE_FLUSH, null, null);
    }
          }
