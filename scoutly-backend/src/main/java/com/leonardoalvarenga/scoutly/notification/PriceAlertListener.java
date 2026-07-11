package com.leonardoalvarenga.scoutly.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceAlertListener {

    private final EmailService emailService;

    @RabbitListener(queues = "price.alerts.queue")
    public void processPriceAlert(PriceAlertDTO alert) {
        log.info("Processando alerta de preço para o produto: {} (E-mail: {})", alert.productName(), alert.userEmail());

        NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String formattedCurrent = formatCurrency.format(alert.currentPrice());
        String formattedTarget = formatCurrency.format(alert.targetPrice());

        String htmlBody = "<div style=\"font-family: 'Inter', Arial, sans-serif; max-width: 600px; margin: 0 auto; color: #333;\">"
                + "<h2 style=\"color: #10C07F;\">A hora chegou, " + alert.userName() + "! 🎉</h2>"
                + "<p>Boas notícias! O produto que você estava monitorando no <strong>Scoutly</strong> acabou de atingir o seu preço alvo.</p>"
                + "<div style=\"background-color: #f8fafc; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #10C07F;\">"
                + "<h3 style=\"margin-top: 0;\">" + alert.productName() + "</h3>"
                + "<p style=\"font-size: 1.2rem; margin-bottom: 5px;\">Preço atual: <strong style=\"color: #166534;\">" + formattedCurrent + "</strong></p>"
                + "<p style=\"font-size: 0.9rem; color: #64748b; margin-top: 0;\">Seu preço alvo era: " + formattedTarget + "</p>"
                + "</div>"
                + "<p>Não perca tempo, as promoções costumam acabar rápido:</p>"
                + "<a href=\"" + alert.productUrl() + "\" style=\"display: inline-block; padding: 12px 24px; background-color: #10C07F; color: #ffffff; text-decoration: none; border-radius: 6px; font-weight: bold; margin-top: 10px;\">Comprar Agora</a>"
                + "<p style=\"margin-top: 30px; font-size: 0.8rem; color: #94a3b8;\">Você está recebendo este e-mail porque cadastrou um alerta no seu painel.</p>"
                + "</div>";

        try {
            emailService.sendHtmlEmail(alert.userEmail(), "🚨 Queda de Preço: " + alert.productName(), htmlBody);
            log.info("Alerta enviado com sucesso para {}", alert.userEmail());
        } catch (Exception e) {
            log.error("Falha ao enviar alerta para {}: {}", alert.userEmail(), e.getMessage());
        }
    }
}
