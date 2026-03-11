
# BoostyBridge

[![Version](https://img.shields.io/badge/version-a0.3-orange)](#)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![CodeFactor](https://www.codefactor.io/repository/github/elytrya/boostybridge/badge)](https://www.codefactor.io/repository/github/elytrya/boostybridge)
[![Language](https://img.shields.io/badge/language-Java_17+-red)](#)
[![Platform](https://img.shields.io/badge/platform-Spigot_|_Paper_1.21+-green)](https://papermc.io/) <br>
[![GitHub latest commit](https://badgen.net/github/last-commit/Elytrya/BoostyBridge)](https://GitHub.com/Elytrya/BoostyBridge/commit/)
[![GitHub branches](https://badgen.net/github/branches/Elytrya/BoostyBridge)](https://github.com/Elytrya/BoostyBridge/)
[![GitHub commits](https://badgen.net/github/commits/Elytrya/BoostyBridge)](https://GitHub.com/Elytrya/BoostyBridge/commit/)
[![GitHub issues](https://badgen.net/github/issues/Elytrya/BoostyBridge/)](https://GitHub.com/Elytrya/BoostyBridge/issues/)

[🇷🇺 Русский](#русский) | [🇬🇧 English](#english)

[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/plugin/boostybridge)
[![Hangar](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/hangar_vector.svg)](https://hangar.papermc.io/Elytrya/BoostyBridge)
[![Spigot](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/spigot_vector.svg)](https://www.spigotmc.org/resources/boostybridge.133270/)
---

## Русский

**BoostyBridge** — это плагин с открытым исходным кодом для серверов Minecraft, предоставляющий интеграцию с платформой Boosty.to. Плагин автоматизирует выдачу и снятие игровых привилегий за подписку на Boosty.

### Основные фишки

* **Умная защита от кражи наград:** Проверка владения аккаунтом через Email (скрыто от других игроков).
* **Автоматическая синхронизация:** Фоновая проверка актуальности подписок и выполнение команд на снятие прав.
* **Поддержка PlaceholderAPI:** Вывод данных о подписке в любые плагины (TAB, Scoreboard и т.д.).
* **Метрики bStats:** Сбор анонимной статистики (отключается в конфиге: `bstats: false`).
* **Базы данных:** Поддержка SQLite (по умолчанию) и MySQL для сетей серверов.
* **Глобальные оповещения:** Красивые поздравления при оформлении подписки.

### Плейсхолдеры (PlaceholderAPI)

| Плейсхолдер | Описание |
| :--- | :--- |
| `%boosty_global_subscribers%` | Общее количество подписчиков в профиле. |
| `%boosty_level%` | Название уровня подписки игрока (или "None"). |
| `%boosty_name%` | Имя пользователя на Boosty. |
| `%boosty_is_linked%` | Привязан ли аккаунт (`true`/`false`). |
| `%boosty_has_sub%` | Есть ли активная подписка (`true`/`false`). |

### Команды и права

| Команда | Описание | Права |
| :--- | :--- | :--- |
| `/boosty link <ник>` | Привязать аккаунт Boosty. | Доступно всем |
| `/boosty info` | Узнать статус своей привязки. | Доступно всем |
| `/boosty reload` | Перезагрузить конфиг плагина. | `boosty.admin` |
| `/boosty admin info <игрок>` | Проверить данные игрока. | `boosty.admin` |
| `/boosty admin unlink <игрок>` | Отвязать аккаунт игрока. | `boosty.admin` |
| `/boosty admin forcelink <игрок> <ник>` | Привязать аккаунт принудительно. | `boosty.admin` |

### todo
- [x] Интеграция с PlaceholderAPI.
- [ ] Альтернативная верификация через личные сообщения Boosty.
- [ ] Команда ручной синхронизации (`/boosty admin forcesync`).
- [ ] Улучшенная обработка ошибок API и лимитов запросов.
- [ ] Оптимизация кэша и работа с нестандартными символами.

---

## English

**BoostyBridge** is an open-source plugin for Minecraft servers that provides integration with the Boosty.to platform. The plugin automates the process of giving and taking away in-game rewards for Boosty subscriptions.

### Main Features

* **Smart Reward Protection:** Email verification to confirm account ownership (hidden from others).
* **Automatic Synchronization:** Background checks for active subscriptions and automatic perk removal.
* **PlaceholderAPI Support:** Display Boosty data in other plugins like TAB or Scoreboards.
* **bStats Metrics:** Anonymous data collection (can be disabled in `config.yml` via `bstats: false`).
* **Database Support:** SQLite (default) and MySQL support for server networks.
* **Global Announcements:** Customizable broadcast messages when a player links a subscription.

### Placeholders (PlaceholderAPI)

| Placeholder | Description |
| :--- | :--- |
| `%boosty_global_subscribers%` | Total count of active subscribers on the profile. |
| `%boosty_level%` | Subscription level name (returns "None" if no sub). |
| `%boosty_name%` | User's Boosty display name. |
| `%boosty_is_linked%` | Account linkage status (`true`/`false`). |
| `%boosty_has_sub%` | Whether the player has any active sub (`true`/`false`). |

### Commands & Permissions

| Command | Description | Permission |
| --- | --- | --- |
| `/boosty link <name>` | Link a Boosty account. | None |
| `/boosty info` | Check your link status. | None |
| `/boosty reload` | Reload the plugin config. | `boosty.admin` |
| `/boosty admin info <player>` | Check a player's data. | `boosty.admin` |
| `/boosty admin unlink <player>` | Unlink a player's account. | `boosty.admin` |
| `/boosty admin forcelink <player> <name>` | Force link an account. | `boosty.admin` |

### todo

* [x] PlaceholderAPI integration.
* [ ] Alternative verification via Boosty Direct Messages.
* [ ] Manual synchronization command (`/boosty admin forcesync`).
* [ ] Improved API error handling and rate limit protection.
* [ ] General optimization and special character handling.

### Build Instructions

```bash
git clone [https://github.com/Elytrya/BoostyBridge](https://github.com/Elytrya/BoostyBridge)
cd BoostyBridge
mvn clean package

```

### License

This project is licensed under the **GPL-3.0 License**.

