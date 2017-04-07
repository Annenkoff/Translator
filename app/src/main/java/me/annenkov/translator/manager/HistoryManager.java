package me.annenkov.translator.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import me.annenkov.translator.model.HistoryElement;

public class HistoryManager {
    private Timer mTimer;
    private HistoryElement mCurrentHistoryElement;

    public HistoryManager() {
        mCurrentHistoryElement = new HistoryElement("", "", "", "");
    }

    public Timer getTimer() {
        return mTimer;
    }

    public void addHistoryElement(HistoryElement historyElement) {
        historyElement.save();
    }

    public void addHistoryElementWithTimer(HistoryElement historyElement, long delay) {
        mTimer = new Timer(true);
        mTimer.schedule(new TimerTask(historyElement), delay);
    }

    public List<HistoryElement> getHistoryElements() {
        try {
            List<HistoryElement> historyElements = HistoryElement.listAll(HistoryElement.class);
            Collections.reverse(historyElements);
            return historyElements;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public void setHistoryElements(List<HistoryElement> historyElements) {
        Collections.reverse(historyElements);
        HistoryElement.deleteAll(HistoryElement.class);
        HistoryElement.saveInTx(historyElements);
    }

    public HistoryElement getFirstHistoryElement() {
        return HistoryElement.last(HistoryElement.class);
    }

    public int getHistoryElementsSize() {
        return HistoryElement.listAll(HistoryElement.class).size();
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
        List<HistoryElement> historyElements = HistoryElement.listAll(HistoryElement.class);
        for (HistoryElement historyElementInList : historyElements) {
            if (historyElementInList.equals(historyElement)) {
                return historyElementInList;
            }
        }
        return null;
    }

    public Integer getElementInHistoryIndex(HistoryElement historyElement) {
        List<HistoryElement> historyElements = getHistoryElements();
        for (int i = 0; i < historyElements.size(); i++) {
            if (historyElements.get(i).equals(historyElement)) {
                return i;
            }
        }
        return -1;
    }

    public void updateHistory(List<HistoryElement> newHistoryElements) {
        List<HistoryElement> historyElements = HistoryElement.listAll(HistoryElement.class);
        for (int i = 0; i < historyElements.size(); i++) {
            for (int j = 0; j < newHistoryElements.size(); j++) {
                if (historyElements.get(i).equals(newHistoryElements.get(j))) {
                    historyElements.set(i, newHistoryElements.get(j));
                }
            }
        }
        setHistoryElements(historyElements);
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
