package com.vortex.client.utils;

import com.vortex.client.render.ListeningGlfw;

public interface GlfwListeningApi {
   boolean canGlfwListening(ListeningGlfw listeningGlfw);

   void handleListeningGlfw(ListeningGlfw listeningGlfw);
}
