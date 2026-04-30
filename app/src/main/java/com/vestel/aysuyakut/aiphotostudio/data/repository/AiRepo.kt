package com.vestel.aysuyakut.aiphotostudio.data.repository

import android.net.Uri

interface AiRepo {
   suspend fun applyEffect(effect: String, image: Uri): Uri?
   suspend fun applyPrompt(prompt: String, source: Uri): Uri?

}
