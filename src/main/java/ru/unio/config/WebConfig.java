package ru.unio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурационный класс для настройки веб-слоя приложения.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Настройка обработки статических ресурсов</li>
 *   <li>Конфигурация доступа к загруженным файлам</li>
 * </ul>
 *
 * <h3>Жизненный цикл работы:</h3>
 * <ol>
 *   <li>При старте приложения Spring создает экземпляр этого класса</li>
 *   <li>Метод {@code addResourceHandlers} регистрирует обработчики ресурсов</li>
 *   <li>При обращении к URL с префиксом {@code /uploads/} сервер ищет файлы в указанной директории</li>
 * </ol>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Директория для хранения загруженных файлов.
     * <p>
     * Формируется как поддиректория {@code uploads} в рабочей директории приложения.
     * Рабочая директория определяется через {@code System.getProperty("user.dir")}.
     * </p>
     *
     * <h4>Пример пути:</h4>
     * <code>/home/user/app_root/uploads/</code>
     */
    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    /**
     * Регистрирует обработчики для статических ресурсов.
     *
     * @param registry реестр для управления обработчиками ресурсов
     *
     * <h4>Особенности конфигурации:</h4>
     * <ul>
     *   <li>Все запросы по пути {@code /uploads/**} перенаправляются в локальную файловую систему</li>
     *   <li>Физическое расположение файлов: {@code [рабочая_директория]/uploads/}</li>
     *   <li>Префикс {@code file:} указывает, что ресурсы находятся в файловой системе</li>
     * </ul>
     *
     * <h4>Пример работы:</h4>
     * <p>
     * Запрос {@code http://example.com/uploads/image.jpg} будет преобразован в
     * {@code file:[user.dir]/uploads/image.jpg}
     * </p>
     *
     * @see WebMvcConfigurer#addResourceHandlers(ResourceHandlerRegistry)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}