package onevnl.ru.elytrya.models;

import java.util.UUID;

public record BoostyUser(UUID uuid, String playerName, String boostyName, String levelName) {}