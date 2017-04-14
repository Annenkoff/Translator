package me.annenkov.translator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import me.annenkov.translator.R;

public class SelectLanguageActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private FirstLanguageAdapter mAdapter;

    private List<String> mLanguages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_select_language);
        initToolbar();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_select_language);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.keyboard_backspace));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateUI() {
        mLanguages = getIntent().getStringArrayListExtra("LANGUAGES");
        if (mAdapter == null) {
            mAdapter = new FirstLanguageAdapter();
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class FirstLanguageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Button mNameOfLanguage;

        FirstLanguageHolder(View itemView) {
            super(itemView);
            this.mNameOfLanguage = (Button) itemView.findViewById(R.id.text_of_one_language_element);
        }

        void setOnClickListener() {
            mNameOfLanguage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("LANGUAGE", mNameOfLanguage.getText());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private class FirstLanguageAdapter extends RecyclerView.Adapter<FirstLanguageHolder> {
        @Override
        public FirstLanguageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_language, parent, false);
            return new FirstLanguageHolder(view);
        }

        @Override
        public void onBindViewHolder(FirstLanguageHolder holder, int position) {
            holder.mNameOfLanguage.setText(mLanguages.get(position));
            holder.setOnClickListener();
        }

        @Override
        public int getItemCount() {
            return mLanguages.size();
        }
    }
}
