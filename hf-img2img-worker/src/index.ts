// src/index.ts
import { InferenceClient } from "npm:@huggingface/inference";

export default {
  async fetch(req: Request, env: { HF_TOKEN: string }) {
    try {
      if (req.method !== "POST") {
        return new Response("POST only", { status: 405 });
      }

      const form = await req.formData();

      const file = form.get("image") as File | null;
      if (!file) return new Response("image missing", { status: 400 });

      const prompt = (form.get("prompt") as string) ?? "";
      const negative_prompt = (form.get("negative_prompt") as string) ?? "";
      const strength = parseFloat((form.get("strength") as string) ?? "0.45");
      const guidance_scale = parseFloat((form.get("guidance_scale") as string) ?? "7");
      const num_inference_steps = parseInt((form.get("steps") as string) ?? "30", 10);
      const width = parseInt((form.get("width") as string) ?? "1024", 10);
      const height = parseInt((form.get("height") as string) ?? "1024", 10);

      // HF Inference Client
      const hf = new InferenceClient(env.HF_TOKEN);

      // Refiner yerine BASE model (img2img destekler)
      const model = "stabilityai/stable-diffusion-xl-base-1.0";
      // İstersen hızlı deneme:
      // const model = "stabilityai/sdxl-turbo";

      const blob = await hf.imageToImage({
        model,
        // provider'ı 'hf-inference' diyerek HF altyapısını zorunlu kılıyoruz.
        // (aksi halde provider seçimi yapmaya çalışır)
        provider: "hf-inference",
        inputs: file,
        parameters: {
          prompt,
          negative_prompt,
          strength,
          guidance_scale,
          num_inference_steps,
          width,
          height,
        },
      });

      return new Response(blob, {
        status: 200,
        headers: {
          "content-type": "image/png",
          "cache-control": "no-store",
        },
      });
    } catch (err: any) {
      const msg = (err && (err.message || err.toString())) ?? "unknown";
      return new Response(`Error: ${msg}`, { status: 500 });
    }
  },
} satisfies ExportedHandler;
