package me.annenkov.translator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class SelectSecondLanguageActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SelectSecondLanguageActivity.SecondLanguageAdapter mAdapter;

    private List<String> mLanguages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();
    }

    private void updateUI() {
        mLanguages = getIntent().getStringArrayListExtra("LANGUAGES");
        if (mAdapter == null) {
            mAdapter = new SecondLanguageAdapter();
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class SecondLanguageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Button mNameOfLanguage;

        public SecondLanguageHolder(View itemView) {
            super(itemView);
            this.mNameOfLanguage = (Button) itemView.findViewById(R.id.text_of_one_element);
        }

        @Override
        public void onClick(View view) {

        }
    }

    private class SecondLanguageAdapter extends RecyclerView.Adapter<SelectSecondLanguageActivity.SecondLanguageHolder> {
        @Override
        public SelectSecondLanguageActivity.SecondLanguageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_language, parent, false);
            view.setOnClickListener(new MyOnClickListener());
            return new SelectSecondLanguageActivity.SecondLanguageHolder(view);
        }

        @Override
        public void onBindViewHolder(SelectSecondLanguageActivity.SecondLanguageHolder holder, int position) {
            holder.mNameOfLanguage.setText(mLanguages.get(position));
        }

        @Override
        public int getItemCount() {
            return mLanguages.size();
        }
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("LANGUAGE", mLanguages.get(mRecyclerView.indexOfChild(v)));
            setResult(2, intent);
            finish();
        }
    }
}
