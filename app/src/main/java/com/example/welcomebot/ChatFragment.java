package com.example.welcomebot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.Locale;
import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatFragment extends Fragment {


    private String uuid = UUID.randomUUID().toString();
    private TextToSpeech mtts;
    private AIRequest aiRequest;
    private AIDataService aiDataService;
    private AIServiceContext customAIServiceContext;
    private LinearLayout chatLayout;
    private EditText queryEditText;
    private static final int USER = 10001;
    private static final int BOT = 10002;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String dialogFlowKey;
    FirebaseTranslator englishChineseTranslator;
    FirebaseTranslator chineseEnglishTranslator;
    FirebaseModelDownloadConditions englishToChineseConditions;
    FirebaseModelDownloadConditions chineseToEnglishConditions;
    FirebaseLanguageIdentification languageIdentifier;
    String defaultLanguage = "au";

    String translatedText="";
    String chineseTextToSpeak="";

    MaterialCardView introCardView;
    TextView tv_chatQuestions;
    TextView tv_chatIntro;
    View rootview;
    FrameLayout layout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        rootview = inflater.inflate(R.layout.fragment_chat,container,false);;
         introCardView = (MaterialCardView) rootview.findViewById(R.id.introCard);
         tv_chatIntro = (TextView) rootview.findViewById(R.id.tv_chatIntro);
         tv_chatQuestions =(TextView) rootview.findViewById(R.id.tv_chatQuestions);
        changeLabelToEnglish();

        // language detection initializer
         languageIdentifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();

        // Create an English-China translator:
        FirebaseTranslatorOptions englishToChineseOptions =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                        .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                        .build();
        englishChineseTranslator =
                FirebaseNaturalLanguage.getInstance().getTranslator(englishToChineseOptions);

        englishToChineseConditions = new FirebaseModelDownloadConditions.Builder()
                .build();

        //-------------------------------------------------------------------------------------//

        FirebaseTranslatorOptions chineseToEnglishOptions =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                        .setTargetLanguage(FirebaseTranslateLanguage.EN)
                        .build();
        chineseEnglishTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(chineseToEnglishOptions);
        chineseToEnglishConditions = new FirebaseModelDownloadConditions.Builder()
                .build();


        dialogFlowKey = getActivity().getResources().getString(R.string.dialogflow_key);


     return rootview;


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.custom_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item  = menu.findItem(R.id.app_bar_search);
        item.setVisible(false);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.app_bar_China){
            defaultLanguage ="cn";
            Toast.makeText(getActivity(), "Language switched to Chinese", Toast.LENGTH_LONG).show();
            changeLabelToChinese();

        }

        if(id == R.id.app_bar_australia){
            defaultLanguage = "au";
            Toast.makeText(getActivity(), "Language switched to English", Toast.LENGTH_LONG).show();
            changeLabelToEnglish();

        }

        return super.onOptionsItemSelected(item);
    }

    public void changeLabelToChinese(){
        StringBuilder chineseIntro =  new StringBuilder("");
        chineseIntro.append("    朋友，你好！我是聊天机器人immigos。我可以回答一些基本问题以帮助您更好地学习澳大利亚文化以及探索澳大利亚！\n" +
                "你可以通过以下方式询问我：\n");

        chineseIntro.append("• 告诉我一些有关澳大利亚有趣的事情 \n\n");
        chineseIntro.append("• 告诉我离我最近的市场（运动场所或公园）\n\n");
        chineseIntro.append("• 告诉我一个笑话 \n\n");
        chineseIntro.append("• 通过“我非常开心”或“我非常生气”来表达自己的情绪");

        SpannableStringBuilder ssb = new SpannableStringBuilder(chineseIntro.toString());
        ssb.setSpan(new ImageSpan(getActivity(), R.mipmap.launcher_icon), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv_chatIntro.setText(ssb, TextView.BufferType.SPANNABLE);


        //tv_chatIntro.setText(chineseIntro.toString());

    }

    public void changeLabelToEnglish(){


        StringBuilder englishIntro =  new StringBuilder("");
        englishIntro.append("    Hi there mate, I am Immigos chatbot. \n\nI can answer basic questions and will help you along to learn about Australian culture and explore Aussieland!\n" +
                "Go ahead and ask me ：");
        englishIntro.append("\n\n• \"Tell me some interesting facts about Australia \" or fun facts about Australia \n\n");
        englishIntro.append("• Show me the markets near me\" (parks or restaurants )\n\n");
        englishIntro.append("• Ask me to tell you a joke \n\n");
        englishIntro.append("• Express your emotions like \"I am happy\" or \"I am very angry\"");

        //tv_chatIntro.setText(englishIntro.toString());

        SpannableStringBuilder ssb = new SpannableStringBuilder(englishIntro.toString());
        ssb.setSpan(new ImageSpan(getActivity(), R.mipmap.launcher_icon), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv_chatIntro.setText(ssb, TextView.BufferType.SPANNABLE);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //ImageView imageView = (ImageView) getView().findViewById(R.id.foo);

        final ScrollView scrollview = getView().findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));


        chatLayout = getView().findViewById(R.id.chatLayout);

        ImageView sendBtn =(ImageView) getView().findViewById(R.id.sendBtn);
        //sendBtn.setClickable(true);

        queryEditText = getView().findViewById(R.id.queryEditText);
        queryEditText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        sendMessage(sendBtn);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });

        initChatbot();


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(sendBtn);
            }
        });


        // or  (ImageView) view.findViewById(R.id.foo);
    }

    private void initChatbot(){
        final AIConfiguration config =  new AIConfiguration(dialogFlowKey,
                AIConfiguration.SupportedLanguages.English,AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(getActivity(),config);
        customAIServiceContext = AIServiceContextBuilder.buildFromSessionId(uuid);
        aiRequest = new AIRequest();

    }


    public String translateToEnglish(String message){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {


                languageIdentifier.identifyLanguage(message).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String languageCode) {

                        if (languageCode != "und" && languageCode.equals("zh")) {

                            chineseEnglishTranslator.downloadModelIfNeeded(chineseToEnglishConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    chineseEnglishTranslator.translate(message).addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            translatedText=s;
                                        }
                                    });

                                }
                            });
                            //Log.i(TAG, "Language: " + languageCode);
                            //Log.i(TAG, "Language: translated" + translatedText);

                        } else {
                            translatedText =message;
                        }
                    }
                });

            }
        });

                  return translatedText;
    }

    private void fadeOutAndHideView(final MaterialCardView img)
    {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(50);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }

    public void sendMessage(View view) {

        String msg = queryEditText.getText().toString();
        //String response = translateToEnglish(msg);
        ChatFragment chFragment = this;
        if (msg.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            //introCardView.setVisibility(View.INVISIBLE);
            fadeOutAndHideView(introCardView);
            languageIdentifier.identifyLanguage(msg).addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String languageCode) {

                    if (languageCode != "und" && languageCode.equals("zh")) {

                        chineseEnglishTranslator.downloadModelIfNeeded(chineseToEnglishConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                chineseEnglishTranslator.translate(msg).addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        translatedText=s;
                                        showTextView(msg, USER);
                                        queryEditText.setText("");
                                        // Android client
                                        aiRequest.setQuery(translatedText);

                                        RequestTask requestTask = new RequestTask(chFragment,getActivity(), aiDataService, customAIServiceContext);
                                        requestTask.execute(aiRequest);
                                    }
                                });

                            }
                        });

                        //translate the text to english and then display
                        //Log.i(TAG, "Language: " + languageCode);
                        //Log.i(TAG, "Language: translated" + translatedText);

                    } else {
                        translatedText = msg;

                        showTextView(msg, USER);
                        queryEditText.setText("");
                        // Android client
                        aiRequest.setQuery(translatedText);
                        RequestTask requestTask = new RequestTask(chFragment,getActivity(), aiDataService, customAIServiceContext);

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                requestTask.execute(aiRequest);
                            }
                        });



                    }
                }
            });


