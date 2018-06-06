package com.example.onepiece.mainPage;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.onepiece.R;
import com.example.onepiece.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class DiscoverFragment extends Fragment {
//    List<Integer> background_colors;
    List<Integer> background;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        background = new ArrayList<>();
        background.add(R.drawable.p1);
        background.add(R.drawable.p2);
        background.add(R.drawable.p3);
        background.add(R.drawable.p4);
        background.add(R.drawable.p5);
        background.add(R.drawable.p6);
        background.add(R.drawable.p7);
        background.add(R.drawable.p8);

        View view = inflater.inflate(R.layout.discover_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.discover_recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        MasonryAdapter adapter = new MasonryAdapter(background, getContext());
        recyclerView.setAdapter(adapter);
        SpacesItemDecoration decoration = new SpacesItemDecoration(2, 10, true);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemClickListener(new MasonryAdapter.OnItemClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder VH, int position) {
                Toast.makeText(getActivity(), "you click" + position, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public static class MasonryView extends RecyclerView.ViewHolder {
        ImageView mImageView;

        public MasonryView(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.masonry_image_view);
        }
    }

    public static class MasonryAdapter extends RecyclerView.Adapter<MasonryView> {
        private List<Integer> background;
        private Context mContext;

        public MasonryAdapter(List<Integer> background_colors, Context context) {
            this.background = background_colors;
            this.mContext = context;
        }

        @Override
        public MasonryView onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.masonry_item, parent, false);
            return new MasonryView(view);
        }

        @Override
        public void onBindViewHolder(final MasonryView holder, final int position) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(mContext.getResources(), background.get(position), options);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.mImageView.getLayoutParams();
            float itemWidth = (ScreenUtils.getScreenWidth(mContext) - 10 * 3) / 2;
            params.width = (int) itemWidth;
            float scale = (itemWidth + 0f) / options.outWidth;
            params.height = (int) (options.outHeight * scale);
            holder.mImageView.setLayoutParams(params);

            holder.mImageView.setBackgroundResource(background.get(position));
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onClick(holder, position);
                    }
                }
            });

            holder.mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemLongClickListener != null) {
                        mOnItemLongClickListener.onLongClick(holder, position);
                        return true;
                    }
                    return false;
                }
            });
        }


        @Override
        public int getItemCount() {
            return background.size();
        }

        private OnItemClickListener mOnItemClickListener;
        private OnItemLongClickListener mOnItemLongClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
            mOnItemLongClickListener = onItemLongClickListener;
        }

        interface OnItemClickListener {
            void onClick(RecyclerView.ViewHolder VH, int position);
        }

        interface OnItemLongClickListener {
            void onLongClick(RecyclerView.ViewHolder VH, int position);
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
}
