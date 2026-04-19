# BoostyBridge

[![Version](https://img.shields.io/badge/version-b0.2-orange)](#)
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

**BoostyBridge** — это open-source плагин для серверов Minecraft с интеграцией Boosty.to. Он автоматически выдаёт и снимает игровые привилегии в зависимости от подписки пользователя.

### Основные возможности

* **Верификация через личные сообщения бусти:** Подтверждение привязки через код, отправленный в личные сообщения Boosty.
* **Discord Webhook:** Отправка событий (подписка, отвязка, истечение) в Discord.
* **Автоматическая синхронизация:** Проверка подписок в фоне и авто-снятие прав.
* **PlaceholderAPI:** Интеграция с TAB, scoreboards и другими плагинами.
* **Админ команды:** Принудительная отвязка, привязка, синхорнизация.
* **Базы данных:** SQLite (по умолчанию) и MySQL.

### Плейсхолдеры

| Плейсхолдер | Описание |
| :--- | :--- |
| `%boosty_global_subscribers%` | Общее число подписчиков |
| `%boosty_level%` | Уровень подписки |
| `%boosty_name%` | Ник на Boosty |
| `%boosty_is_linked%` | Привязан ли аккаунт |
| `%boosty_has_sub%` | Есть ли подписка |

### Команды

| Команда | Описание | Права |
| :--- | :--- | :--- |
| `/boosty link <ник>` | Привязка Boosty | Все |
| `/boosty info` | Проверка статуса | Все |
| `/boosty reload` | Перезагрузка | `boosty.admin` |
| `/boosty admin info <игрок>` | Информация об игроке | `boosty.admin` |
| `/boosty admin unlink <игрок>` | Отвязка | `boosty.admin` |
| `/boosty admin forcelink <игрок> <ник>` | Принудительная привязка | `boosty.admin` |
| `/boosty admin forcesync` | Принудительная синхронизация | `boosty.admin` |

### Верификация при привязке аккаунта бусти

Доступны несколько способов:

1. **Через личку бусти (основной)**
   - Пользователь получает код в личные сообщения
   - Вводит его на сервере

2. **Email (fallback)**
   - Используется, если в лс бусти отправить не удалось

### ✅ TODO

- [x] PlaceholderAPI
- [x] DM-верификация
- [x] Discord webhook
- [x] Ручная синхронизация
(если появились идеи - создайте issues)

---

## English

**BoostyBridge** is an open-source Minecraft plugin that integrates Boosty.to with your server. It automates reward management based on user subscriptions.

### Features

* **Secure Reward System:** Ownership verification via Email or Boosty DMs.
* **DM Verification:** Link accounts using a code sent via Boosty direct messages.
* **Discord Webhook Support:** Send events directly to Discord.
* **Automatic Sync:** Background subscription checks.
* **PlaceholderAPI Support**
* **bStats Metrics**
* **SQLite / MySQL support**

### Placeholders

| Placeholder | Description |
| :--- | :--- |
| `%boosty_global_subscribers%` | Total subscribers |
| `%boosty_level%` | Subscription level |
| `%boosty_name%` | Boosty username |
| `%boosty_is_linked%` | Linked status |
| `%boosty_has_sub%` | Has active sub |

### Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/boosty link <name>` | Link account | None |
| `/boosty info` | Check status | None |
| `/boosty reload` | Reload config | `boosty.admin` |
| `/boosty admin info <player>` | Player info | `boosty.admin` |
| `/boosty admin unlink <player>` | Unlink | `boosty.admin` |
| `/boosty admin forcelink <player> <name>` | Force link | `boosty.admin` |
| `/boosty admin forcesync` | Force sync | `boosty.admin` |

### Verification Methods

1. **Boosty DM (primary)**
2. **Email (fallback if DM fails)**

### TODO

- [x] PlaceholderAPI
- [x] DM verification
- [x] Discord webhook
- [x] Manual sync command

---

## Build

```bash
git clone https://github.com/Elytrya/BoostyBridge
cd BoostyBridge
mvn clean package
```


##  License
Licensed under GPL-3.0