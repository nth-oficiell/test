package com.matrix.client.utils;

import com.matrix.client.render.ListeningGlfw;

public interface GlfwListeningApi {
   boolean canGlfwListening(ListeningGlfw listeningGlfw);

   void handleListeningGlfw(ListeningGlfw listeningGlfw);
}
