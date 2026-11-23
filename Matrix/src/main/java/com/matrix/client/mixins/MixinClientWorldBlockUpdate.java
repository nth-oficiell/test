package com.matrix.client.mixins;

import com.matrix.client.render.BlockStateHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.matrix.client.categories.bots.AutoBuilder;
import com.matrix.client.categories.donutsmp.Bloodhound;
import com.matrix.client.categories.donutsmp.ChunkFinder;
import com.matrix.client.categories.donutsmp.ChunkFinderV2;
import com.matrix.client.categories.visuals.HoleESP;
import com.matrix.client.categories.visuals.SpawnerFinder;

@Mixin({ClientWorld.class})
public class MixinClientWorldBlockUpdate {
   @Inject(
      method = {"method_41928(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;I)V"},
      at = {@At("HEAD")}
   )
   private void matrixChunkFinderBlockUpdate(BlockPos pos2, BlockState blockState, int matrixChunkValue, CallbackInfo callbackInfo) {
      ClientWorld self = (ClientWorld)((net.minecraft.client.world.ClientWorld) (Object) (this));
      BlockState oldState = self.getBlockState(pos2);
      ChunkFinder.onBlockStateChanged(self, pos2, oldState, blockState);
      ChunkFinderV2.onBlockStateChanged(pos2, oldState, blockState);
      Bloodhound.onBlockStateChanged(pos2, oldState, blockState);
      HoleESP.handleScanRender(pos2, oldState, blockState);
      SpawnerFinder.handleInstance(self, pos2, oldState, blockState);
      AutoBuilder.recordBlockStateChange(pos2, oldState, blockState);
      if (oldState != blockState) {
         BlockStateHelper.handleHashOpenForBlockPos(pos2);
      }
   }

   @Inject(
      method = {"method_41926(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_243;)V"},
      at = {@At("HEAD")}
   )
   private void matrixServerBlockAck(BlockPos pos2, BlockState blockState, Vec3d vec, CallbackInfo callbackInfo) {
      AutoBuilder.recordTickedBlockState(pos2, blockState);
   }
}
