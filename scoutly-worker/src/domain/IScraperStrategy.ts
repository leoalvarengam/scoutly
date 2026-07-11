import { CheerioAPI } from "cheerio";

export interface IScraperStrategy {
  executionType: "AXIOS" | "PUPPETEER" | "API";
  waitForSelector?: string;

  extractPrice($: CheerioAPI): number | null;
}
