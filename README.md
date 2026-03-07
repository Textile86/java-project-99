### Hexlet tests and linter status:
[![Actions Status](https://github.com/Textile86/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/Textile86/java-project-99/actions)
![Checkstyle](https://github.com/Textile86/java-project-99/actions/workflows/checkstyle.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Textile86_java-project-99&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Textile86_java-project-99)

https://java-project-99-0dzq.onrender.com

# Менеджер задач

Веб-приложение для управления задачами, построенное на Spring Boot.

## Демо

**Приложение задеплоено на Render:** https://java-project-99-0dzq.onrender.com
> ⚠️ Бесплатный тариф Render — первый запрос может занять до 60 секунд (cold start).
Для входа используйте:

| Поле | Значение |
|------|----------|
| email | `admin@example.com` |
| password | `qwerty` |

## Технологии

- Java 21
- Spring Boot 3.3.5
- Spring Security + JWT
- Spring Data JPA
- MapStruct
- PostgreSQL / H2
- Sentry
- Swagger UI

## Возможности

- Управление задачами (создание, редактирование, удаление)
- Статусы задач и метки
- Фильтрация задач по названию, исполнителю, статусу и метке
- Аутентификация через JWT
- Мониторинг ошибок через Sentry
- Интерактивная документация API через Swagger
        
## Запуск

### Требования

- Java 21
- Gradle

### Локальный запуск
```bash
# Клонировать репозиторий
git clone https://github.com/Textile86/java-project-99.git
cd java-project-99

# Запустить с профилем development
./gradlew bootRun --args='--spring.profiles.active=development'
```

Приложение будет доступно по адресу `http://localhost:8080`

### Переменные окружения

| Переменная | Описание |
|------------|----------|
| `SENTRY_DSN` | DSN для отправки ошибок в Sentry |
| `SENTRY_AUTH_TOKEN` | Токен авторизации Sentry (для сборки) |
| `DATABASE_URL` | URL базы данных (для продакшена) |

## API

Документация доступна после запуска по адресу:
`http://localhost:8080/swagger-ui/index.html`

### Основные эндпоинты

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/login` | Получить JWT токен |
| GET/POST | `/api/users` | Пользователи |
| GET/POST | `/api/tasks` | Задачи |
| GET/POST | `/api/task_statuses` | Статусы задач |
| GET/POST | `/api/labels` | Метки |

### Фильтрация задач
```
GET /api/tasks?titleCont=баг&status=draft&assigneeId=1&labelId=2
```

## Тесты
```bash
./gradlew test
```

## Дефолтные данные

При первом запуске автоматически создаются:

- Пользователь: `admin@example.com` / `qwerty`
- Статусы: `draft`, `to_review`, `to_be_fixed`, `to_publish`, `published`
- Метки: `bug`, `feature`, `duplicate`, `enhancement`, `invalid`, `question`
