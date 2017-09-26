package ua.itstep.android11.gitrepos;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Maksim Baydala on 23/09/17.
 */
public class GitInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        // Customize the request
        String customUrl = original.url().toString();
        customUrl = customUrl.replace("%26", "&");
        customUrl = customUrl.replace("%3D", "=");
        customUrl = customUrl.replace("%2B", "+");

        Request request = original.newBuilder()
                .header("Accept", "application/json")
                .url(customUrl)
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }
}
