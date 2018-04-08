package com.udacity.gradle.builditbigger.free;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.leafnext.androidlibraryjokes.JokeDetailActivity;
import com.udacity.gradle.builditbigger.AsyncTaskCallBack;
import com.udacity.gradle.builditbigger.R;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi.Builder;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivityFragment extends Fragment {



   private Button jokeButton;
    private AsyncTaskCallBack mAsyncTaskCallBack;
    public String jokeReturned;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        jokeButton = root.findViewById(R.id.jokeButton);
        jokeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                    new EndpointAsyncTask().execute(new Pair<Context, String>(getActivity(),"hello"));

            }
        });

        AdView mAdView = (AdView) root.findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        return root;
    }

    public void setAsyncTaskCallBack(AsyncTaskCallBack callBack){
        mAsyncTaskCallBack = callBack;
    }

    @VisibleForTesting
    public Button getJokeButton(){
        return jokeButton;
    }

    class EndpointAsyncTask extends AsyncTask<Pair<Context,String>,Void,String> {

        private MyApi myApiService = null;

        @Override
        protected String doInBackground(Pair<Context, String>[] pairs) {

            if (myApiService == null){
                MyApi.Builder builder = new Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),null)
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        //.setRootUrl("http://127.0.0.1:8080")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                                request.setDisableGZipContent(true);
                            }
                        });

                myApiService = builder.build();
            }

            try {
                return myApiService.sayHi("syed").execute().getData();

            }catch (IOException e){
                return e.getMessage();
            }

        }


        @Override
        protected void onPostExecute(String s) {
            jokeReturned = s;
            if (mAsyncTaskCallBack != null){
                mAsyncTaskCallBack.jokeFetchCompleted();
            }
            Intent intent = new Intent(getActivity(), JokeDetailActivity.class);
            intent.putExtra("jokeKey",s);
            startActivity(intent);
        }
    }
}
