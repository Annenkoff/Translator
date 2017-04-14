package me.annenkov.translator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.annenkov.translator.R;
import me.annenkov.translator.model.HistoryElement;

public class HistoryActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private HistoryActivity.HistoryAdapter mAdapter;
    private TextView mEmptyHistoryBackgroundNotificationTextView;
    private LinearLayout mEmptyHistoryBackgroundNotification;

    private List<HistoryElement> mHistoryElements;

    private boolean isOnlyFavorites;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_history);
        initToolbar();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_history);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mEmptyHistoryBackgroundNotificationTextView = (TextView) findViewById(R.id.empty_history_backgroung_notification_textview);
        mEmptyHistoryBackgroundNotification = (LinearLayout) findViewById(R.id.empty_history_backgroung_notification);

        isOnlyFavorites = getIntent().getBooleanExtra("IS_ONLY_FAVORITES", false);

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
        mHistoryElements = getElements(isOnlyFavorites);
        if (mHistoryElements.size() == 0 && !isOnlyFavorites) {
            mEmptyHistoryBackgroundNotificationTextView.setText(getResources().getString(R.string.empty_history_message));
            mEmptyHistoryBackgroundNotification.setVisibility(View.VISIBLE);
        } else if (mHistoryElements.size() == 0 && isOnlyFavorites) {
            mEmptyHistoryBackgroundNotificationTextView.setText(getResources().getString(R.string.empty_favorites_message));
            mEmptyHistoryBackgroundNotification.setVisibility(View.VISIBLE);
        }
        if (mAdapter == null) {
            mAdapter = new HistoryActivity.HistoryAdapter();
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private List<HistoryElement> getElements(boolean isOnlyFavorites) {
        List<HistoryElement> historyElements = (List<HistoryElement>) getIntent().getSerializableExtra("HISTORY");
        List<HistoryElement> elements;
        if (isOnlyFavorites) {
            elements = new ArrayList<>();
            for (HistoryElement historyElement : historyElements) {
                if (historyElement.isFavorite()) elements.add(historyElement);
            }
        } else {
            elements = historyElements;
        }
        return elements;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_history_menu_item:
                if (!isOnlyFavorites) {
                    mHistoryElements = new ArrayList<>();
                    Toast.makeText(this, R.string.history_cleared_toast, Toast.LENGTH_SHORT).show();
                } else {
                    for (HistoryElement historyElement : mHistoryElements) {
                        historyElement.setFavorite(false);
                    }
                    Toast.makeText(this, R.string.favorites_cleared_toast, Toast.LENGTH_SHORT).show();
                }
                applyChanges();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyChanges() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("NEW_HISTORY", (ArrayList<? extends Parcelable>) mHistoryElements);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
    }

    private void applyChanges(String text, String firstLanguageReduction, String secondLanguageReduction) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("NEW_HISTORY", (ArrayList<? extends Parcelable>) mHistoryElements);
        bundle.putString("TEXT_TO_TRANSLATE", text);
        bundle.putString("FIRST_LANGUAGE", firstLanguageReduction);
        bundle.putString("SECOND_LANGUAGE", secondLanguageReduction);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
    }

    private class HistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private HistoryElement mHistoryElement;
        private TextView firstText;
        private TextView secondText;
        private TextView twoLanguageReductions;
        private ImageButton addToFavoritesButton;

        HistoryHolder(View itemView) {
            super(itemView);
            this.firstText = (TextView) itemView.findViewById(R.id.firstTextHistory);
            this.secondText = (TextView) itemView.findViewById(R.id.secondTextHistory);
            this.twoLanguageReductions = (TextView) itemView.findViewById(R.id.twoLanguageReductions);
            this.addToFavoritesButton = (ImageButton) itemView.findViewById(R.id.addToFavoritesButtonHistory);
        }

        void bindHolder(HistoryElement historyElement) {
            mHistoryElement = historyElement;
            firstText.setText(historyElement.getFirstText());
            secondText.setText(historyElement.getSecondText());
            twoLanguageReductions.setText(String.format("%s-%s",
                    historyElement.getFirstLanguageReduction(),
                    historyElement.getSecondLanguageReduction()));
            updateFavoriteButton();
            addToFavoritesButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mHistoryElement.setFavorite(!mHistoryElement.isFavorite());
            updateFavoriteButton();
            applyChanges();
        }

        private void updateFavoriteButton() {
            if (mHistoryElement.isFavorite()) {
                addToFavoritesButton.setImageResource(R.drawable.bookmark_black);
            } else {
                addToFavoritesButton.setImageResource(R.drawable.bookmark_outline_black);
            }
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryActivity.HistoryHolder> {
        @Override
        public HistoryActivity.HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_history_element, parent, false);
            final HistoryHolder historyHolder = new HistoryActivity.HistoryHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String twoLanguagesReductions = historyHolder.twoLanguageReductions.getText().toString();
                    applyChanges(historyHolder.firstText.getText().toString(), twoLanguagesReductions.split("-")[0], twoLanguagesReductions.split("-")[1]);
                    finish();
                }
            });
            return historyHolder;
        }

        @Override
        public void onBindViewHolder(HistoryActivity.HistoryHolder holder, int position) {
            holder.bindHolder(mHistoryElements.get(position));
        }

        @Override
        public int getItemCount() {
            return mHistoryElements.size();
        }
    }
}
