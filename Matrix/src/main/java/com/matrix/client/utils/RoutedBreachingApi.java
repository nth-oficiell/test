package com.matrix.client.utils;
import com.matrix.client.utils.AnchorRoutePlanner;
import com.matrix.client.utils.PlayerCombatState;

public interface RoutedBreachingApi {
   float getRemainingRouteHp();

   double getEstimatedTicksRemaining();

   int getMaxDrop();

   AnchorRoutePlanner.Mode getRouteMode();

   int getTimeUnreachable();

   PlayerCombatState getImmuneFire();
}
