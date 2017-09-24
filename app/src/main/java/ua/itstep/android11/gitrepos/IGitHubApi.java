package ua.itstep.android11.gitrepos;

/**
 * Created by Maksim Baydala on 22/09/17.
 */


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public interface IGitHubApi {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json; charset=utf-8"
    })

    // https://api.github.com/search/users?q={name}+type:org
    @GET("/search/users")
    Call<ModelsList> getData(@Query("q") String name);


}
