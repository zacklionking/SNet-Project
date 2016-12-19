package jcoolj.com.base.view.scrollable;

import android.support.v7.widget.GridLayoutManager;

public class HeaderSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private ExtendAdapter adapter;
    private int mSpanSize = 1;

    public HeaderSpanSizeLookup(ExtendAdapter adapter, int spanSize) {
        this.adapter = adapter;
        this.mSpanSize = spanSize;
    }

    @Override
    public int getSpanSize(int position) {
        boolean isHeaderOrFooter = adapter.isHeader(position);
        return isHeaderOrFooter ? mSpanSize : 1;
    }
}
