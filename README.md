
# BoostyBridge

[![Version](https://img.shields.io/badge/version-a0.1-orange)](#)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![CodeFactor](https://www.codefactor.io/repository/github/elytrya/boostybridge/badge)](https://www.codefactor.io/repository/github/elytrya/boostybridge)
[![Language](https://img.shields.io/badge/language-Java_17+-red)](#)
[![Platform](https://img.shields.io/badge/platform-Spigot_|_Paper_1.21+-green)](https://papermc.io/) <br>
[![GitHub latest commit](https://badgen.net/github/last-commit/Elytrya/BoostyBridge)](https://GitHub.com/Eltyrya/BoostyBridge/commit/)
[![GitHub branches](https://badgen.net/github/branches/Elytrya/BoostyBridge)](https://github.com/Elytrya/BoostyBridge/)
[![GitHub commits](https://badgen.net/github/commits/Elytrya/BoostyBridge)](https://GitHub.com/Elytrya/BoostyBridge/commit/)
[![GitHub issues](https://badgen.net/github/issues/Elytrya/BoostyBridge/)](https://GitHub.com/Elytrya/BoostyBridge/issues/)

[🇷🇺 Русский](#русский) | [🇬🇧 English](#english)

[![](https://cd1n.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/plugin/boostybridge)
[![Hangar](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/hangar_vector.svg)](https://hangar.papermc.io/Elytrya/BoostyBridge)
[![Spigot](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/spigot_vector.svg)](https://www.spigotmc.org/resources/boostybridge.133270/)
---

## Русский

**BoostyBridge** — это плагин с открытым исходным кодом для серверов Minecraft, предоставляющий интеграцию с платформой Boosty.to. Плагин автоматизирует выдачу и снятие игровых привилегий за подписку на Boosty.

### Основные фишки

* **Умная защита от кражи наград:** Плагин проверяет, действительно ли игрок владеет аккаунтом на Boosty. Если в профиле Boosty указан Email, плагин попросит игрока написать его в чат (сообщение никто не увидит). 
  * *Как отключить:* Если вам это не нужно, просто установите `verify_email: false` в конфиге.
* **Автоматическая синхронизация:** Плагин сам проверяет актуальность подписок в фоновом режиме. Если игрок перестал платить, плагин автоматически выполнит команды на снятие прав. 
  * *Как настроить:* Интервал проверок меняется в `sync.interval_minutes`.
* **Авто-обновление токенов:** Вам нужно вставить данные для авторизации всего один раз. Плагин сам обновляет токены доступа, чтобы связь с Boosty не прерывалась.
* **Базы данных под любой сервер:** По умолчанию используется локальная база SQLite, не требующая настройки. Для крупных сетей серверов есть поддержка MySQL.
* **Глобальные оповещения:** При успешной подписке плагин может отправить красивое поздравление на весь сервер. 
  * *Как отключить:* Настраивается индивидуально для каждого уровня подписки через параметр `congratulation: false`.

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
- [ ] Интеграция с PlaceholderAPI (вывод уровня подписки и ника: `%boosty_level%`, `%boosty_name%`).
- [ ] Альтернативная верификация через личные сообщения Boosty (отправка уникального кода подтверждения в чат подписчику).
- [ ] Команда ручной синхронизации (`/boosty admin forcesync`) для обновления списка подписок прямо сейчас без ожидания таймера.
- [ ] Больше админ команд.
- [ ] Улучшенная обработка ошибок API (защита от лимитов запросов Бусти, понятные предупреждения в консоль, если токен окончательно истек).
- [ ] Оптимизация кэша и фикс мелких багов (работа с нестандартными символами в никах, улучшение безопасности).

### Сборка
Для компиляции плагина необходимы Java 17+ и Maven.
```bash
git clone https://github.com/Elytrya/BoostyBridge
cd BoostyBridge
mvn clean package

```

Готовый `.jar` файл появится в папке `target/`.

### Лицензия

Проект распространяется по лицензии **GPL-3.0**. Вы можете свободно использовать, изменять и распространять код при условии сохранения открытого исходного кода.

---

## English

**BoostyBridge** is an open-source plugin for Minecraft servers that provides integration with the Boosty.to platform. The plugin automates the process of giving and taking away in-game rewards for Boosty subscriptions.

### Main Features

* **Smart Reward Protection:** The plugin verifies if a player actually owns the Boosty account. If an Email is linked to the Boosty profile, the plugin will ask the player to type it into the chat (the message is hidden from others).
* *How to disable:* Set `verify_email: false` in the config.


* **Automatic Synchronization:** The plugin checks subscriptions in the background. If a player cancels their subscription, the plugin automatically executes commands to remove their perks.
* *How to configure:* Change the check interval via `sync.interval_minutes`.


* **Auto-updating Tokens:** You only need to paste your authorization data once. The plugin automatically refreshes access tokens to keep the connection alive.
* **Database Support:** Uses a local SQLite database by default (no setup required). MySQL is also supported for larger networks.
* **Global Announcements:** The plugin can broadcast a customizable congratulation message to the entire server when someone links a subscription.
* *How to disable:* Can be toggled for each subscription tier individually using `congratulation: false`.



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

* [ ] PlaceholderAPI integration (e.g., `%boosty_level%`, `%boosty_name%`).
* [ ] Alternative verification via Boosty Direct Messages (sending a unique confirmation code to the subscriber's chat).
* [ ] Manual synchronization command (`/boosty admin forcesync`) to update all subscriptions immediately without waiting for the timer.
* [ ] More admin commands.
* [ ] Improved API error handling (Boosty rate limit protection, clear console warnings if the authorization token completely expires).
* [ ] General optimization and bug fixes (handling special characters in nicknames, security improvements).

### Build Instructions

To compile the plugin, you need Java 17+ and Maven installed.

```bash
git clone https://github.com/Elytrya/BoostyBridge
cd BoostyBridge
mvn clean package

```

The compiled `.jar` file will be located in the `target/` directory.



### License

This project is licensed under the **GPL-3.0 License**. You are free to use, modify, and distribute the code, provided that the source code remains open.

```

```
