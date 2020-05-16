package com.example.welcomebot;

import ai.api.AIServiceContext;
import ai.api.android.AIDataService;

import android.app.Activity;
import android.os.AsyncTask;

import ai.api.AIServiceContext;
import ai.api.AIServiceException;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import androidx.fragment.app.Fragment;

/**
 * The class makes use of method to handle dialogflow requests in asnc manner
 */
public class RequestTask extends AsyncTask<AIRequest, Void, AIResponse> {

    Activity activity;
    private ChatFragment chatFragment;
    private TranslateFragment translateFragment;
    static final String BROADCAST_ACTION = "CALL_FUNCTION";
    String callingClass = "NA";


    Fragment fragment;
    private AIDataService aiDataService;
    private AIServiceContext customAIServiceContext;


    RequestTask(ChatFragment chatFragment,Activity activity, AIDataService aiDataService, AIServiceContext customAIServiceContext){
        this.chatFragment = chatFragment;
        this.activity = activity;
        this.aiDataService = aiDataService;
        this.customAIServiceContext = customAIServiceContext;
        this.callingClass = "chat";
    }

    RequestTask(TranslateFragment translateFragment,Activity activity, AIDataService aiDataService, AIServiceContext customAIServiceContext){
        this.translateFragment = translateFragment;
        this.activity = activity;
        this.aiDataService = aiDataService;
        this.customAIServiceContext = customAIServiceContext;
        this.callingClass = "translate";
    }

    @Override
    protected AIResponse doInBackground(AIRequest... aiRequests) {
        final AIRequest request = aiRequests[0];
        try {
            if(this.callingClass.equals("chat")){
                return aiDataService.request(request, customAIServiceContext);
            }else{
                return null;
            }

        } catch (AIServiceException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(AIResponse aiResponse) {
        //((MainActivity)activity).callback(aiResponse);
        //((ChatFragment)fragment).callback(aiResponse);
        if(this.callingClass.equals("chat")){
            chatFragment.callback(aiResponse);
        }
        else{
            translateFragment.callback();

        }




    }


}
