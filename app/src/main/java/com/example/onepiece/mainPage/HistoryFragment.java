package com.example.onepiece.mainPage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onepiece.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2018/6/11 0011.
 */

public class HistoryFragment extends Fragment {
    public static String TAG = "HistoryFragment";
    private RecyclerView mRecyclerView;
    private RecordAdapter mRecordAdapter;
    private List<String> record;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.history_fragment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setAdapter();
        return view;
    }

    private class RecordHolder extends RecyclerView.ViewHolder {
        public ImageView deleteButton;
        public TextView record;

        public RecordHolder(View itemView) {
            super(itemView);
            deleteButton = itemView.findViewById(R.id.history_fragment_delete);
            record = itemView.findViewById(R.id.history_fragment_record);
        }
    }

    private class RecordAdapter extends RecyclerView.Adapter<RecordHolder> {
        List<String> record;

        public RecordAdapter(List<String> record) {
            this.record = record;
        }

        @NonNull
        @Override
        public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.history_fragment_recycler_view_item, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = v.findViewById(R.id.history_fragment_record);
                    SearchView searchView = getActivity().findViewById(R.id.activity_search_search_view);
                    searchView.setQuery(textView.getText(), true);
                }
            });
            return new RecordHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordHolder holder, int position) {
            holder.record.setText(record.get(position));
            holder.deleteButton.setTag(position + 1);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    int pointer = sharedPreferences.getInt("pointer", 0);
                    int index = (int)v.getTag();
                    for (int i = index; i < pointer; i++) {
                        editor.putString(String.valueOf(i), sharedPreferences.getString(String.valueOf(i + 1), null));
                    }
                    editor.putInt("pointer", pointer - 1).apply();
                    setAdapter();
                }
            });
        }

        @Override
        public int getItemCount() {
            return record.size();
        }
    }

    void updateHistory(String query) {
        record = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int pointer = sharedPreferences.getInt("pointer", 0);
        for (int i = pointer; i >= 1; i--) {
            editor.putString(String.valueOf(i), sharedPreferences.getString(String.valueOf(i - 1), null));
        }
        editor.putString(String.valueOf(1), query);
        pointer = Math.min(pointer + 1, 20);
        editor.putInt("pointer", pointer).apply();
        setAdapter();
    }

    void setAdapter() {
        record = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int pointer = sharedPreferences.getInt("pointer", 0);
        for (int i = 1; i <= pointer; i++) {
            record.add(sharedPreferences.getString(String.valueOf(i), null));
        }
        mRecordAdapter = new RecordAdapter(record);
        mRecyclerView.setAdapter(mRecordAdapter);
    }
}
