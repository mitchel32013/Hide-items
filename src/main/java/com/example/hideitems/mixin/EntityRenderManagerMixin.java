package com.example.hideitems.mixin;

import com.example.hideitems.HideItemsClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderManager.class)
public class EntityRenderManagerMixin {
	/**
	 * The game asks this before extracting render state and drawing an entity.
	 * Answering "no" for dropped items skips all of that work for them.
	 */
	@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
	private void hideitems$skipDroppedItems(Entity entity, Frustum frustum, double x, double y, double z,
											CallbackInfoReturnable<Boolean> cir) {
		if (HideItemsClient.isHidden() && entity instanceof ItemEntity) {
			cir.setReturnValue(false);
		}
	}
}
