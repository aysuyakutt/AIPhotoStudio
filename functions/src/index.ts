import {onCall} from "firebase-functions/v2/https";
import {initializeApp} from "firebase-admin/app";
import {GoogleGenerativeAI} from "@google/generative-ai";
import * as functions from "firebase-functions";

initializeApp();

export const enrichPrompt = onCall(
  {region: "europe-west1"},
  async (request) => {
    try {
      const GEMINI_KEY =
        process.env.GEMINI_KEY ||
        (functions.config().gemini?.key as string);

      if (!GEMINI_KEY) {
        throw new Error(
          "Gemini API key missing. " +
          "Set with: firebase functions:config:set gemini.key=..."
        );
      }

      const genAI = new GoogleGenerativeAI(GEMINI_KEY);

      const userPrompt = (request.data?.userPrompt as string) || "";
      const styleHint = (request.data?.styleHint as string) || "auto";

      const sys = [
        "You are a prompt engineer for Stable Diffusion 1.5 img2img.",
        "Return a concise enriched prompt; also include 'NEGATIVE: ...' line.",
        "Avoid verbosity; keep it production-ready.",
      ].join(" ");

      const input =
        `STYLE_HINT: ${styleHint}\nUSER_PROMPT: ${userPrompt}`;

      const model = genAI.getGenerativeModel({
        model: "gemini-1.5-flash",
      });

      const res = await model.generateContent([sys, input]);
      const text = res.response.text() || userPrompt;

      const negativeMatch = text.match(/NEGATIVE\s*:\s*(.*)/i);
      const negative =
        negativeMatch?.[1]?.trim() ??
        "blurry, lowres, watermark, text, deformed, " +
        "extra fingers, oversaturated";

      const cleaned = text
        .replace(/NEGATIVE\s*:\s*.*$/i, "")
        .trim();

      return {prompt: cleaned, negative};
    } catch (e: unknown) {
      console.error("enrichPrompt error:", e);
      if (e instanceof Error) {
        throw new Error(e.message);
      }
      throw new Error("Unknown error");
    }
  }
);
