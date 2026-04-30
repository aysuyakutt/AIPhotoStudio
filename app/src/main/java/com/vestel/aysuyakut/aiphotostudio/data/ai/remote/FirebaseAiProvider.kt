package com.vestel.aysuyakut.aiphotostudio.data.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai

import com.google.firebase.ai.type.GenerativeBackend

object FirebaseAiProvider {

    // Varsayılan: Gemini Developer API (ücretsiz katman)
    fun gemini(modelName: String = "gemini-2.5-flash"): GenerativeModel =
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(modelName)


}
