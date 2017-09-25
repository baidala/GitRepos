package ua.itstep.android11.gitrepos;

/**
 * Created by Maksim Baydala on 22/09/17.
 */


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface IGitHubApi {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json; charset=utf-8"
    })

    // https://api.github.com/search/users?q={login}+type:org
    @GET("/search/users")
    Call<OrgModelsList> getOrganization(@Query("q") String login);

    // "https://api.github.com/users/{login}/repos"
    @GET("users/{login}/repos")
    Call<List<ReposModel>> getRepos(@Path("login") String login);
}