/*            showTextView(msg, USER);
            queryEditText.setText("");
            // Android client
            aiRequest.setQuery(msg);
            ChatFragment chFragment;
            RequestTask requestTask = new RequestTask(this,getActivity(), aiDataService, customAIServiceContext);
            requestTask.execute(aiRequest);*/
        }
    }


    public void showTextView(String message, int type) {

        switch (type) {
            case USER:
                layout = getUserLayout();
                TextView tv = layout.findViewById(R.id.chatMsg);

                tv.setText(message);
                break;
            case BOT:

                layout = getBotLayout();

                TextView tv1 = layout.findViewById(R.id.chatMsg);
                //tv1.setText(message);

                mtts =  new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS){
                            int result = 0;
                            if(defaultLanguage.equals("cn")){
                                result = mtts.setLanguage(Locale.CHINA);
                            }
                            else{
                                result = mtts.setLanguage(Locale.ENGLISH);
                            }

                            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                                Log.e("TTs","Language Not supported");
                            }
                            else {
                                float pitch = 1.0f;
                                if (pitch< 0.1) pitch = 0.1f;
                                float speed = 1.0f;
                                mtts.setPitch(pitch);
                                mtts.setSpeechRate(speed);

                                if (defaultLanguage.equals("cn")) {
                                englishChineseTranslator.downloadModelIfNeeded(englishToChineseConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        englishChineseTranslator.translate(message).addOnSuccessListener(new OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(String s) {
                                                tv1.setText(s);
                                                chineseTextToSpeak=s;
                                                if(!chineseTextToSpeak.equals("")){
                                                    mtts.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                                if (chineseTextToSpeak.equals("")) {
                                    mtts.speak(message,TextToSpeech.QUEUE_FLUSH,null);
                                }
                            }
                        }
                        else {
                            Log.e("TTs","Language Not supported");
                        }
                    }
                });
                if(chineseTextToSpeak.equals("")){

                    tv1.setText(message);
                }

                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
        layout.requestFocus();
        queryEditText.requestFocus(); // change focus back to edit text to continue typing
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }

    public void callback(AIResponse aiResponse) {
        if (aiResponse != null) {
            // process aiResponse here
            String botReply = aiResponse.getResult().getFulfillment().getSpeech();
            Log.d(TAG, "Bot Reply: " + botReply);

            String botResponseArray [] = botReply.split("\\|\\|");
            for(int i=0;i<botResponseArray.length;i++){

                showTextView(botResponseArray[i], BOT);
            }
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was a problem please try again!", BOT);
        }
    }

    @Override
    public void onDestroy() {
        if (mtts != null){
            mtts.stop();
            mtts.shutdown();
        }
        super.onDestroy();
    }

}
