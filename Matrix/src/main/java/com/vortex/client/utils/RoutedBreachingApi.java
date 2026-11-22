package com.vortex.client.utils;
import com.vortex.client.utils.AnchorRoutePlanner;
import com.vortex.client.utils.PlayerCombatState;

public interface RoutedBreachingApi {
   float getRemainingRouteHp();

   double getEstimatedTicksRemaining();

   int getMaxDrop();

   AnchorRoutePlanner.Mode getRouteMode();

   int getTimeUnreachable();

   PlayerCombatState getImmuneFire();
}
