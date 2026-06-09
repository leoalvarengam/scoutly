import * as cheerio from "cheerio";
import axios from "axios";

import { getStrategyForDomain } from "../strategies/StrategyRegistry";

export class ScraperEngine {
  async processUrl(url: string): Promise<number | null> {
    const domain = this.extractDomain(url);
    const strategy = getStrategyForDomain(domain);

    if (!strategy) {
      throw new Error("Loja não suportada");
    }

    let response;

    try {
      response = await axios.get(url);
    } catch (error) {
      console.error(error);
      throw error;
    }

    const $ = cheerio.load(response.data);

    return strategy.extractPrice($);
  }

  private extractDomain(url: string): string {
    const parsedUrl = new URL(url);

    return parsedUrl.hostname.replace("www.", "");
  }
}
