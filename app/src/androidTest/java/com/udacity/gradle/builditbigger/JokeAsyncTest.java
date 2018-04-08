package com.udacity.gradle.builditbigger;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;
import static android.support.test.InstrumentationRegistry.getInstrumentation;

import com.leafnext.javajokeslibrary.Jokes;
import com.udacity.gradle.builditbigger.free.MainActivity;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



    @RunWith(AndroidJUnit4.class)
    public class JokeAsyncTest {

    private MainActivity mMainActivity = null;

    @Rule
   public final ActivityTestRule<MainActivity> mActivityActivityTestRule = new
        ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp()throws Exception{
        mMainActivity = mActivityActivityTestRule.getActivity();
    }


    @Test
    public void testAsyncTaskResult(){

        Jokes jokes = new Jokes();
        String currentJoke = jokes.getJokes();

        Assert.assertNotNull(mMainActivity);

        final Button jokeButton = mMainActivity.fragment.getJokeButton();

        final Object syncObject = new Object();

        mMainActivity.fragment.setAsyncTaskCallBack(new AsyncTaskCallBack() {
            @Override
            public void jokeFetchCompleted() {
                synchronized (syncObject){
                    syncObject.notify();
                }
            }
        });

        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                jokeButton.performClick();
            }
        });

        synchronized (syncObject){
            try {
                syncObject.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        getInstrumentation().waitForIdleSync();

        Assert.assertEquals(mMainActivity.fragment.jokeReturned,currentJoke);

}


}
