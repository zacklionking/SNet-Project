package jcoolj.com.base.view.scrollable;

public interface ScrollBehavior {

    void onScrollChanged(ScrollState direction, int position);

    void onReachEnd();

    void onScrollDirectionChanged(ScrollState direction);

}
