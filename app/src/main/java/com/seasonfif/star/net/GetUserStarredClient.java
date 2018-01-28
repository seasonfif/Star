package com.seasonfif.star.net;

import com.seasonfif.star.model.Repository;

import java.util.List;

import retrofit2.Retrofit;

/**
 * Created by lxy on 2018/1/28.
 */
public class GetUserStarredClient extends BaseClient<List<Repository>> {

    private String name;
    private String sort;
    private int page;

    public GetUserStarredClient(String name, String sort, int page) {
        this.name = name;
        this.sort = sort;
        this.page = page;
    }

    @Override
    protected ApiSubscriber getApiObservable(Retrofit retrofit) {
        return new ApiSubscriber() {
            @Override
            protected void call(Retrofit retrofit) {
                StarService service = retrofit.create(StarService.class);
                if (page == 0){
                    service.userStarredReposList(name, sort);
                }else{
                    service.userStarredReposList(name, sort, PER_PAGE, page);
                }
            }
        };
    }
}
