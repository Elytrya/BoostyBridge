package onevnl.ru.elytrya.models;

public record PendingLink(
        String boostyName,
        String levelName,
        String verificationValue,  
        String verificationType    
) {}