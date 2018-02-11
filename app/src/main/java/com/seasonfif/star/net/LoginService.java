package com.seasonfif.star.net;

import android.support.annotation.NonNull;
import com.seasonfif.star.model.BasicToken;
import com.seasonfif.star.model.CreateAuthorization;
import com.seasonfif.star.model.OauthToken;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface LoginService {

    @POST("/authorizations")
    Observable<Response<BasicToken>> authorizations(
        @NonNull @Body CreateAuthorization createAuthorization);

    @POST("/login/oauth/access_token")
    Observable<Response<OauthToken>> getAccessToken(
        @Query("client_id") String clientId, @Query("client_secret") String clientSecret,
        @Query("code") String code, @Query("state") String state);

}
