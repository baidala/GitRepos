package ua.itstep.android11.gitrepos;

import android.app.Application;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maksim Baydala on 22/09/17.
 */

public class MainApp extends Application {

    private static IGitHubApi gitHubApi;
    private Retrofit retrofit;
    private OkHttpClient client;

    @Override
    public void onCreate() {
        super.onCreate();

        client = new OkHttpClient.Builder()
                .addInterceptor(new GitInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.github.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        gitHubApi = retrofit.create(IGitHubApi.class);

    }

    public static IGitHubApi getApi() {
        return gitHubApi;
    }

}
