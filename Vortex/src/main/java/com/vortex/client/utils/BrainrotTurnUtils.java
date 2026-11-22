package com.vortex.client.utils;

import java.util.List;
import com.vortex.client.utils.WarmBrainrotBase;
import com.vortex.client.utils.TungPupil;
import com.vortex.client.utils.ShinThigh;
import com.vortex.client.utils.LidBombardiro;
import com.vortex.client.utils.SloshFore;
import com.vortex.client.utils.BrrFore;
import com.vortex.client.utils.ArmTrunk;
import com.vortex.client.utils.EarChimpanzini;
import com.vortex.client.utils.TailKatana;
import com.vortex.client.utils.FingerClaw;
import com.vortex.client.utils.FootBombombini;
import com.vortex.client.utils.AmbalabuBoneca;
import com.vortex.client.utils.LidRing;
import com.vortex.client.utils.ShinFoot;
import com.vortex.client.utils.TailFore;
import com.vortex.client.utils.LegBurbaloni;
import com.vortex.client.utils.ForeShinConst;
import com.vortex.client.utils.CrownFore;
import com.vortex.client.utils.ShinEar;
import com.vortex.client.utils.TailEar;
import com.vortex.client.utils.DinFore;
import com.vortex.client.utils.DolfinitaBananita;

public final class BrainrotTurnUtils {
   public static final List<WarmBrainrotBase> BRAINROT_TURN_ITEMS = List.of(
      new TungPupil(),
      new ShinThigh(),
      new LidBombardiro(),
      new SloshFore(),
      new BrrFore(),
      new ArmTrunk(),
      new EarChimpanzini(),
      new TailKatana(),
      new FingerClaw(),
      new FootBombombini(),
      new AmbalabuBoneca(),
      new LidRing(),
      new ShinFoot(),
      new TailFore(),
      new LegBurbaloni(),
      new ForeShinConst(),
      new CrownFore(),
      new ShinEar(),
      new TailEar(),
      new DinFore(),
      new DolfinitaBananita()
   );

   public static String[] getBrainrotTurn() {
      String[] strings = new String[BRAINROT_TURN_ITEMS.size()];

      for (int i = 0; i < strings.length; i++) {
         strings[i] = BRAINROT_TURN_ITEMS.get(i).formatTungDinString();
      }

      return strings;
   }

   private BrainrotTurnUtils() {
   }

   public static int getBrainrotTurn(String brainrotTurnText) {
      for (int i = 0; i < BRAINROT_TURN_ITEMS.size(); i++) {
         if (BRAINROT_TURN_ITEMS.get(i).formatTungDinString().equals(brainrotTurnText)) {
            return i;
         }
      }

      return -1;
   }
}
