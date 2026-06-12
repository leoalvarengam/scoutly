package com.leonardoalvarenga.scoutly.messaging;

import java.util.UUID;

public record ScrapeProductMessage(
        UUID productId,
        String url
) {
}
