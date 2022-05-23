package ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.FileDownloadMode.FOLDER;

/**
 * Базовый класс UI-тестов для предварительной настройки
 */
public class TestRunner {

    static{
        Configuration.fileDownload = FOLDER;
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;
    }

    @BeforeEach
    public void addAllureListenerToSelenideLogger(){
        if (!SelenideLogger.hasListener("AllureSelenide")){
            SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                            .savePageSource(false)
                            .screenshots(true));
        }
    }
}
