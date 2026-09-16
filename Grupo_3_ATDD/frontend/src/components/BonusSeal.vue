<script setup>
defineProps({
  earned: { type: Boolean, required: true },
  size: { type: String, default: 'lg' } // 'lg' | 'sm'
})
</script>

<template>
  <div class="seal" :class="[earned ? 'seal--earned' : 'seal--pending', `seal--${size}`]">
    <svg viewBox="0 0 64 64" class="seal__ring" aria-hidden="true">
      <circle cx="32" cy="32" r="29" />
      <circle cx="32" cy="32" r="23" v-if="earned" class="seal__ring-inner" />
    </svg>
    <div class="seal__glyph">
      <svg v-if="earned" viewBox="0 0 24 24" width="40%" height="40%" fill="none">
        <path d="M5 13l4 4L19 7" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" />
      </svg>
      <svg v-else viewBox="0 0 24 24" width="34%" height="34%" fill="none">
        <circle cx="12" cy="12" r="1.5" fill="currentColor" />
      </svg>
    </div>
  </div>
</template>

<style scoped>
.seal {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.seal--lg {
  width: 84px;
  height: 84px;
}

.seal--sm {
  width: 20px;
  height: 20px;
}

.seal__ring {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.seal__ring circle {
  fill: none;
  stroke-width: 3;
}

.seal--earned .seal__ring circle {
  stroke: var(--gold);
}

.seal--pending .seal__ring circle {
  stroke: var(--navy-600);
  stroke-dasharray: 4 5;
}

.seal__ring-inner {
  stroke-width: 1.5 !important;
  stroke: var(--gold-deep) !important;
  stroke-dasharray: none !important;
  opacity: 0.55;
}

.seal--earned {
  background: radial-gradient(circle at 35% 30%, rgba(199, 154, 62, 0.35), rgba(199, 154, 62, 0.08));
  color: var(--gold);
}

.seal--pending {
  color: var(--ink-text-muted);
}

.seal--sm.seal--earned {
  background: var(--gold);
  color: var(--navy-900);
}

.seal--sm .seal__glyph svg {
  width: 55%;
  height: 55%;
}
</style>
