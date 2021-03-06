package com.example.onepiece.mainPage;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onepiece.R;
import com.example.onepiece.model.DiscoveryBean;
import com.example.onepiece.model.OneBean;
import com.example.onepiece.util.FileUtils;
import com.example.onepiece.util.HttpUtils;
import com.example.onepiece.util.ScreenUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class DiscoverFragment extends Fragment {
    RecyclerView mRecyclerView;
    List<DiscoveryBean> mDiscoveryItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.discover_recycler_view);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        SpacesItemDecoration decoration = new SpacesItemDecoration(2, 20, true);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        getDiscovery();
        return view;
    }

    private class MasonryViewHolder extends RecyclerView.ViewHolder{
        public MasonryViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class MasonryAdapter extends RecyclerView.Adapter<MasonryViewHolder>{
        private List<DiscoveryBean> discoveryItems;
        private Context mContext;
        @BindView(R.id.discovery_fragment_card_container)
        CardView mCardView;
        @BindView(R.id.discovery_fragment_card_front_background)
        LinearLayout mLinearLayoutFront;
        @BindView(R.id.discovery_fragment_card_back_background)
        LinearLayout mLinearLayoutBack;
        @BindView(R.id.discovery_fragment_title)
        TextView title;
        @BindView(R.id.discovery_fragment_artist)
        TextView artist;
        @BindView(R.id.discovery_fragment_comment)
        TextView comment;
        @BindView(R.id.discovery_fragment_author)
        TextView author;
        @BindView(R.id.discovery_fragment_date)
        TextView date;

        int[] backgroundColor = new int[]{
                R.color.green, R.color.yellow, R.color.pink, R.color.orange, R.color.fuchsia,
                R.color.skyblue, R.color.olive, R.color.chartreuse, R.color.slategray, R.color.cornflowerblue,
                R.color.aqua, R.color.red, R.color.lightsalmon, R.color.gold, R.color.chocolate,
                R.color.powderblue, R.color.brown, R.color.powderblue, R.color.lime, R.color.khaki
        };

        public MasonryAdapter(List<DiscoveryBean> discoveryItems, Context context) {
            this.discoveryItems = discoveryItems;
            this.mContext = context;
        }

        @NonNull
        @Override
        public MasonryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discovery_fragment_card, parent, false);
            ButterKnife.bind(this, view);
            mCardView.setTag(false);
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout oldView = v.findViewById(R.id.discovery_fragment_card_front_background);
                    LinearLayout newView = v.findViewById(R.id.discovery_fragment_card_back_background);
                    boolean isShown = (boolean)v.getTag();
                    if (!isShown) {
                        flipCard(oldView, newView);
                        v.setTag(true);
                    } else {
                        flipCard(newView, oldView);
                        v.setTag(false);
                    }
                }
            });
            return new MasonryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MasonryViewHolder holder, final int position) {
            Random random = new Random();
            DiscoveryBean discoveryBean = discoveryItems.get(position);

            int bc = backgroundColor[random.nextInt(20)];
            mLinearLayoutFront.setBackgroundResource(bc);
            mLinearLayoutFront.getBackground().setAlpha(80);
            mLinearLayoutFront.setMinimumWidth((ScreenUtils.getScreenWidth(mContext) - 50) / 2);
            mLinearLayoutBack.setBackgroundResource(bc);
            mLinearLayoutBack.getBackground().setAlpha(80);
            mLinearLayoutBack.setMinimumWidth((ScreenUtils.getScreenWidth(mContext) - 50) / 2);

            String d = discoveryBean.getCreateDatetime();
            int index = d.indexOf('T');
            title.setText(discoveryBean.getSong().getTitle());
            artist.setText(discoveryBean.getSong().getArtist());
            comment.setText(discoveryBean.getText());
            author.setText(discoveryBean.getAuthor().getUsername());
            date.setText(d.substring(0, index));
        }


        @Override
        public int getItemCount() {
            return discoveryItems.size();
        }

        private void flipCard(final View oldView, final View newView) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(oldView, "rotationX", 0, 90);
            final ObjectAnimator animator2 = ObjectAnimator.ofFloat(newView, "rotationX", -90, 0);
            animator2.setInterpolator(new OvershootInterpolator(2.0f));

            animator1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    oldView.setVisibility(View.GONE);
                    animator2.setDuration(100).start();
                    newView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            animator1.setDuration(100).start();
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public SpacesItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    private void getDiscovery() {
        if (!readCache()) {
            Retrofit retrofit = HttpUtils.getRetrofit();
            HttpUtils.MyApi api = retrofit.create(HttpUtils.MyApi.class);
            api.fetchDiscovery()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<DiscoveryBean>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(List<DiscoveryBean> discoveryBeans) {
                            mDiscoveryItems = discoveryBeans;
                            saveInCache(discoveryBeans);
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                            MasonryAdapter adapter = new MasonryAdapter(mDiscoveryItems, getContext());
                            mRecyclerView.setAdapter(adapter);
                        }
                    });
        }
    }

    private boolean saveInCache(List<DiscoveryBean> discoveryBeans) {
        String discoveryPath = FileUtils.getDiscoveryCacheDirectory(getActivity());
        FileUtils.createDir(discoveryPath);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String fileName = simpleDateFormat.format(date);
        return FileUtils.saveParcelableObject(discoveryBeans, discoveryPath + fileName);
    }

    private boolean readCache() {
        String discoveryCache = FileUtils.getDiscoveryCacheDirectory(getActivity());
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String fileName = simpleDateFormat.format(date);
        List<DiscoveryBean> discoveryBeans = (List<DiscoveryBean>)FileUtils.readParcelableObject(discoveryCache + fileName);
        if (discoveryBeans!= null) {
            mDiscoveryItems = discoveryBeans;
            MasonryAdapter adapter = new MasonryAdapter(mDiscoveryItems, getContext());
            mRecyclerView.setAdapter(adapter);
            return true;
        }
        return false;
    }
}

