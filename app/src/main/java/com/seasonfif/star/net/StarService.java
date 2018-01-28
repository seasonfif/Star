package com.seasonfif.star.net;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by lxy on 2018/1/27.
 */

public interface StarService {

    @GET("/users/{username}/starred")
    Observable<Response> userStarredReposList(@Path("username") String username, @Query("sort") String sort);

    @GET("/users/{username}/starred")
    Observable<Response> userStarredReposList(@Path("username") String username, @Query("sort") String sort,
                              @Query("page") int page, @Query("per_page") int per_page);
}
