package me.annenkov.translator.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.annenkov.translator.model.HistoryElement;

/**
 * Класс для работы с историей и избранным.
 * Вся история хранится в базе данных с использованием
 * DAO класса HistoryElement и библиотеки Sugar ORM.
 */
public class HistoryManager {
    /**
     * Текущий элемент в истории - элемент, на котором в данный
     * момент сфокусировано внимание. Эдакий "курсор". Причём,
     * он не обязательно должен находится в истории.
     * <p>
     * Используется, когда элемент уже добавлен в историю, но
     * затем добавляется в избранное из главного экрана.
     * Если последний элемент в истории и текущий элемент
     * совпадают - последний элемент заменяется текущим.
     * Дублирования не происходит.
     */
    private static HistoryElement mCurrentHistoryElement;

    public HistoryManager() {
        mCurrentHistoryElement = new HistoryElement("", "", "", "");
    }

    public static void addHistoryElement(HistoryElement historyElement) {
        historyElement.save();
    }

    /**
     * Добавление в историю по таймеру.
     * Переводчик не имеет кнопки для перевода, поэтому
     * перевод осуществляется через определённое время после
     * набора текста.
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

    public static HistoryElement getCurrentHistoryElement() {
        return mCurrentHistoryElement;
    }

    public static void setCurrentHistoryElement(HistoryElement currentHistoryElement) {
        mCurrentHistoryElement = currentHistoryElement;
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
