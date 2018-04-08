package com.udacity.gradle.builditbigger.paid;

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
import android.widget.TextView;

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

    String str;
    TextView jokeTextview;
    Button jokeButton;
    private AsyncTaskCallBack mAsyncTaskCallBack;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        jokeButton = root.findViewById(R.id.jokeButton);
        jokeTextview = root.findViewById(R.id.jokeTextView);

        jokeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String s = new EndpointAsyncTask().execute(new Pair<Context, String>(getActivity(),"hello")).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
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

        Context context;

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
            jokeTextview.setText(s);
            if (mAsyncTaskCallBack != null){
                mAsyncTaskCallBack.jokeFetchCompleted();
            }
            Intent intent = new Intent(getActivity(), JokeDetailActivity.class);
            intent.putExtra("jokeKey",s);
            startActivity(intent);
        }
    }
}
