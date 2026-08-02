package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Compatibility shim for MultiBufferSource from older mappings.
 * Provides a simple no-op VertexConsumer so existing mod render code compiles against 26.2.
 * This is intentionally minimal and only intended to allow successful builds; visual fidelity
 * at runtime may differ and should be improved later by wiring to RenderBuffers/SectionBufferBuilderPack.
 */
public class MultiBufferSource {

    private static final VertexConsumer NOOP = new NoOpVertexConsumer();

    public VertexConsumer getBuffer(RenderType type) {
        return NOOP;
    }

    private static final class NoOpVertexConsumer implements VertexConsumer {
        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            return this;
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            return this;
        }

        @Override
        public VertexConsumer setColor(int color) {
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u1, int v1) {
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u2, int v2) {
            return this;
        }

        @Override
        public VertexConsumer setNormal(float nx, float ny, float nz) {
            return this;
        }

        @Override
        public VertexConsumer setLineWidth(float width) {
            return this;
        }

        // default helper methods from the VertexConsumer interface are left as-is (they have default implementations)
    }
}
