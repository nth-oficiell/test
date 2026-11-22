package com.vortex.client.utils;

import com.vortex.client.world.CenterVecData;
import com.vortex.client.utils.DefendEngageType;
import com.vortex.client.utils.DefensiveAggressiveType;
import com.vortex.client.utils.SelfHazData;
import com.vortex.client.utils.SurroundKitType;
import com.vortex.client.utils.DanceChaseType;
import com.vortex.client.utils.ForceTimedType;
import com.vortex.client.utils.RefBareData;

public record ComboTradeData(
   DefendEngageType mode,
   DefensiveAggressiveType posture,
   SelfHazData weights,
   SurroundKitType lane,
   DanceChaseType stance,
   boolean dodge,
   float minDamage,
   float selfWeight,
   float maxSelfDamage,
   boolean allowTotemTrade,
   ForceTimedType breaks,
   boolean anchorRelease,
   boolean restockWindow,
   String status,
   CenterVecData combo,
   boolean comboYield,
   RefBareData trade
) {
   public ComboTradeData(
      DefendEngageType mode,
      DefensiveAggressiveType posture,
      SelfHazData weights,
      SurroundKitType lane,
      DanceChaseType stance,
      boolean dodge,
      float minDamage,
      float selfWeight,
      float maxSelfDamage,
      boolean allowTotemTrade,
      ForceTimedType breaks,
      boolean anchorRelease,
      boolean restockWindow,
      String status,
      CenterVecData combo,
      boolean comboYield,
      RefBareData trade
   ) {
      trade = trade == null ? RefBareData.INSTANCE : trade;
      this.mode = mode;
      this.posture = posture;
      this.weights = weights;
      this.lane = lane;
      this.stance = stance;
      this.dodge = dodge;
      this.minDamage = minDamage;
      this.selfWeight = selfWeight;
      this.maxSelfDamage = maxSelfDamage;
      this.allowTotemTrade = allowTotemTrade;
      this.breaks = breaks;
      this.anchorRelease = anchorRelease;
      this.restockWindow = restockWindow;
      this.status = status;
      this.combo = combo;
      this.comboYield = comboYield;
      this.trade = trade;
   }

   public ComboTradeData(
      DefendEngageType comboYield,
      DefensiveAggressiveType breaks,
      SelfHazData anchorRelease,
      SurroundKitType dodge,
      DanceChaseType combo,
      boolean mode,
      float restockWindow,
      float allowTotemTrade,
      float status,
      boolean lane,
      ForceTimedType maxSelfDamage,
      boolean stance,
      boolean weights,
      String posture,
      CenterVecData minDamage,
      boolean selfWeight
   ) {
      this(
         comboYield,
         breaks,
         anchorRelease,
         dodge,
         combo,
         mode,
         restockWindow,
         allowTotemTrade,
         status,
         lane,
         maxSelfDamage,
         stance,
         weights,
         posture,
         minDamage,
         selfWeight,
         RefBareData.INSTANCE
      );
   }
}
