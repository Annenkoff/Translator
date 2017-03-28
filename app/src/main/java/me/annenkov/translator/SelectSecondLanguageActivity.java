package me.annenkov.translator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SelectSecondLanguageActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SelectSecondLanguageActivity.SecondLanguageAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // если мы уверены, что изменения в контенте не изменят размер layout-а RecyclerView
        // передаем параметр true - это увеличивает производительность
        mRecyclerView.setHasFixedSize(true);

        // используем linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // создаем адаптер

        updateUI();
    }

    private void updateUI() {
        String[] languages = {"LOL", "KEK", "Cheburek"};
        if (mAdapter == null) {
            mAdapter = new SelectSecondLanguageActivity.SecondLanguageAdapter(languages);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class SecondLanguageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameOfLanguage;

        public SecondLanguageHolder(View itemView) {
            super(itemView);
            this.nameOfLanguage = (TextView) itemView.findViewById(R.id.tv_recycler_language);
        }

        @Override
        public void onClick(View view) {

        }
    }

    private class SecondLanguageAdapter extends RecyclerView.Adapter<SelectSecondLanguageActivity.SecondLanguageHolder> {
        private String[] mLanguages;

        SecondLanguageAdapter(String[] mLanguages) {
            this.mLanguages = mLanguages;
        }

        @Override
        public SelectSecondLanguageActivity.SecondLanguageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_language, parent, false);
            return new SelectSecondLanguageActivity.SecondLanguageHolder(view);
        }

        @Override
        public void onBindViewHolder(SelectSecondLanguageActivity.SecondLanguageHolder holder, int position) {
            holder.nameOfLanguage.setText(mLanguages[position]);
        }

        @Override
        public int getItemCount() {
            return mLanguages.length;
        }
    }
}
