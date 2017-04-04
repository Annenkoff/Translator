package me.annenkov.translator;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryElement implements Parcelable {
    public static final Creator<HistoryElement> CREATOR = new Creator<HistoryElement>() {
        @Override
        public HistoryElement createFromParcel(Parcel in) {
            return new HistoryElement(in);
        }

        @Override
        public HistoryElement[] newArray(int size) {
            return new HistoryElement[size];
        }
    };
    private String firstLanguageReduction;
    private String secondLanguageReduction;
    private String firstText;
    private String secondText;
    private boolean isFavorite;

    public HistoryElement(String firstLanguageReduction, String secondLanguageReduction, String firstText, String secondText) {
        this.firstLanguageReduction = firstLanguageReduction;
        this.secondLanguageReduction = secondLanguageReduction;
        this.firstText = firstText;
        this.secondText = secondText;
        this.isFavorite = false;
    }

    public HistoryElement(String firstLanguageReduction, String secondLanguageReduction, String firstText, String secondText, boolean isFavorite) {
        this.firstLanguageReduction = firstLanguageReduction;
        this.secondLanguageReduction = secondLanguageReduction;
        this.firstText = firstText;
        this.secondText = secondText;
        this.isFavorite = isFavorite;
    }

    protected HistoryElement(Parcel in) {
        firstLanguageReduction = in.readString();
        secondLanguageReduction = in.readString();
        firstText = in.readString();
        secondText = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public String getFirstLanguageReduction() {
        return firstLanguageReduction;
    }

    public String getSecondLanguageReduction() {
        return secondLanguageReduction;
    }

    public String getFirstText() {
        return firstText;
    }

    public String getSecondText() {
        return secondText;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstLanguageReduction);
        dest.writeString(secondLanguageReduction);
        dest.writeString(firstText);
        dest.writeString(secondText);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}
