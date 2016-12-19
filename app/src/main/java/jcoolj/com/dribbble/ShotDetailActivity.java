package jcoolj.com.dribbble;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import jcoolj.com.base.CardActivity;
import jcoolj.com.base.view.scrollable.ExtendAdapter;
import jcoolj.com.core.network.Subscriber;
import jcoolj.com.dribbble.adapter.ShotCardAdapter;
import jcoolj.com.dribbble.bean.User;
import jcoolj.com.dribbble.data.UserManager;
import jcoolj.com.dribbble.bean.Comment;
import jcoolj.com.dribbble.utils.Navigator;
import jcoolj.com.dribbble.bean.Shot;
import jcoolj.com.dribbble.view.ShotCard;
import jcoolj.com.dribbble.view.UserCard;

public class ShotDetailActivity extends CardActivity implements Subscriber<User> {

    private ShotCard shotCard;
    private UserCard userCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initShotCard();

        Intent intent = getIntent();
        boolean needRefresh = Navigator.isNeedRefresh(intent);
        Shot shot = Navigator.getShot(intent);

        if(shot != null){
            shotCard.setShot(shot);
            initUserCard(shot.getUser(), needRefresh);
            setCards(shotCard, userCard);
        } else {
            User user = Navigator.getUser(intent);
            if(user != null) {
                initUserCard(user, needRefresh);
                setCards(userCard, shotCard);
            } //else
                //throw new IllegalArgumentException("No data found.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        shotCard.startShotDisplay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shotCard.stopShotDisplay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shotCard.cancelShotLoading();
    }

    private void initShotCard(){
        shotCard = new ShotCard(this);
        shotCard.setOnCommentClickListener(new ShotCardAdapter.OnCommentClickListener() {
            @Override
            public void onAvatarClick(User user) {
                if(!user.equals(shotCard.getShot().getUser()))
                    Navigator.navigateToUserCard(ShotDetailActivity.this, user);
            }

            @Override
            public void onCommentClick(Comment comment) {
//                Toast.makeText(getContext(), comment.getBody(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUserCard(User user, boolean needRefresh){
        userCard = new UserCard(this);
        userCard.setUser(user);
        userCard.setOnItemClickListener(new ExtendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Shot shot = userCard.getShot(position);
                if (shot == null)
                    return;
                shotCard.setShot(shot);
                flip();
            }
        });
        shotCard.setOnUserClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCard.refresh();
                flip();
            }
        });
        if(needRefresh) {
            UserManager userManager = new UserManager();
            userManager.getUserInfo(this, user.getId());
        }
    }

    @Override
    public void onSubscribe() {

    }

    @Override
    public void onComplete(User user) {
        userCard.updateUser(user);
    }

    @Override
    public void onRefuse(Throwable e) {

    }

}
