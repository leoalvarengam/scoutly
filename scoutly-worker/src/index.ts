import amqp from "amqplib";
import { ScraperEngine } from "./core/ScraperEngine.js";

async function startWorker() {
  const queue = "scoutly.scraping.queue";
  const rabbitUrl = "amqp://scoutly_user:scoutly_pass@localhost:5672";

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
