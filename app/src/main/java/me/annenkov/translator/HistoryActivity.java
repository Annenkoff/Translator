package me.annenkov.translator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private HistoryActivity.HistoryAdapter mAdapter;

    private List<HistoryElement> mHistoryElements;

    private boolean isOnlyFavorites;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_history);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        isOnlyFavorites = getIntent().getBooleanExtra("IS_ONLY_FAVORITES", false);

        updateUI();
    }

    private void updateUI() {
        mHistoryElements = getElements(isOnlyFavorites);
        if (mAdapter == null) {
            mAdapter = new HistoryActivity.HistoryAdapter();
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

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
            if (historyElement.isFavorite()) {
                addToFavoritesButton.setImageResource(R.drawable.ic_bookmark_black_24dp);
            } else {
                addToFavoritesButton.setImageResource(R.drawable.ic_bookmark_outline_black_24dp);
            }
        }

        void setOnClickListener() {
            addToFavoritesButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mHistoryElement.isFavorite()) {
                addToFavoritesButton.setImageResource(R.drawable.ic_bookmark_outline_black_24dp);
            } else {
                addToFavoritesButton.setImageResource(R.drawable.ic_bookmark_black_24dp);
            }
            mHistoryElement.setFavorite(!mHistoryElement.isFavorite());
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("NEW_HISTORY", (ArrayList<? extends Parcelable>) mHistoryElements);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryActivity.HistoryHolder> {
        @Override
        public HistoryActivity.HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_history_element, parent, false);
            return new HistoryActivity.HistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(HistoryActivity.HistoryHolder holder, int position) {
            holder.bindHolder(mHistoryElements.get(position));
            holder.setOnClickListener();
        }

        @Override
        public int getItemCount() {
            return mHistoryElements.size();
        }
    }
}