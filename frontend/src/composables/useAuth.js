import { ref } from 'vue'

const user = ref(null)
const userUpdated = ref(0)

export function useAuth() {
  return {
    user,
    userUpdated,
    refreshUser: () => {
      userUpdated.value++
    },
    setUser: (u) => {
      user.value = u
    }
  }
}

export function emitUserUpdate() {
  window.dispatchEvent(new CustomEvent('user-updated'))
}

export function onUserUpdate(callback) {
  window.addEventListener('user-updated', callback)
}
