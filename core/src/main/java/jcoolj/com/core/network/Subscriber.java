package jcoolj.com.core.network;

public interface Subscriber<T> {

    void onSubscribe();

    void onRefuse(Throwable e);

    void onComplete(T data);

}
