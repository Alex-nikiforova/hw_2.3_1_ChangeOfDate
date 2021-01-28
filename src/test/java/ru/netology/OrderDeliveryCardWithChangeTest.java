package ru.netology;

import com.codeborne.selenide.Condition;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class OrderDeliveryCardWithChangeTest {

    Faker faker = new Faker(new Locale("ru"));
    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final DataGeneratorForCardOrder cardOrder = new DataGeneratorForCardOrder(faker.name().fullName(), faker.address().city(), faker.phoneNumber().phoneNumber(), LocalDate.now().plusDays(4).format(df), LocalDate.now().plusDays(7).format(df));

    @BeforeEach
    void setUpAll() {
        open("http://localhost:9999");
    }

    @Test
    void shouldReturnSuccessfullyIfChangedDate() {

        $("[placeholder='Город']").setValue(cardOrder.getCity());
        $("[placeholder='Дата встречи']").sendKeys(Keys.chord(Keys.SHIFT,Keys.HOME), Keys.BACK_SPACE);
        $("[placeholder='Дата встречи']").setValue(cardOrder.getMeetingDate1());
        $("[name='name']").setValue(cardOrder.getFullName());
        $("[name='phone']").setValue(cardOrder.getMobilePhone());
        $(".checkbox__box").click();
        $(withText("Запланировать")).click();
        $("[data-test-id='success-notification'] > .notification__title")
                .shouldHave(Condition.exactText("Успешно!"))
                .shouldBe(Condition.visible);
        $("[data-test-id='success-notification'] > .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + cardOrder.getMeetingDate1()))
                .shouldBe(Condition.visible);
        $("[placeholder='Дата встречи']").sendKeys(Keys.chord(Keys.SHIFT,Keys.HOME), Keys.BACK_SPACE);
        $("[placeholder='Дата встречи']").setValue(cardOrder.getMeetingDate2());
        $(withText("Запланировать")).click();
        $("[data-test-id='replan-notification'] > .notification__title")
                .shouldHave(Condition.exactText("Необходимо подтверждение"))
                .shouldBe(Condition.visible);
        $("[data-test-id='replan-notification'] > .notification__content")
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"))
                .shouldBe(Condition.visible);
        $(withText("Перепланировать")).click();
        $("[data-test-id='success-notification'] > .notification__title")
                .shouldHave(Condition.exactText("Успешно!"))
                .shouldBe(Condition.visible);
        $("[data-test-id='success-notification'] > .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + cardOrder.getMeetingDate2()))
                .shouldBe(Condition.visible);
    }
}
