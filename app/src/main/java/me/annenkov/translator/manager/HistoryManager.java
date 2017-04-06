package me.annenkov.translator.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import me.annenkov.translator.model.HistoryElement;

public class HistoryManager {
    private Timer mTimer;
    private List<HistoryElement> mHistoryElements;
    private HistoryElement mCurrentHistoryElement;

    public HistoryManager() {
        mHistoryElements = new ArrayList<>();
        mCurrentHistoryElement = new HistoryElement("", "", "", "");
    }

    public Timer getTimer() {
        return mTimer;
    }

    public void addHistoryElement(HistoryElement historyElement) {
        mHistoryElements.add(0, historyElement);
    }

    public void addHistoryElementWithTimer(HistoryElement historyElement, long delay) {
        mTimer = new Timer(true);
        mTimer.schedule(new TimerTask(historyElement), delay);
    }

    public List<HistoryElement> getHistoryElements() {
        return mHistoryElements;
    }

    public void setHistoryElements(List<HistoryElement> historyElements) {
        mHistoryElements = historyElements;
    }

    public HistoryElement getFirstHistoryElement() {
        return mHistoryElements.get(0);
    }

    public HistoryElement getHistoryElement(int index) {
        return mHistoryElements.get(index);
    }

    public int getHistoryElementsSize() {
        return mHistoryElements.size();
    }

    public void cancelTimer() {
        mTimer.cancel();
    }

    public HistoryElement getCurrentHistoryElement() {
        return mCurrentHistoryElement;
    }

    public void setCurrentHistoryElement(HistoryElement currentHistoryElement) {
        mCurrentHistoryElement = currentHistoryElement;
    }

    public boolean isElementInHistory(HistoryElement historyElement) {
        return getElementInHistory(historyElement) != null;
    }

    public HistoryElement getElementInHistory(HistoryElement historyElement) {
        for (HistoryElement historyElementInList : mHistoryElements) {
            if (historyElementInList.equals(historyElement)) {
                return historyElementInList;
            }
        }
        return null;
    }

    public Integer getElementInHistoryIndex(HistoryElement historyElement) {
        for (int i = 0; i < mHistoryElements.size(); i++) {
            if (mHistoryElements.get(i).equals(historyElement)) {
                return i;
            }
        }
        return -1;
    }

    public void updateHistory(List<HistoryElement> historyElements) {
        for (int i = 0; i < mHistoryElements.size(); i++) {
            for (int j = 0; j < historyElements.size(); j++) {
                if (mHistoryElements.get(i).equals(historyElements.get(j))) {
                    mHistoryElements.set(i, historyElements.get(j));
                }
            }
        }
    }

    private class TimerTask extends java.util.TimerTask {
        private HistoryElement mHistoryElement;

        TimerTask(HistoryElement historyElement) {
            mHistoryElement = historyElement;
        }

        @Override
        public void run() {
            addHistoryElement(mHistoryElement);
        }
    }
}
