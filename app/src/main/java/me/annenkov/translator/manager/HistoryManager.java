package me.annenkov.translator.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.annenkov.translator.activity.MainActivity;
import me.annenkov.translator.model.HistoryElement;

/**
 * Класс для работы с историей и избранным.
 * Вся история хранится в базе данных с использованием
 * DAO класса HistoryElement и библиотеки Sugar ORM.
 */
public class HistoryManager {
    public static void addHistoryElement(HistoryElement historyElement) {
        historyElement.save();
    }

    /**
     * Добавление в историю по таймеру.
     * Переводчик не имеет кнопки для перевода, поэтому
     * перевод осуществляется через определённое время после
     * набора текста. Сделано для оптимизации трафика.
     *
     * @param historyElement Элемент, который добавляется в историю.
     */
    public static void addHistoryElementWithTimer(HistoryElement historyElement) {
        TimerManager.startAddHistoryElementTimer(historyElement);
    }

    public static List<HistoryElement> getHistoryElements() {
        try {
            List<HistoryElement> historyElements = HistoryElement.listAll(HistoryElement.class);
            Collections.reverse(historyElements);
            return historyElements;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public static void setHistoryElements(List<HistoryElement> historyElements) {
        Collections.reverse(historyElements);
        HistoryElement.deleteAll(HistoryElement.class);
        HistoryElement.saveInTx(historyElements);
    }

    public static HistoryElement getFirstHistoryElement() {
        if (HistoryElement.last(HistoryElement.class) != null) {
            return HistoryElement.last(HistoryElement.class);
        } else {
            return new HistoryElement("", "", "", "");
        }
    }

    /**
     * Генерация "текущего элемента истории".
     * Текущий элемент истории - элемент, который содержит информацию из MainActivity
     * и не обязательно хранится в истории в данный момент. Сделано для того, чтобы
     * не происходило дублирования при добавлении в избранное на главном экране.
     *
     * @param activity Активити, из которого берётся информация о текстах.
     */
    public static HistoryElement getCurrentHistoryElement(MainActivity activity) {
        return new HistoryElement(LanguagesManager.getFirstLanguageReduction().toUpperCase(),
                LanguagesManager.getSecondLanguageReduction().toUpperCase(),
                activity.getInputText().getText().toString(),
                activity.getTranslatedText().getText().toString());
    }

    /**
     * Получение индекса переданного объекта в истории.
     *
     * @param historyElement Элемент, индекс которого требуется получить.
     */
    public static Integer getElementInHistoryIndex(HistoryElement historyElement) {
        List<HistoryElement> historyElements = getHistoryElements();
        for (int i = 0; i < historyElements.size(); i++) {
            if (historyElements.get(i).equals(historyElement)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Обновление отдельных элементов в истории.
     * Используется, например, когда имеется список только избранных элементов.
     * В итоге формируется новый список истории и записывается в базу.
     *
     * @param newElements Список обновлённых избранных элементов.
     */
    public static void updateHistory(List<HistoryElement> newElements) {
        List<HistoryElement> historyElements = HistoryElement.listAll(HistoryElement.class);
        for (int i = 0; i < historyElements.size(); i++) {
            for (int j = 0; j < newElements.size(); j++) {
                if (historyElements.get(i).equals(newElements.get(j))) {
                    historyElements.set(i, newElements.get(j));
                }
            }
        }
        setHistoryElements(historyElements);
    }
}
