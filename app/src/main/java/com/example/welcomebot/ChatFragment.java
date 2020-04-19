package com.example.welcomebot;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        dialogFlowKey = getActivity().getResources().getString(R.string.dialogflow_key);


     return inflater.inflate(R.layout.fragment_chat,container,false);


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




    public void sendMessage(View view) {
        String msg = queryEditText.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            showTextView(msg, USER);
            queryEditText.setText("");
            // Android client
            aiRequest.setQuery(msg);
            ChatFragment chFragment;
            RequestTask requestTask = new RequestTask(this,getActivity(), aiDataService, customAIServiceContext);
            requestTask.execute(aiRequest);

        }
    }


    public void showTextView(String message, int type) {
        FrameLayout layout;
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
                            int result = mtts.setLanguage(Locale.ENGLISH);

                            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                                Log.e("TTs","Language Not supported");
                            }
                            else {
                                ;
                                float pitch = 1.0f;
                                if (pitch< 0.1) pitch = 0.1f;
                                float speed = 1.0f;
                                mtts.setPitch(pitch);
                                mtts.setSpeechRate(speed);

                                // Log.i("shreyas",text.toString());
                                mtts.speak(message,TextToSpeech.QUEUE_FLUSH,null);
                                tv1.setText(message);

                            }
                        }
                        else {
                            Log.e("TTs","Language Not supported");
                        }
                    }
                });

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
            showTextView(botReply, BOT);
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was some communication issue. Please Try again!", BOT);
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
