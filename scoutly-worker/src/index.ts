import dotenv from "dotenv";
dotenv.config();

import express from "express";
import amqp from "amqplib";
import { ScraperEngine } from "./core/ScraperEngine.js";

const app = express();
const PORT = process.env.PORT || 3000;

app.get("/", (_req, res) => {
  res.send("Worker está online e escutando a fila do RabbitMQ.");
});

app.listen(PORT, () => {
  console.log(`[*] Rodando na porta ${PORT}.`);
});

async function startWorker() {
  const queue = "scoutly.scraping.queue";
  const rabbitUrl = process.env.RABBITMQ_URL;
  const webhookSecret = process.env.SCOUTLY_WEBHOOK_SECRET;
  const backendUrl = process.env.BACKEND_URL;

  if (!webhookSecret) {
    throw new Error("SCOUTLY_WEBHOOK_SECRET não configurada.");
  }

  if (!rabbitUrl) {
    throw new Error("RABBITMQ_URL não configurada.");
  }

  try {
    const connection = await amqp.connect(rabbitUrl);
    const channel = await connection.createChannel();

    await channel.assertQueue(queue, { durable: true });

    console.log(
      `[*] Worker iniciado. Aguardando mensagens na fila '${queue}'...`,
    );

    const scraperEngine = new ScraperEngine();

    channel.consume(queue, async (msg) => {
      if (msg !== null) {
        try {
          const payload = JSON.parse(msg.content.toString());
          console.log(
            `\n[x] Mensagem recebida para o produto: ${payload.productId}`,
          );
          console.log(`URL alvo: ${payload.url}`);

          const price = await scraperEngine.processUrl(payload.url);
          console.log(`Preço encontrado: ${price}`);

          if (price === null) {
            console.log("[!] Preço não encontrado. Cancelando envio ao banco.");
            channel.ack(msg);
            return;
          }

          const response = await fetch(
            `${backendUrl}/api/tracking/webhook/price`,
            {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
                "X-Scoutly-Secret": webhookSecret,
              },
              body: JSON.stringify({
                productId: payload.productId,
                price: price,
              }),
            },
          );

          if (!response.ok) {
            const errorBody = await response.text();
            console.error(
              `\n[X] Spring Boot recusou o preço! Status: ${response.status}`,
            );
            console.error(`Motivo: ${errorBody}`);

            return;
          }

          console.log("[→] Preço enviado para o Spring Boot.");
          channel.ack(msg);
          console.log(
            `[v] Processamento concluído e mensagem removida da fila.`,
          );
        } catch (error) {
          console.error("Erro ao processar a mensagem:", error);
        }
      }
    });
  } catch (error) {
    console.error("Erro fatal ao conectar no RabbitMQ:", error);
  }
}

startWorker();
