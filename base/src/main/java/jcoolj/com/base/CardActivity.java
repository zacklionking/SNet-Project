package jcoolj.com.base;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import jcoolj.com.base.view.card.FlipCard;
import jcoolj.com.core.support.IActivity;

public abstract class CardActivity extends IActivity {

    protected FlipCard cardContainer;

    protected boolean isOverridePendingTransition = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
        isOverridePendingTransition = getIntent().getBooleanExtra("isOverridePendingTransition", false);
        if(isOverridePendingTransition)
            overridePendingTransition(R.anim.anim_scale_enter, R.anim.anim_scale_enter);
        super.setContentView(R.layout.activity_card);

        cardContainer = (FlipCard) findViewById(R.id.card_container);
    }

    @Override
    public void onBackPressed() {
        if(isBackSide())
            flip();
        else
            super.onBackPressed();
    }

    @Override
    public void setContentView(int layoutResID) { }

    public void setFlipListener(FlipCard.FlipListener listener){
        cardContainer.setFlipListener(listener);
    }

    public void setCards(View frontCard, View backCard){
        cardContainer.setFrontView(frontCard);
        cardContainer.setBackView(backCard);
    }

    public void setFrontCard(View view){
        cardContainer.setFrontView(view);
    }

    public void setBackCard(View view){
        cardContainer.setBackView(view);
    }

    public void flip(){
        cardContainer.flip();
    }

    public boolean isBackSide(){
        return cardContainer.isBackside();
    }

    @Override
    public void finish() {
        super.finish();
        if(isOverridePendingTransition)
            overridePendingTransition(R.anim.anim_scale_exit, R.anim.anim_scale_exit);
    }

}
