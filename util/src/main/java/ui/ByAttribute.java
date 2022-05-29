package ui;

import org.openqa.selenium.By;

import static java.lang.String.format;

public class ByAttribute {

    /**
     * Создает селектор по типу элемента и частичному значению аттрибута class
     */
    public static By byClassContaining(String classContainedValue) {
        return byClassContaining("*", classContainedValue);
    }

    /**
     * Создает селектор по типу элемента и частичному значению аттрибута
     */
    public static By byClassContaining(String elementType, String classContainedValue) {
        return byAttributeContaining(elementType, "class", classContainedValue);
    }

    /**
     * Создает селектор по типу элемента и частичному значению аттрибута
     */
    public static By byAttributeContaining(String elementType, String attributeName, String attributeContainedValue) {
        return By.xpath(format(".//%s[contains(@%s, '%s')]", elementType, attributeName, attributeContainedValue));
    }
}
