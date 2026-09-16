<script setup>
import { ref } from 'vue'

defineProps({
  entries: { type: Array, required: true }
})

const open = ref(true)
</script>

<template>
  <section class="log" :class="{ 'log--collapsed': !open }">
    <button class="log__toggle" @click="open = !open" :aria-expanded="open">
      <span>Registro de chamadas</span>
      <span class="log__count">{{ entries.length }}</span>
      <span class="log__chevron">{{ open ? '▾' : '▸' }}</span>
    </button>
    <div v-if="open" class="log__body">
      <p v-if="entries.length === 0" class="log__empty">
        Nenhuma chamada ainda. Crie um aluno para começar a testar a API.
      </p>
      <div v-for="(e, i) in entries" :key="i" class="log__row" :class="`log__row--${e.level}`">
        <span class="log__time">{{ e.time }}</span>
        <span class="log__method">{{ e.method }}</span>
        <span class="log__path">{{ e.path }}</span>
        <span class="log__status">{{ e.status }}</span>
        <span class="log__msg">{{ e.message }}</span>
      </div>
    </div>
  </section>
</template>

<style scoped>
.log {
  border-top: 1px solid var(--navy-700);
  background: var(--navy-850);
  font-family: var(--font-mono);
  font-size: 12.5px;
}

.log__toggle {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 24px;
  background: none;
  border: none;
  color: var(--ink-text-muted);
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 500;
}

.log__toggle:hover {
  color: var(--ink-text);
}

.log__count {
  background: var(--navy-700);
  color: var(--ink-text-muted);
  border-radius: 999px;
  padding: 1px 8px;
  font-size: 11px;
}

.log__chevron {
  margin-left: auto;
  color: var(--ink-text-muted);
}

.log__body {
  max-height: 220px;
  overflow-y: auto;
  padding: 0 24px 14px;
}

.log__empty {
  color: var(--ink-text-muted);
  font-family: var(--font-body);
  margin: 4px 0 10px;
}

.log__row {
  display: grid;
  grid-template-columns: 78px 46px 1fr auto;
  gap: 10px;
  padding: 5px 0;
  border-bottom: 1px solid var(--navy-800);
  color: var(--ink-text-muted);
  align-items: baseline;
}

.log__row:last-child {
  border-bottom: none;
}

.log__time {
  color: var(--navy-600);
}

.log__method {
  color: var(--gold);
  font-weight: 500;
}

.log__path {
  color: var(--ink-text);
}

.log__status {
  justify-self: end;
}

.log__msg {
  grid-column: 1 / -1;
  color: var(--ink-text-muted);
  padding-left: 88px;
}

.log__row--error .log__status,
.log__row--error .log__msg {
  color: var(--clay);
}

.log__row--ok .log__status {
  color: var(--gold);
}
</style>
