package com.leonardoalvarenga.scoutly.notification;

import java.math.BigDecimal;

public record PriceAlertDTO(
        String userEmail,
        String userName,
        String productName,
        String productUrl,
        BigDecimal targetPrice,
        BigDecimal currentPrice
) {
}
