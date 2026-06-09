import { CheerioAPI } from "cheerio";

export interface IScraperStrategy {
  extractPrice($: CheerioAPI): number | null;
}
