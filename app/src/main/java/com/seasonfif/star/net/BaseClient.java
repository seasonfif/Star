package com.seasonfif.star.net;

import android.support.v4.util.Pair;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by lxy on 2018/1/28.
 */

public abstract class BaseClient<T> {

    public final static int PER_PAGE = 10;

    protected abstract ApiSubscriber getApiObservable(Retrofit retrofit);

    public Observable<Pair<T, Integer>> observable(){
        return getApiObservable();
    }

    private Observable getApiObservable() {
        return Observable.create(getApiObservable(getEngine()));
    }

    private Retrofit getEngine() {
        return RetrofitEngine.getRetrofit();
    }

    protected abstract class ApiSubscriber implements Observable.OnSubscribe<Pair<T, Integer>>, Callback<T> {
        private Subscriber<? super Pair<T, Integer>> subscriber;

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.isSuccessful()){
                subscriber.onNext(new Pair<T, Integer>(response.body(), getLinkData(response)));
                subscriber.onCompleted();
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            subscriber.onError(t);
        }

        @Override
        public void call(Subscriber<? super Pair<T, Integer>> subscriber) {
            this.subscriber = subscriber;
            call(getEngine());
        }

        protected abstract void call(Retrofit retrofit);

        private Integer getLinkData(Response r) {
            if (r != null) {
                String link = r.headers().get("Link");
                if (link != null) {
                    String[] parts = link.split(",");
                    try {
                        PaginationLink paginationLink = new PaginationLink(parts[0]);
                        return paginationLink.rel == RelType.next ? paginationLink.page : null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
