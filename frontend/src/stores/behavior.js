import { defineStore } from 'pinia'
import { ref } from 'vue'
import { behaviorApi } from '../api'

export const useBehaviorStore = defineStore('behavior', () => {
  function report(data) {
    behaviorApi.report(data)
  }

  function flush() {
    behaviorApi.flush()
  }

  return { report, flush }
})
