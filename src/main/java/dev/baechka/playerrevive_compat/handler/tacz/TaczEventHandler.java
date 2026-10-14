package dev.baechka.playerrevive_compat.handler.tacz;

/**
 * Обработчик событий для мода TACZ (Timeless and Classics Zero)
 * Блокирует стрельбу если игрок в состоянии bleeding
 *
 * Этот класс НЕ содержит никаких ссылок на классы TACZ напрямую.
 * Вся логика находится во внутреннем классе TaczEventHandlerImpl,
 * который загружается только при вызове register().
 */
public class TaczEventHandler {

    /**
     * Регистрирует обработчики событий TACZ
     * Вызывается только если TACZ загружен
     */
    public static void register() {
        // Загружаем реализацию через отдельный класс
        TaczEventHandlerImpl.doRegister();
    }
}
