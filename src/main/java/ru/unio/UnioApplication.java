package ru.unio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Главный класс приложения, точка входа в Spring Boot приложение.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Запускает Spring Boot приложение</li>
 *   <li>Настраивает автоматическую конфигурацию Spring Boot</li>
 *   <li>Активирует сканирование JPA репозиториев</li>
 * </ul>
 *
 * <h3>Аннотации:</h3>
 * <ul>
 *   <li>{@code @SpringBootApplication} - объединяет три аннотации:
 *     <ul>
 *       <li>{@code @Configuration} - помечает класс как источник определений бинов</li>
 *       <li>{@code @EnableAutoConfiguration} - включает автоматическую конфигурацию Spring Boot</li>
 *       <li>{@code @ComponentScan} - включает сканирование компонентов в текущем пакете и подпакетах</li>
 *     </ul>
 *   </li>
 *   <li>{@code @EnableJpaRepositories} - активирует JPA репозитории в указанном пакете</li>
 * </ul>
 *
 * <h3>Жизненный цикл приложения:</h3>
 * <ol>
 *   <li>Вызывается метод {@code main()}</li>
 *   <li>Spring Boot инициализирует контекст приложения</li>
 *   <li>Автоконфигурация настраивает все необходимые компоненты</li>
 *   <li>Приложение готово к обработке запросов</li>
 * </ol>
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.unio.repository")
public class UnioApplication {

	/**
	 * Точка входа в приложение.
	 *
	 * <h4>Особенности работы:</h4>
	 * <ul>
	 *   <li>Создает и настраивает {@link org.springframework.context.ApplicationContext}</li>
	 *   <li>Запускает встроенный сервер (если есть зависимость web)</li>
	 *   <li>Обрабатывает аргументы командной строки</li>
	 * </ul>
	 *
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(UnioApplication.class, args);
	}
}