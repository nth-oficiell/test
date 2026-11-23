package com.matrix.client.utils;

import java.util.List;
import com.matrix.client.utils.WarmBrainrotBase;
import com.matrix.client.utils.TungPupil;
import com.matrix.client.utils.ShinThigh;
import com.matrix.client.utils.LidBombardiro;
import com.matrix.client.utils.SloshFore;
import com.matrix.client.utils.BrrFore;
import com.matrix.client.utils.ArmTrunk;
import com.matrix.client.utils.EarChimpanzini;
import com.matrix.client.utils.TailKatana;
import com.matrix.client.utils.FingerClaw;
import com.matrix.client.utils.FootBombombini;
import com.matrix.client.utils.AmbalabuBoneca;
import com.matrix.client.utils.LidRing;
import com.matrix.client.utils.ShinFoot;
import com.matrix.client.utils.TailFore;
import com.matrix.client.utils.LegBurbaloni;
import com.matrix.client.utils.ForeShinConst;
import com.matrix.client.utils.CrownFore;
import com.matrix.client.utils.ShinEar;
import com.matrix.client.utils.TailEar;
import com.matrix.client.utils.DinFore;
import com.matrix.client.utils.DolfinitaBananita;

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
